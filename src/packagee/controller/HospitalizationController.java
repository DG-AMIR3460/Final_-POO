package packagee.controller;

import packagee.model.entities.Appointment;
import packagee.model.entities.Doctor;
import packagee.model.entities.Hospitalization;
import packagee.model.entities.Patient;
import packagee.model.entities.User;
import packagee.model.enums.AppointmentStatus;
import packagee.model.enums.HospitalizationStatus;
import packagee.model.enums.RoomType;
import packagee.model.observers.ModelObserver;
import packagee.model.repositories.IAppointmentRepository;
import packagee.model.repositories.IHospitalizationRepository;
import packagee.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class HospitalizationController {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private final IHospitalizationRepository hospitalizationRepository;
    private final IUserRepository userRepository;
    private final IAppointmentRepository appointmentRepository;

    public HospitalizationController(IHospitalizationRepository hospitalizationRepository,
                                     IUserRepository userRepository,
                                     IAppointmentRepository appointmentRepository) {
        this.hospitalizationRepository = hospitalizationRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Response request(long patientId, long doctorId, String dateStr,
                            String reason, String roomTypeStr, String observations) {
        if (!DATE_PATTERN.matcher(dateStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");
        LocalDate date;
        try { date = LocalDate.parse(dateStr); } catch (DateTimeParseException e) {
            return new Response(StatusCode.BAD_REQUEST, "Invalid date.");
        }

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Optional<User> docOpt = userRepository.findById(doctorId);
        if (docOpt.isEmpty() || !(docOpt.get() instanceof Doctor doctor))
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");

        RoomType roomType;
        try { roomType = RoomType.fromDisplayName(roomTypeStr); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid room type."); }

        String id = hospitalizationRepository.generateId(patientId);
        Hospitalization h = new Hospitalization(id, patient, doctor, date, reason, roomType, observations);
        hospitalizationRepository.add(h);
        return new Response(StatusCode.OK, "Hospitalization requested. ID: " + id);
    }

    public Response requestFromAppointment(String appointmentId, String dateStr,
                                           String reason, String roomTypeStr, String observations) {
        Optional<Appointment> aOpt = appointmentRepository.findById(appointmentId);
        if (aOpt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = aOpt.get();

        if (!DATE_PATTERN.matcher(dateStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");
        LocalDate date;
        try { date = LocalDate.parse(dateStr); } catch (DateTimeParseException e) {
            return new Response(StatusCode.BAD_REQUEST, "Invalid date.");
        }

        RoomType roomType;
        try { roomType = RoomType.fromDisplayName(roomTypeStr); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid room type."); }

        a.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.notifyObservers();

        String id = hospitalizationRepository.generateId(a.getPatient().getId());
        Hospitalization h = new Hospitalization(id, a.getPatient(), a.getDoctor(), date,
                reason, roomType, observations, HospitalizationStatus.ONGOING);
        hospitalizationRepository.add(h);
        return new Response(StatusCode.OK, "Hospitalization started. ID: " + id);
    }

    public Response approve(String hospId) {
        Optional<Hospitalization> opt = hospitalizationRepository.findById(hospId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Hospitalization not found.");
        Hospitalization h = opt.get();
        if (h.getStatus() != HospitalizationStatus.REQUESTED)
            return new Response(StatusCode.BAD_REQUEST, "Only REQUESTED hospitalizations can be approved.");
        h.setStatus(HospitalizationStatus.ONGOING);
        hospitalizationRepository.notifyObservers();
        return new Response(StatusCode.OK, "Hospitalization approved.");
    }

    public Response deny(String hospId) {
        Optional<Hospitalization> opt = hospitalizationRepository.findById(hospId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Hospitalization not found.");
        Hospitalization h = opt.get();
        if (h.getStatus() == HospitalizationStatus.CANCELED)
            return new Response(StatusCode.BAD_REQUEST, "Hospitalization already cancelled.");
        h.setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.notifyObservers();
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

    public Response requestDirect(long patientId, long doctorId, String dateStr,
                                   String reason, String roomTypeStr, String observations) {
        if (!DATE_PATTERN.matcher(dateStr).matches())
            return new Response(StatusCode.BAD_REQUEST, "Date must follow YYYY-MM-DD format.");
        LocalDate date;
        try { date = LocalDate.parse(dateStr); } catch (DateTimeParseException e) {
            return new Response(StatusCode.BAD_REQUEST, "Invalid date.");
        }

        Optional<User> patOpt = userRepository.findById(patientId);
        if (patOpt.isEmpty() || !(patOpt.get() instanceof Patient patient))
            return new Response(StatusCode.NOT_FOUND, "Patient not found.");

        Optional<User> docOpt = userRepository.findById(doctorId);
        if (docOpt.isEmpty() || !(docOpt.get() instanceof Doctor doctor))
            return new Response(StatusCode.NOT_FOUND, "Doctor not found.");

        RoomType roomType;
        try { roomType = RoomType.fromDisplayName(roomTypeStr); }
        catch (IllegalArgumentException e) { return new Response(StatusCode.BAD_REQUEST, "Invalid room type."); }

        String id = hospitalizationRepository.generateId(patientId);
        Hospitalization h = new Hospitalization(id, patient, doctor, date, reason, roomType, observations,
                HospitalizationStatus.ONGOING);
        hospitalizationRepository.add(h);
        return new Response(StatusCode.OK, "Hospitalization started. ID: " + id);
    }

    public Response getAllIds() {
        JSONArray arr = new JSONArray();
        hospitalizationRepository.getAll().stream()
                .filter(h -> h.getStatus() == HospitalizationStatus.REQUESTED)
                .forEach(h -> arr.put(h.getId()));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public void addObserver(ModelObserver observer)    { hospitalizationRepository.addObserver(observer); }
    public void removeObserver(ModelObserver observer) { hospitalizationRepository.removeObserver(observer); }

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
