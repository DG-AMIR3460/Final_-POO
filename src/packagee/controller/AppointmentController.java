package packagee.controller;

import packagee.model.entities.Appointment;
import packagee.model.entities.Doctor;
import packagee.model.entities.Patient;
import packagee.model.entities.User;
import packagee.model.enums.AppointmentStatus;
import packagee.model.enums.Specialty;
import packagee.model.observers.ModelObserver;
import packagee.model.repositories.AppointmentRepository;
import packagee.model.repositories.UserRepository;

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

public class AppointmentController {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");
    private static final Set<Integer> VALID_MINUTES = Set.of(0, 15, 30, 45);

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Response requestByDoctor(long patientId, long doctorId, String dateStr, String timeStr,
                                    String reason, boolean type) {
        Response dateTimeValidation = validateDateTime(dateStr, timeStr);
        if (!dateTimeValidation.isOk()) return dateTimeValidation;

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Optional<User> docOpt = userRepository.findById(doctorId);
        if (docOpt.isEmpty() || !(docOpt.get() instanceof Doctor doctor))
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");

        LocalDateTime dt = parseDateTime(dateStr, timeStr);
        if (!isDoctorAvailable(doctor, dt))
            return new Response(StatusCode.CONFLICT, "Doctor is not available at the requested time.");

        String id = appointmentRepository.generateId(patientId);
        Appointment appointment = new Appointment(id, patient, doctor, doctor.getSpecialty(), dt, reason, type);
        appointmentRepository.add(appointment);
        return new Response(StatusCode.OK, "Appointment requested. ID: " + id);
    }

    public Response requestBySpecialty(long patientId, String specialtyDisplay, String dateStr,
                                       String timeStr, String reason, boolean type) {
        Response dateTimeValidation = validateDateTime(dateStr, timeStr);
        if (!dateTimeValidation.isOk()) return dateTimeValidation;

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Specialty specialty;
        try { specialty = Specialty.fromDisplayName(specialtyDisplay); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid specialty."); }

        LocalDateTime dt = parseDateTime(dateStr, timeStr);
        Doctor doctor = findAvailableDoctor(specialty, dt);
        if (doctor == null)
            return new Response(StatusCode.CONFLICT, "No doctor available with that specialty at the requested time.");

        String id = appointmentRepository.generateId(patientId);
        Appointment appointment = new Appointment(id, patient, doctor, specialty, dt, reason, type);
        appointmentRepository.add(appointment);
        return new Response(StatusCode.OK, "Appointment requested. ID: " + id);
    }

    public Response accept(String appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = opt.get();
        if (a.getStatus() != AppointmentStatus.REQUESTED)
            return new Response(StatusCode.BAD_REQUEST, "Appointment can only be accepted from REQUESTED state.");
        a.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.notifyObservers();
        return new Response(StatusCode.OK, "Appointment accepted.");
    }

    public Response complete(String appointmentId, String diagnosis, String observations,
                             String recommendedTreatment, String followUp) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = opt.get();
        if (a.getStatus() != AppointmentStatus.PENDING)
            return new Response(StatusCode.BAD_REQUEST, "Only PENDING appointments can be completed.");
        a.setStatus(AppointmentStatus.COMPLETED);
        a.setDiagnosis(diagnosis);
        a.setObservations(observations);
        a.setRecommendedTreatment(recommendedTreatment);
        a.setFollowUp(followUp);
        appointmentRepository.notifyObservers();
        return new Response(StatusCode.OK, "Appointment completed.");
    }

    public Response cancel(String appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = opt.get();
        if (a.getStatus() == AppointmentStatus.COMPLETED)
            return new Response(StatusCode.BAD_REQUEST, "Completed appointments cannot be cancelled.");
        a.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.notifyObservers();
        return new Response(StatusCode.OK, "Appointment cancelled.");
    }

    public Response reschedule(String appointmentId, String newTimeStr, String rescheduleReason) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = opt.get();

        if (!TIME_PATTERN.matcher(newTimeStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Time must follow hh:mm format.");
        int minutes;
        try {
            minutes = Integer.parseInt(newTimeStr.substring(3));
        } catch (NumberFormatException e) {
            return new Response(StatusCode.BAD_REQUEST, "Invalid time format.");
        }
        if (!VALID_MINUTES.contains(minutes))
            return new Response(StatusCode.BAD_REQUEST, "Minutes must be 00, 15, 30, or 45.");

        LocalTime newTime;
        try { newTime = LocalTime.parse(newTimeStr); }
        catch (DateTimeParseException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid time."); }

        LocalDateTime newDt = LocalDateTime.of(a.getDatetime().toLocalDate(), newTime);
        a.setDatetime(newDt);
        if (rescheduleReason != null && !rescheduleReason.isBlank()) {
            a.setReason(a.getReason() + " | Rescheduled: " + rescheduleReason);
        }
        appointmentRepository.notifyObservers();
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

    public void addObserver(ModelObserver observer)    { appointmentRepository.addObserver(observer); }
    public void removeObserver(ModelObserver observer) { appointmentRepository.removeObserver(observer); }

    private boolean isDoctorAvailable(Doctor doctor, LocalDateTime requested) {
        LocalDateTime end = requested.plusMinutes(15);
        return doctor.getAppointments().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELED
                          && a.getStatus() != AppointmentStatus.COMPLETED)
                .noneMatch(a -> {
                    LocalDateTime s = a.getDatetime();
                    LocalDateTime e = s.plusMinutes(15);
                    return requested.isBefore(e) && end.isAfter(s);
                });
    }

    private Doctor findAvailableDoctor(Specialty specialty, LocalDateTime dt) {
        return userRepository.getDoctors().stream()
                .filter(d -> d.getSpecialty() == specialty && isDoctorAvailable(d, dt))
                .findFirst().orElse(null);
    }

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
