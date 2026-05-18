package main.model.repositories;

import main.model.entities.Hospitalization;
import main.model.observers.Observable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion del repositorio de hospitalizaciones.
 * Los controladores dependen de esta interfaz (Dependency Inversion Principle).
 * Nuevas implementaciones pueden agregarse sin modificar los controladores
 * (Open/Closed Principle).
 */
public interface IHospitalizationRepository extends Observable {

    String generateId(long patientId);

    boolean add(Hospitalization hospitalization);

    Optional<Hospitalization> findById(String id);

    List<Hospitalization> getAll();

    List<Hospitalization> getByPatient(long patientId);

    List<Hospitalization> getByDoctor(long doctorId);
}
