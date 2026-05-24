package core.controller;

import core.model.business.AppointmentManager;
import core.model.entities.Appointment;
import core.model.entities.Doctor;
import core.model.entities.Patient;
import core.model.entities.User;
import core.model.enums.Specialty;
import core.model.observers.ModelObserver;
import core.model.repositories.IAppointmentRepository;
import core.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Conecta las vistas de citas con la logica del modelo.
 * S - Single Responsibility: parseo de entradas y traduccion a Response.
 * D - Dependency Inversion: depende de AppointmentManager e interfaces de repositorio.
 *
 * AppointmentManager: contiene las REGLAS DE NEGOCIO (disponibilidad, transiciones).
 * IAppointmentRepository: acceso a datos para consultas y busquedas.
 * IUserRepository: busqueda de pacientes y doctores por ID.
 */
public class AppointmentController {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");
    private static final Set<Integer> VALID_MINUTES = Set.of(0, 15, 30, 45);

    private final AppointmentManager appointmentManager;
    private final IAppointmentRepository appointmentRepository;
    private final IUserRepository userRepository;

    public AppointmentController(AppointmentManager appointmentManager,
                                  IAppointmentRepository appointmentRepository,
                                  IUserRepository userRepository) {
        this.appointmentManager = appointmentManager;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Response requestByDoctor(long patientId, long doctorId, String dateStr, String timeStr,
                                    String reason, boolean type) {
        Response validation = validateDateTime(dateStr, timeStr);
        if (!validation.isOk()) return validation;

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Optional<User> docOpt = userRepository.findById(doctorId);
        if (docOpt.isEmpty() || !(docOpt.get() instanceof Doctor doctor))
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");

        LocalDateTime dt = parseDateTime(dateStr, timeStr);
        if (!appointmentManager.isDoctorAvailable(doctor, dt))
            return new Response(StatusCode.CONFLICT, "Doctor is not available at the requested time.");

        Appointment a = appointmentManager.create(patient, doctor, doctor.getSpecialty(), dt, reason, type);
        return new Response(StatusCode.OK, "Appointment requested. ID: " + a.getId());
    }

    public Response requestBySpecialty(long patientId, String specialtyDisplay, String dateStr,
                                       String timeStr, String reason, boolean type) {
        Response validation = validateDateTime(dateStr, timeStr);
        if (!validation.isOk()) return validation;

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Specialty specialty;
        try { specialty = Specialty.fromDisplayName(specialtyDisplay); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid specialty."); }

        LocalDateTime dt = parseDateTime(dateStr, timeStr);
        Doctor doctor = appointmentManager.findAvailableDoctor(specialty, dt);
        if (doctor == null)
            return new Response(StatusCode.CONFLICT, "No doctor available with that specialty at the requested time.");

        Appointment a = appointmentManager.create(patient, doctor, specialty, dt, reason, type);
        return new Response(StatusCode.OK, "Appointment requested. ID: " + a.getId());
    }

    public Response accept(String appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        if (!appointmentManager.accept(opt.get()))
            return new Response(StatusCode.BAD_REQUEST, "Appointment can only be accepted from REQUESTED state.");
        return new Response(StatusCode.OK, "Appointment accepted.");
    }

    public Response complete(String appointmentId, String diagnosis, String observations,
                             String recommendedTreatment, String followUp) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        if (!appointmentManager.complete(opt.get(), diagnosis, observations, recommendedTreatment, followUp))
            return new Response(StatusCode.BAD_REQUEST, "Only PENDING appointments can be completed.");
        return new Response(StatusCode.OK, "Appointment completed.");
    }

    public Response cancel(String appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        if (!appointmentManager.cancel(opt.get()))
            return new Response(StatusCode.BAD_REQUEST, "Completed appointments cannot be cancelled.");
        return new Response(StatusCode.OK, "Appointment cancelled.");
    }

    public Response reschedule(String appointmentId, String newTimeStr, String rescheduleReason) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");

        if (!TIME_PATTERN.matcher(newTimeStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Time must follow hh:mm format.");
        int minutes;
        try { minutes = Integer.parseInt(newTimeStr.substring(3)); }
        catch (NumberFormatException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid time format."); }
        if (!VALID_MINUTES.contains(minutes))
            return new Response(StatusCode.BAD_REQUEST, "Minutes must be 00, 15, 30, or 45.");

        LocalTime newTime;
        try { newTime = LocalTime.parse(newTimeStr); }
        catch (DateTimeParseException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid time."); }

        Appointment a = opt.get();
        LocalDateTime newDt = LocalDateTime.of(a.getDatetime().toLocalDate(), newTime);
        if (!appointmentManager.isDoctorAvailable(a.getDoctor(), newDt, a))
            return new Response(StatusCode.CONFLICT, "Doctor is not available at the requested time.");
        appointmentManager.reschedule(a, newDt, rescheduleReason);
        return new Response(StatusCode.OK, "Appointment rescheduled.");
    }

    public Response getPatientAppointments(long patientId) {
        List<Appointment> list = appointmentRepository.getByPatientSortedDesc(patientId);
        JSONArray arr = new JSONArray();
        list.forEach(a -> arr.put(serializeAppointment(a)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public Response getDoctorAppointments(long doctorId, boolean onlyPending) {
        List<Appointment> list = onlyPending
                ? appointmentRepository.getByDoctorPendingSortedDesc(doctorId)
                : appointmentRepository.getByDoctorSortedDesc(doctorId);
        JSONArray arr = new JSONArray();
        list.forEach(a -> arr.put(serializeAppointment(a)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public Response getAllAppointmentIds() {
        JSONArray arr = new JSONArray();
        appointmentRepository.getAll().forEach(a -> arr.put(a.getId()));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public void addObserver(ModelObserver observer)    { appointmentManager.addObserver(observer); }
    public void removeObserver(ModelObserver observer) { appointmentManager.removeObserver(observer); }

    private Response validateDateTime(String dateStr, String timeStr) {
        if (!DATE_PATTERN.matcher(dateStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");
        try { LocalDate.parse(dateStr); } catch (DateTimeParseException e) {
            return new Response(StatusCode.BAD_REQUEST, "Invalid date.");
        }
        if (!TIME_PATTERN.matcher(timeStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Time must follow hh:mm format.");
        int minutes;
        try { minutes = Integer.parseInt(timeStr.substring(3)); }
        catch (NumberFormatException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid time."); }
        if (!VALID_MINUTES.contains(minutes))
            return new Response(StatusCode.BAD_REQUEST, "Minutes must be 00, 15, 30, or 45.");
        return new Response(StatusCode.OK, "OK");
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        return LocalDateTime.of(LocalDate.parse(dateStr), LocalTime.parse(timeStr));
    }

    public static JSONObject serializeAppointment(Appointment a) {
        JSONObject o = new JSONObject();
        o.put("id", a.getId());
        o.put("datetime", a.getDatetime().toString());
        o.put("doctorName", a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname());
        o.put("doctorId", a.getDoctor().getId());
        o.put("patientName", a.getPatient().getFirstname() + " " + a.getPatient().getLastname());
        o.put("patientId", a.getPatient().getId());
        o.put("specialty", a.getSpecialty().getDisplayName());
        o.put("type", a.isType() ? "In-person" : "Remote");
        o.put("status", a.getStatus().name());
        o.put("reason", a.getReason() != null ? a.getReason() : "");
        o.put("diagnosis", a.getDiagnosis() != null ? a.getDiagnosis() : "");
        o.put("observations", a.getObservations() != null ? a.getObservations() : "");
        o.put("recommendedTreatment", a.getRecommendedTreatment() != null ? a.getRecommendedTreatment() : "");
        o.put("followUp", a.getFollowUp() != null ? a.getFollowUp() : "");
        return o;
    }
}
