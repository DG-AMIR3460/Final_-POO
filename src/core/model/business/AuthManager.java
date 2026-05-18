package core.model.business;

import core.model.entities.User;
import core.model.repositories.IUserRepository;

import java.util.Optional;

/**
 * Logica de negocio de autenticacion.
 *
 * S - Single Responsibility: unica responsabilidad es validar credenciales.
 * D - Dependency Inversion: depende de IUserRepository (interfaz), nunca de UserRepository directamente.
 */
public class AuthManager {

    private final IUserRepository userRepository;

    public AuthManager(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Busca un usuario por nombre de usuario. */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** Regla de negocio: verifica que la contrasena coincida. */
    public boolean verifyPassword(User user, String password) {
        return user.getPassword().equals(password);
    }
}
