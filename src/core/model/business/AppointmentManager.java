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

    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;

    public AppointmentManager(IAppointmentRepository appointmentRepository,
                               IUserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

   
    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime requested) {
        LocalDateTime end = requested.plusMinutes(15);
        return doctor.getAppointments().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELED
                          && a.getStatus() != AppointmentStatus.COMPLETED)
                .noneMatch(a -> {
                    LocalDateTime s = a.getDatetime();
                    LocalDateTime e = s.plusMinutes(15);
                    return requested.isBefore(e) && end.isAfter(s);
                });
    }

    /**
     * Regla: busca el primer doctor disponible con la especialidad solicitada.
     */
    public Doctor findAvailableDoctor(Specialty specialty, LocalDateTime dt) {
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
        a.setDiagnosis(diagnosis);
        a.setObservations(observations);
        a.setRecommendedTreatment(recommendedTreatment);
        a.setFollowUp(followUp);
        appointmentRepository.notifyObservers();
        return true;
    }

    /** Cualquier estado (excepto COMPLETED) → CANCELED. */
    public boolean cancel(Appointment a) {
        if (a.getStatus() == AppointmentStatus.COMPLETED) return false;
        a.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.notifyObservers();
        return true;
    }

    /** Cambia la hora de una cita ya existente. */
    public void reschedule(Appointment a, LocalDateTime newDt, String rescheduleReason) {
        a.setDatetime(newDt);
        if (rescheduleReason != null && !rescheduleReason.isBlank()) {
            a.setReason(a.getReason() + " | Rescheduled: " + rescheduleReason);
        }
        appointmentRepository.notifyObservers();
    }

    // -------------------------------------------------------------------------
    // Creacion
    // -------------------------------------------------------------------------

    /** Crea y persiste una cita medica. */
    public Appointment create(Patient patient, Doctor doctor, Specialty specialty,
                               LocalDateTime dt, String reason, boolean type) {
        String id = appointmentRepository.generateId(patient.getId());
        Appointment a = new Appointment(id, patient, doctor, specialty, dt, reason, type);
        appointmentRepository.add(a);
        return a;
    }

    // -------------------------------------------------------------------------
    // Observer delegation
    // -------------------------------------------------------------------------

    public void addObserver(ModelObserver o)    { appointmentRepository.addObserver(o); }
    public void removeObserver(ModelObserver o) { appointmentRepository.removeObserver(o); }
}
