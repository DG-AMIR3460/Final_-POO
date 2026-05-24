package core.controller;

import core.model.entities.Appointment;
import core.model.entities.Prescription;
import core.model.enums.AppointmentStatus;
import core.model.repositories.IAppointmentRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Controlador encargado de gestionar las prescripciones médicas
 * asociadas a citas (appointments) en el sistema.
 */
public class PrescriptionController {
    
    // Repositorio de citas
    private final IAppointmentRepository appointmentRepository;
    
    
    public PrescriptionController(IAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    
    // Agrega una prescipcion medica a una cita existente
    public Response prescribe(String appointmentId, String medicationName, String doseStr,
                              String administrationRoute, String durationStr,
                              String additionalInstructions, String frequencyStr) {
        // Validación: el ID de la cita no puede ser nulo ni vacío
        if (appointmentId == null || appointmentId.isBlank())
            return new Response(StatusCode.BAD_REQUEST, "Appointment ID is required.");
        // Validación: el nombre del medicamento es obligatorio
        if (medicationName == null || medicationName.isBlank())
            return new Response(StatusCode.BAD_REQUEST, "Medication name is required.");
        
        // Se busca la cita en el repositorio por su ID
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        Appointment a = opt.get();
        
        // Solo se pueden agregar prescripciones a citas en estado PENDING
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
    
    // Obtiene todas las prescripciones asociadas a una cita específica.
    public Response getPrescriptions(String appointmentId) {
        
        // Se busca la cita; si no existe se retorna error 404
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return new Response(StatusCode.NOT_FOUND, "Appointment not found.");
        
        // Se serializa cada prescripción de la cita en formato JSON
        JSONArray arr = new JSONArray();
        opt.get().getPrescriptions().forEach(p -> arr.put(serializePrescription(p)));
        return new Response(StatusCode.OK, "OK", arr);
    }
    
    
    /* Obtiene todas las prescripciones emitidas por un médico,
     recorriendo todas sus citas ordenadas de forma descendente.*/
    public Response getAllPrescriptionsForDoctor(long doctorId) {
        JSONArray arr = new JSONArray();
        appointmentRepository.getByDoctorSortedDesc(doctorId).stream()
                .flatMap(a -> a.getPrescriptions().stream())
                .forEach(p -> arr.put(serializePrescription(p)));
        return new Response(StatusCode.OK, "OK", arr);
    }
    
    // Convierte un objeto Prescription en un JSONObject para su transmisión.
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
