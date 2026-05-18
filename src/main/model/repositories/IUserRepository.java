package main.model.repositories;

import main.model.entities.Doctor;
import main.model.entities.Patient;
import main.model.entities.User;
import main.model.observers.Observable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion del repositorio de usuarios.
 * Los controladores dependen de esta interfaz (Dependency Inversion Principle).
 * Nuevas implementaciones (BD, XML, etc.) pueden agregarse sin modificar los
 * controladores (Open/Closed Principle).
 */
public interface IUserRepository extends Observable {

    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    boolean usernameExists(String username);

    boolean idExists(long id);

    List<User> getAll();

    List<Doctor> getDoctors();

    List<Patient> getPatients();

    boolean add(User user);
}
