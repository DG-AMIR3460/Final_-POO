package core.controller;

import core.model.business.AuthManager;
import core.model.entities.User;

import org.json.JSONObject;
import java.util.Optional;

/**
 * Conecta la vista de login con la logica de autenticacion.
 * S - Single Responsibility: solo gestiona el flujo de login.
 * D - Dependency Inversion: depende de AuthManager, no de repositorios directamente.
 */
public class AuthController {

    private final AuthManager authManager;

    public AuthController(AuthManager authManager) {
        this.authManager = authManager;
    }

    public Response login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new Response(StatusCode.BAD_REQUEST, "Username and password are required.");
        }
        Optional<User> opt = authManager.findByUsername(username);
        if (opt.isEmpty()) {
            return new Response(StatusCode.NOT_FOUND, "User not found.");
        }
        User user = opt.get();
        if (!authManager.verifyPassword(user, password)) {
            return new Response(StatusCode.BAD_REQUEST, "Incorrect password.");
        }
        JSONObject data = new JSONObject();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("firstname", user.getFirstname());
        data.put("lastname", user.getLastname());
        data.put("type", user.getClass().getSimpleName());
        return new Response(StatusCode.OK, "Login successful.", data);
    }
}
