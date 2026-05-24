package core.model.business;

import core.model.entities.Appointment;
import core.model.entities.Doctor;
import core.model.entities.Hospitalization;
import core.model.entities.Patient;
import core.model.enums.AppointmentStatus;
import core.model.enums.HospitalizationStatus;
import core.model.enums.RoomType;
import core.model.observers.ModelObserver;
import core.model.repositories.IAppointmentRepository;
import core.model.repositories.IHospitalizationRepository;

import java.time.LocalDate;

/**
 * Logica de negocio de hospitalizaciones.
 *
 * S - Single Responsibility: solo maneja reglas de hospitalización.
 * D - Dependency Inversion: depende de interfaces, no de clases concretas.
 * O - Open/Closed: agregar nuevas reglas (ej. limite de camas) no toca los controladores.
 */
public class HospitalizationManager {

    private final IHospitalizationRepository hospitalizationRepository;
    private final IAppointmentRepository appointmentRepository;

    public HospitalizationManager(IHospitalizationRepository hospitalizationRepository,
                                   IAppointmentRepository appointmentRepository) {
        this.hospitalizationRepository = hospitalizationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Creacion (3 escenarios de negocio distintos)

    /**
     * Escenario 1: Paciente solicita hospitalizacion → estado REQUESTED (espera aprobacion del doctor).
     */
    public Hospitalization createRequest(Patient patient, Doctor doctor, LocalDate date,
                                          String reason, RoomType roomType, String observations) {
        String id = hospitalizationRepository.generateId(patient.getId());
        Hospitalization h = new Hospitalization(id, patient, doctor, date, reason, roomType, observations);
        hospitalizationRepository.add(h);
        return h;
    }

    /**
     * Escenario 2: Doctor crea hospitalizacion directa → estado ONGOING (sin aprobacion pendiente).
     */
    public Hospitalization createDirect(Patient patient, Doctor doctor, LocalDate date,
                                         String reason, RoomType roomType, String observations) {
        String id = hospitalizationRepository.generateId(patient.getId());
        Hospitalization h = new Hospitalization(id, patient, doctor, date, reason, roomType, observations,
                HospitalizationStatus.ONGOING);
        hospitalizationRepository.add(h);
        return h;
    }

    /**
     * Escenario 3: Hospitalizacion derivada de una cita → la cita se completa automaticamente + estado ONGOING.
     */
    public Hospitalization createFromAppointment(Appointment appointment, LocalDate date,
                                                   String reason, RoomType roomType, String observations) {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.notifyObservers();

        String id = hospitalizationRepository.generateId(appointment.getPatient().getId());
        Hospitalization h = new Hospitalization(id, appointment.getPatient(), appointment.getDoctor(),
                date, reason, roomType, observations, HospitalizationStatus.ONGOING);
        hospitalizationRepository.add(h);
        return h;
    }

    // Transiciones de estado (reglas de negocio)

    /** Regla: solo hospitalizaciones REQUESTED pueden aprobarse → pasan a ONGOING. */
    public boolean approve(Hospitalization h) {
        if (h.getStatus() != HospitalizationStatus.REQUESTED) return false;
        h.setStatus(HospitalizationStatus.ONGOING);
        hospitalizationRepository.notifyObservers();
        return true;
    }

    /** Regla: una hospitalizacion ya CANCELED no puede cancelarse de nuevo. */
    public boolean deny(Hospitalization h) {
        if (h.getStatus() == HospitalizationStatus.CANCELED) return false;
        h.setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.notifyObservers();
        return true;
    }

    // Observer delegation

    public void addObserver(ModelObserver o)    { hospitalizationRepository.addObserver(o); }
    public void removeObserver(ModelObserver o) { hospitalizationRepository.removeObserver(o); }
}
