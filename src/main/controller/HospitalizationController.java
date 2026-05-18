package main.controller;

import main.model.business.HospitalizationManager;
import main.model.entities.Appointment;
import main.model.entities.Doctor;
import main.model.entities.Hospitalization;
import main.model.entities.Patient;
import main.model.entities.User;
import main.model.enums.RoomType;
import main.model.observers.ModelObserver;
import main.model.repositories.IAppointmentRepository;
import main.model.repositories.IHospitalizationRepository;
import main.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Conecta las vistas de hospitalizacion con la logica del modelo.
 * S - Single Responsibility: parseo de entradas y traduccion a Response.
 * D - Dependency Inversion: depende de HospitalizationManager e interfaces de repositorio.
 *
 * HospitalizationManager: contiene las REGLAS DE NEGOCIO (3 escenarios de creacion, approve, deny).
 * IHospitalizationRepository / IUserRepository / IAppointmentRepository: acceso a datos.
 */
public class HospitalizationController {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private final HospitalizationManager hospitalizationManager;
    private final IHospitalizationRepository hospitalizationRepository;
    private final IUserRepository userRepository;
    private final IAppointmentRepository appointmentRepository;

    public HospitalizationController(HospitalizationManager hospitalizationManager,
                                     IHospitalizationRepository hospitalizationRepository,
                                     IUserRepository userRepository,
                                     IAppointmentRepository appointmentRepository) {
        this.hospitalizationManager = hospitalizationManager;
        this.hospitalizationRepository = hospitalizationRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /** Escenario 1: Paciente solicita hospitalizacion (queda en REQUESTED). */
    public Response request(long patientId, long doctorId, String dateStr,
                            String reason, String roomTypeStr, String observations) {
        LocalDate date = parseDate(dateStr);
        if (date == null) return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Optional<User> docOpt = userRepository.findById(doctorId);
        if (docOpt.isEmpty() || !(docOpt.get() instanceof Doctor doctor))
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");

        RoomType roomType;
        try { roomType = RoomType.fromDisplayName(roomTypeStr); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid room type."); }

        Hospitalization h = hospitalizationManager.createRequest(patient, doctor, date, reason, roomType, observations);
        return new Response(StatusCode.OK, "Hospitalization requested. ID: " + h.getId());
    }

    /** Escenario 2: Doctor crea hospitalizacion directa (ONGOING de inmediato). */
    public Response requestDirect(long patientId, long doctorId, String dateStr,
                                   String reason, String roomTypeStr, String observations) {
        LocalDate date = parseDate(dateStr);
        if (date == null) return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Optional<User> docOpt = userRepository.findById(doctorId);
        if (docOpt.isEmpty() || !(docOpt.get() instanceof Doctor doctor))
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");

        RoomType roomType;
        try { roomType = RoomType.fromDisplayName(roomTypeStr); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid room type."); }

        Hospitalization h = hospitalizationManager.createDirect(patient, doctor, date, reason, roomType, observations);
        return new Response(StatusCode.OK, "Hospitalization started. ID: " + h.getId());
    }

    /** Escenario 3: Hospitalizacion derivada de una cita (cita se completa + ONGOING). */
    public Response requestFromAppointment(String appointmentId, String dateStr,
                                           String reason, String roomTypeStr, String observations) {
        Optional<Appointment> aOpt = appointmentRepository.findById(appointmentId);
        if (aOpt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");

        LocalDate date = parseDate(dateStr);
        if (date == null) return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");

        RoomType roomType;
        try { roomType = RoomType.fromDisplayName(roomTypeStr); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid room type."); }

        Hospitalization h = hospitalizationManager.createFromAppointment(aOpt.get(), date, reason, roomType, observations);
        return new Response(StatusCode.OK, "Hospitalization started. ID: " + h.getId());
    }

    public Response approve(String hospId) {
        Optional<Hospitalization> opt = hospitalizationRepository.findById(hospId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Hospitalization not found.");
        if (!hospitalizationManager.approve(opt.get()))
            return new Response(StatusCode.BAD_REQUEST, "Only REQUESTED hospitalizations can be approved.");
        return new Response(StatusCode.OK, "Hospitalization approved.");
    }

    public Response deny(String hospId) {
        Optional<Hospitalization> opt = hospitalizationRepository.findById(hospId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Hospitalization not found.");
        if (!hospitalizationManager.deny(opt.get()))
            return new Response(StatusCode.BAD_REQUEST, "Hospitalization already cancelled.");
        return new Response(StatusCode.OK, "Hospitalization denied.");
    }

    public Response getByPatient(long patientId) {
        List<Hospitalization> list = hospitalizationRepository.getByPatient(patientId);
        JSONArray arr = new JSONArray();
        list.forEach(h -> arr.put(serializeHospitalization(h)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public Response getByDoctor(long doctorId) {
        List<Hospitalization> list = hospitalizationRepository.getByDoctor(doctorId);
        JSONArray arr = new JSONArray();
        list.forEach(h -> arr.put(serializeHospitalization(h)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public Response getAllIds() {
        JSONArray arr = new JSONArray();
        hospitalizationRepository.getAll().stream()
                .filter(h -> h.getStatus() == hospital.model.enums.HospitalizationStatus.REQUESTED)
                .forEach(h -> arr.put(h.getId()));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public void addObserver(ModelObserver observer)    { hospitalizationManager.addObserver(observer); }
    public void removeObserver(ModelObserver observer) { hospitalizationManager.removeObserver(observer); }

    private LocalDate parseDate(String dateStr) {
        if (!DATE_PATTERN.matcher(dateStr).matches()) return null;
        try { return LocalDate.parse(dateStr); } catch (DateTimeParseException e) { return null; }
    }

    public static JSONObject serializeHospitalization(Hospitalization h) {
        JSONObject o = new JSONObject();
        o.put("id", h.getId());
        o.put("patientName", h.getPatient().getFirstname() + " " + h.getPatient().getLastname());
        o.put("patientId", h.getPatient().getId());
        o.put("doctorName", h.getDoctor().getFirstname() + " " + h.getDoctor().getLastname());
        o.put("doctorId", h.getDoctor().getId());
        o.put("date", h.getDate().toString());
        o.put("reason", h.getReason() != null ? h.getReason() : "");
        o.put("roomType", h.getRoomType().name());
        o.put("observations", h.getObservations() != null ? h.getObservations() : "");
        o.put("status", h.getStatus().name());
        return o;
    }
}
