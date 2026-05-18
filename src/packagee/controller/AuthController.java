package packagee.controller;

import packagee.model.entities.User;
import packagee.model.repositories.IUserRepository;

import org.json.JSONObject;
import java.util.Optional;

public class AuthController {

    private final IUserRepository userRepository;

    public AuthController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new Response(StatusCode.BAD_REQUEST, "Username and password are required.");
        }
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            return new Response(StatusCode.NOT_FOUND, "User not found.");
        }
        User user = opt.get();
        if (!user.getPassword().equals(password)) {
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
