package core.model.business;

import core.model.entities.Appointment;
import core.model.entities.Doctor;
import core.model.entities.Patient;
import core.model.enums.AppointmentStatus;
import core.model.enums.Specialty;
import core.model.observers.ModelObserver;
import core.model.repositories.IAppointmentRepository;
import core.model.repositories.IUserRepository;

import java.time.LocalDateTime;


public class AppointmentManager {

    // El Manager es la capa de negocio pura — no conoce vistas ni controllers, solo repositorios
    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;

    public AppointmentManager(IAppointmentRepository appointmentRepository,
                               IUserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    // Verifica solapamiento de intervalos de 15 minutos — dos citas se solapan si [s1, e1) y [s2, e2) se intersectan
    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime requested) {
        return isDoctorAvailable(doctor, requested, null);
    }

    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime requested, Appointment ignoredAppointment) {
        LocalDateTime end = requested.plusMinutes(15);
        return doctor.getAppointments().stream()
                .filter(a -> ignoredAppointment == null || !a.getId().equals(ignoredAppointment.getId()))
                // Las citas canceladas o completadas no bloquean el horario del doctor
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELED
                          && a.getStatus() != AppointmentStatus.COMPLETED)
                .noneMatch(a -> {
                    LocalDateTime s = a.getDatetime();
                    LocalDateTime e = s.plusMinutes(15);
                    // Condición clásica de solapamiento de intervalos: A empieza antes que B termine, y B empieza antes que A termine
                    return requested.isBefore(e) && end.isAfter(s);
                });
    }

    /**
     * Regla: busca el primer doctor disponible con la especialidad solicitada.
     */
    public Doctor findAvailableDoctor(Specialty specialty, LocalDateTime dt) {
        // Reutiliza isDoctorAvailable para no duplicar la lógica de solapamiento
        return userRepository.getDoctors().stream()
                .filter(d -> d.getSpecialty() == specialty && isDoctorAvailable(d, dt))
                .findFirst().orElse(null);
    }

    // -------------------------------------------------------------------------
    // Transiciones de estado (reglas de negocio)
    // -------------------------------------------------------------------------

    /** REQUESTED → PENDING. Retorna false si el estado no permite la transicion. */
    public boolean accept(Appointment a) {
        if (a.getStatus() != AppointmentStatus.REQUESTED) return false;
        a.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.notifyObservers();
        return true;
    }

    /** PENDING → COMPLETED con datos clinicos. Retorna false si no esta en PENDING. */
    public boolean complete(Appointment a, String diagnosis, String observations,
                             String recommendedTreatment, String followUp) {
        if (a.getStatus() != AppointmentStatus.PENDING) return false;
        a.setStatus(AppointmentStatus.COMPLETED);
        // Los datos clínicos solo se asignan si la transición es válida — no hay setters sueltos en el Controller
        a.setDiagnosis(diagnosis);
        a.setObservations(observations);
        a.setRecommendedTreatment(recommendedTreatment);
        a.setFollowUp(followUp);
        appointmentRepository.notifyObservers();
        return true;
    }

    /** Cualquier estado (excepto COMPLETED) → CANCELED. */
    public boolean cancel(Appointment a) {
        // COMPLETED es el único estado terminal — una cita completada no se puede revertir
        if (a.getStatus() == AppointmentStatus.COMPLETED) return false;
        a.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.notifyObservers();
        return true;
    }

    /** Cambia la hora de una cita ya existente. */
    public void reschedule(Appointment a, LocalDateTime newDt, String rescheduleReason) {
        a.setDatetime(newDt);
        // El motivo de reagendamiento se concatena al reason original para mantener el historial en un solo campo
        if (rescheduleReason != null && !rescheduleReason.isBlank()) {
            a.setReason(a.getReason() + " | Rescheduled: " + rescheduleReason);
        }
        appointmentRepository.notifyObservers();
    }
    // Creacion
    
    /** Crea y persiste una cita medica. */
    public Appointment create(Patient patient, Doctor doctor, Specialty specialty,
                               LocalDateTime dt, String reason, boolean type) {
        // El ID lo genera el repositorio para encapsular la estrategia de identificación (no se hardcodea aquí)
        String id = appointmentRepository.generateId(patient.getId());
        Appointment a = new Appointment(id, patient, doctor, specialty, dt, reason, type);
        appointmentRepository.add(a);
        return a;
    }

    // Observer delegation
    // El Manager delega la suscripción al repositorio porque es quien tiene la lista de observadores
    public void addObserver(ModelObserver o)    { appointmentRepository.addObserver(o); }
    public void removeObserver(ModelObserver o) { appointmentRepository.removeObserver(o); }
}
