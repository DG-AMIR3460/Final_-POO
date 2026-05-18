package core.model.repositories;

import core.model.entities.Appointment;
import core.model.observers.Observable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion del repositorio de citas.
 * Los controladores dependen de esta interfaz (Dependency Inversion Principle).
 * Nuevas implementaciones pueden agregarse sin modificar los controladores
 * (Open/Closed Principle).
 */
public interface IAppointmentRepository extends Observable {

    String generateId(long patientId);

    boolean add(Appointment appointment);

    Optional<Appointment> findById(String id);

    List<Appointment> getAll();

    List<Appointment> getByPatientSortedDesc(long patientId);

    List<Appointment> getByDoctorSortedDesc(long doctorId);

    List<Appointment> getByDoctorPendingSortedDesc(long doctorId);
}
