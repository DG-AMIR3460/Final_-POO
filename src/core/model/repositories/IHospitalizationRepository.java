package core.model.repositories;

import core.model.entities.Hospitalization;
import core.model.observers.Observable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion del repositorio de hospitalizaciones.
 * Los controladores dependen de esta interfaz (Dependency Inversion Principle).
 * Nuevas implementaciones pueden agregarse sin modificar los controladores
 * (Open/Closed Principle).
 */
// Extiende Observable para que la implementación concreta pueda notificar cambios a las vistas sin que la interfaz exponga los detalles del patrón
public interface IHospitalizationRepository extends Observable {
    // generateId forma parte del contrato porque la estrategia de identificación puede variar según la implementación
    String generateId(long patientId);
    boolean add(Hospitalization hospitalization);
    Optional<Hospitalization> findById(String id);
    List<Hospitalization> getAll();
    List<Hospitalization> getByPatient(long patientId);
    List<Hospitalization> getByDoctor(long doctorId);
}