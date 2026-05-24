package core.controller;

import core.model.entities.Doctor;
import core.model.entities.User;
import core.model.enums.Specialty;
import core.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;
import java.util.regex.Pattern;

public class DoctorController {

    // Formato estricto del número de licencia y consultorio — se validan con regex para no depender de lógica condicional larga
    private static final Pattern LICENCE_PATTERN = Pattern.compile("^L-\\d{10} MTL$");
    private static final Pattern OFFICE_PATTERN  = Pattern.compile("^O-\\d{3}$");

    // Solo necesita el repositorio de usuarios porque Doctor extiende User — la jerarquía de herencia lo justifica
    private final IUserRepository userRepository;

    public DoctorController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response register(String idStr, String username, String firstname, String lastname,
                             String password, String confirm, String specialtyDisplay,
                             String licenceNumber, String assignedOffice) {
        // currentId null le indica al validador que es un registro nuevo, no una actualización
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
        // Pattern matching con instanceof para hacer el downcast de User a Doctor de forma segura
        Optional<User> opt = userRepository.findById(currentDoctorId);
        if (opt.isEmpty() || !(opt.get() instanceof Doctor d)) {
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");
        }
        // Se pasa currentDoctorId para que el validador omita la verificación de ID duplicado sobre sí mismo
        Response v = validateDoctorData(currentDoctorId, String.valueOf(d.getId()), username, firstname,
                lastname, password, confirm, specialtyDisplay, licenceNumber, assignedOffice);
        if (!v.isOk()) return v;

        // El username solo se verifica contra duplicados si realmente cambió
        if (!d.getUsername().equals(username) && userRepository.usernameExists(username)) {
            return new Response(StatusCode.CONFLICT, "Username already taken.");
        }

        // Mutación directa sobre la entidad recuperada del repositorio — no se crea un objeto nuevo
        d.setUsername(username);
        d.setFirstname(firstname);
        d.setLastname(lastname);
        d.setPassword(password);
        d.setSpecialty(Specialty.fromDisplayName(specialtyDisplay));
        d.setLicenceNumber(licenceNumber);
        d.setAssignedOffice(assignedOffice);
        // Se notifica manualmente porque la mutación no pasa por el Manager, sino directo al repositorio
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
        // getDoctors() ya filtra por tipo en el repositorio — no hace falta instanceof aquí
        userRepository.getDoctors().forEach(d -> arr.put(serializeDoctor(d)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    // currentId null = registro nuevo; currentId presente = actualización — un solo método maneja ambos casos
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
        // Regla de negocio: el ID debe ser exactamente de 12 dígitos y positivo
        if (id <= 0 || String.valueOf(id).length() != 12)
            return new Response(StatusCode.BAD_REQUEST, "ID must be a positive 12-digit number.");

        // Las verificaciones de duplicado solo aplican al registrar, no al actualizar
        if (currentId == null && userRepository.idExists(id))
            return new Response(StatusCode.CONFLICT, "ID already in use.");
        if (currentId == null && userRepository.usernameExists(username))
            return new Response(StatusCode.CONFLICT, "Username already taken.");

        // "Select one" es el valor por defecto del combo en la vista — se trata como campo vacío
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

    // static porque no usa estado del Controller; se reutiliza desde AppointmentController sin instanciar
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