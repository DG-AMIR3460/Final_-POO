package main.controller;

import main.model.entities.Patient;
import main.model.entities.User;
import main.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

public class PatientController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@]+@[^@]+\\.com$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern DATE_PATTERN  = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private final IUserRepository userRepository;

    public PatientController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response register(String idStr, String username, String firstname, String lastname,
                             String password, String confirm, String email,
                             String birthdateStr, String genderStr, String phoneStr, String address) {
        Response v = validatePatientData(null, idStr, username, firstname, lastname,
                password, confirm, email, birthdateStr, genderStr, phoneStr, address);
        if (!v.isOk()) return v;

        long id = Long.parseLong(idStr);
        LocalDate birthdate = LocalDate.parse(birthdateStr);
        long phone = Long.parseLong(phoneStr);
        boolean gender = "Male".equalsIgnoreCase(genderStr);

        Patient patient = new Patient(id, username, firstname, lastname, password,
                email, birthdate, gender, phone, address);
        userRepository.add(patient);
        return new Response(StatusCode.OK, "Patient registered successfully.");
    }

    public Response update(long currentUserId, String username, String firstname, String lastname,
                           String password, String confirm, String email,
                           String birthdateStr, String genderStr, String phoneStr, String address) {
        Optional<User> opt = userRepository.findById(currentUserId);
        if (opt.isEmpty() || !(opt.get() instanceof Patient p)) {
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");
        }
        Response v = validatePatientData(currentUserId, String.valueOf(p.getId()), username, firstname,
                lastname, password, confirm, email, birthdateStr, genderStr, phoneStr, address);
        if (!v.isOk()) return v;

        if (!p.getUsername().equals(username) && userRepository.usernameExists(username)) {
            return new Response(StatusCode.CONFLICT, "Username already taken.");
        }

        p.setUsername(username);
        p.setFirstname(firstname);
        p.setLastname(lastname);
        p.setPassword(password);
        p.setEmail(email);
        p.setBirthdate(LocalDate.parse(birthdateStr));
        p.setGender("Male".equalsIgnoreCase(genderStr));
        p.setPhone(Long.parseLong(phoneStr));
        p.setAddress(address);
        userRepository.notifyObservers();
        return new Response(StatusCode.OK, "Patient info updated successfully.");
    }

    public Response getInfo(long patientId) {
        Optional<User> opt = userRepository.findById(patientId);
        if (opt.isEmpty() || !(opt.get() instanceof Patient p)) {
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");
        }
        JSONObject data = serializePatient(p);
        return new Response(StatusCode.OK, "OK", data);
    }

    public Response getAllPatientsJson() {
        JSONArray arr = new JSONArray();
        userRepository.getPatients().forEach(p -> arr.put(serializePatient(p)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    private Response validatePatientData(Long currentId, String idStr, String username,
                                         String firstname, String lastname,
                                         String password, String confirm,
                                         String email, String birthdateStr,
                                         String genderStr, String phoneStr, String address) {
        if (idStr == null || idStr.isBlank())         return new Response(StatusCode.BAD_REQUEST, "ID is required.");
        if (username == null || username.isBlank())   return new Response(StatusCode.BAD_REQUEST, "Username is required.");
        if (firstname == null || firstname.isBlank()) return new Response(StatusCode.BAD_REQUEST, "Firstname is required.");
        if (lastname == null || lastname.isBlank())   return new Response(StatusCode.BAD_REQUEST, "Lastname is required.");
        if (password == null || password.isBlank())   return new Response(StatusCode.BAD_REQUEST, "Password is required.");
        if (!password.equals(confirm))                return new Response(StatusCode.BAD_REQUEST, "Passwords do not match.");

        long id;
        try { id = Long.parseLong(idStr); } catch (NumberFormatException e) {
            return new Response(StatusCode.BAD_REQUEST, "ID must be numeric.");
        }
        if (id <= 0 || String.valueOf(id).length() != 12)
            return new Response(StatusCode.BAD_REQUEST, "ID must be a positive 12-digit number.");

        if (currentId == null && userRepository.idExists(id))
            return new Response(StatusCode.CONFLICT, "ID already in use.");

        if (currentId == null && userRepository.usernameExists(username))
            return new Response(StatusCode.CONFLICT, "Username already taken.");

        if (!EMAIL_PATTERN.matcher(email).matches())
            return new Response(StatusCode.BAD_REQUEST, "Invalid email format. Must be XXXXX@XXXXX.com");

        if (!PHONE_PATTERN.matcher(phoneStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Phone must have exactly 10 digits.");

        if (!DATE_PATTERN.matcher(birthdateStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Birthdate must follow YYYY-MM-DD format.");
        try { LocalDate.parse(birthdateStr); } catch (DateTimeParseException e) {
            return new Response(StatusCode.BAD_REQUEST, "Birthdate is not a valid date.");
        }

        if ("Select one".equalsIgnoreCase(genderStr))
            return new Response(StatusCode.BAD_REQUEST, "Gender is required.");

        return new Response(StatusCode.OK, "OK");
    }

    public static JSONObject serializePatient(Patient p) {
        JSONObject o = new JSONObject();
        o.put("id", p.getId());
        o.put("username", p.getUsername());
        o.put("firstname", p.getFirstname());
        o.put("lastname", p.getLastname());
        o.put("email", p.getEmail());
        o.put("birthdate", p.getBirthdate().toString());
        o.put("gender", p.isGender() ? "Male" : "Female");
        o.put("phone", p.getPhone());
        o.put("address", p.getAddress());
        return o;
    }
}
