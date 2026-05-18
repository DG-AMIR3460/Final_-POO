package packagee.controller;

import packagee.model.entities.Doctor;
import packagee.model.entities.User;
import packagee.model.enums.Specialty;
import packagee.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;
import java.util.regex.Pattern;

public class DoctorController {

    private static final Pattern LICENCE_PATTERN = Pattern.compile("^L-\\d{10} MTL$");
    private static final Pattern OFFICE_PATTERN  = Pattern.compile("^O-\\d{3}$");

    private final IUserRepository userRepository;

    public DoctorController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response register(String idStr, String username, String firstname, String lastname,
                             String password, String confirm, String specialtyDisplay,
                             String licenceNumber, String assignedOffice) {
        Response v = validateDoctorData(null, idStr, username, firstname, lastname,
                password, confirm, specialtyDisplay, licenceNumber, assignedOffice);
        if (!v.isOk()) return v;

        long id = Long.parseLong(idStr);
        Specialty specialty = Specialty.fromDisplayName(specialtyDisplay);
        Doctor doctor = new Doctor(id, username, firstname, lastname, password,
                specialty, licenceNumber, assignedOffice);
        userRepository.add(doctor);
        return new Response(StatusCode.OK, "Doctor registered successfully.");
    }

    public Response update(long currentDoctorId, String username, String firstname, String lastname,
                           String password, String confirm, String specialtyDisplay,
                           String licenceNumber, String assignedOffice) {
        Optional<User> opt = userRepository.findById(currentDoctorId);
        if (opt.isEmpty() || !(opt.get() instanceof Doctor d)) {
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");
        }
        Response v = validateDoctorData(currentDoctorId, String.valueOf(d.getId()), username, firstname,
                lastname, password, confirm, specialtyDisplay, licenceNumber, assignedOffice);
        if (!v.isOk()) return v;

        if (!d.getUsername().equals(username) && userRepository.usernameExists(username)) {
            return new Response(StatusCode.CONFLICT, "Username already taken.");
        }

        d.setUsername(username);
        d.setFirstname(firstname);
        d.setLastname(lastname);
        d.setPassword(password);
        d.setSpecialty(Specialty.fromDisplayName(specialtyDisplay));
        d.setLicenceNumber(licenceNumber);
        d.setAssignedOffice(assignedOffice);
        userRepository.notifyObservers();
        return new Response(StatusCode.OK, "Doctor info updated successfully.");
    }

    public Response getInfo(long doctorId) {
        Optional<User> opt = userRepository.findById(doctorId);
        if (opt.isEmpty() || !(opt.get() instanceof Doctor d)) {
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");
        }
        return new Response(StatusCode.OK, "OK", serializeDoctor(d));
    }

    public Response getAllDoctorsJson() {
        JSONArray arr = new JSONArray();
        userRepository.getDoctors().forEach(d -> arr.put(serializeDoctor(d)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    private Response validateDoctorData(Long currentId, String idStr, String username,
                                        String firstname, String lastname,
                                        String password, String confirm,
                                        String specialtyDisplay, String licenceNumber, String assignedOffice) {
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

        if ("Select one".equalsIgnoreCase(specialtyDisplay))
            return new Response(StatusCode.BAD_REQUEST, "Specialty is required.");

        try { Specialty.fromDisplayName(specialtyDisplay); } catch (IllegalArgumentException e) {
            return new Response(StatusCode.BAD_REQUEST, "Invalid specialty.");
        }

        if (!LICENCE_PATTERN.matcher(licenceNumber).matches())
            return new Response(StatusCode.BAD_REQUEST, "Licence must follow L-XXXXXXXXXX MTL format.");

        if (!OFFICE_PATTERN.matcher(assignedOffice).matches())
            return new Response(StatusCode.BAD_REQUEST, "Office must follow O-XXX format.");

        return new Response(StatusCode.OK, "OK");
    }

    public static JSONObject serializeDoctor(Doctor d) {
        JSONObject o = new JSONObject();
        o.put("id", d.getId());
        o.put("username", d.getUsername());
        o.put("firstname", d.getFirstname());
        o.put("lastname", d.getLastname());
        o.put("specialty", d.getSpecialty().getDisplayName());
        o.put("licenceNumber", d.getLicenceNumber());
        o.put("assignedOffice", d.getAssignedOffice());
        return o;
    }
}
