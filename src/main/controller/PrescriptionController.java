package main.controller;

import main.model.entities.Appointment;
import main.model.entities.Prescription;
import main.model.enums.AppointmentStatus;
import main.model.repositories.IAppointmentRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class PrescriptionController {

    private final IAppointmentRepository appointmentRepository;

    public PrescriptionController(IAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Response prescribe(String appointmentId, String medicationName, String doseStr,
                              String administrationRoute, String durationStr,
                              String additionalInstructions, String frequencyStr) {
        if (appointmentId == null || appointmentId.isBlank())
            return new Response(StatusCode.BAD_REQUEST, "Appointment ID is required.");
        if (medicationName == null || medicationName.isBlank())
            return new Response(StatusCode.BAD_REQUEST, "Medication name is required.");

        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = opt.get();

        if (a.getStatus() != AppointmentStatus.PENDING)
            return new Response(StatusCode.BAD_REQUEST,
                    "Prescriptions can only be added to PENDING appointments.");

        double dose;
        int duration, frequency;
        try { dose = Double.parseDouble(doseStr); } catch (NumberFormatException e) {
            return new Response(StatusCode.BAD_REQUEST, "Dose must be a valid number.");
        }
        try { duration = Integer.parseInt(durationStr); } catch (NumberFormatException e) {
            return new Response(StatusCode.BAD_REQUEST, "Duration must be a whole number of days.");
        }
        try { frequency = Integer.parseInt(frequencyStr); } catch (NumberFormatException e) {
            return new Response(StatusCode.BAD_REQUEST, "Frequency must be a whole number.");
        }

        new Prescription(a, medicationName, dose, administrationRoute,
                duration, additionalInstructions, frequency);
        appointmentRepository.notifyObservers();
        return new Response(StatusCode.OK, "Prescription added successfully.");
    }

    public Response getPrescriptions(String appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        JSONArray arr = new JSONArray();
        opt.get().getPrescriptions().forEach(p -> arr.put(serializePrescription(p)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public Response getAllPrescriptionsForDoctor(long doctorId) {
        JSONArray arr = new JSONArray();
        appointmentRepository.getByDoctorSortedDesc(doctorId).stream()
                .flatMap(a -> a.getPrescriptions().stream())
                .forEach(p -> arr.put(serializePrescription(p)));
        return new Response(StatusCode.OK, "OK", arr);
    }

    public static JSONObject serializePrescription(Prescription p) {
        JSONObject o = new JSONObject();
        o.put("appointmentId", p.getAppointment().getId());
        o.put("medicationName", p.getMedicationName());
        o.put("dose", p.getDose());
        o.put("administrationRoute", p.getAdministrationRoute());
        o.put("treatmentDuration", p.getTreatmentDuration());
        o.put("additionalInstructions", p.getAdditionalInstructions() != null ? p.getAdditionalInstructions() : "");
        o.put("frequency", p.getFrequency());
        return o;
    }
}
