package core.model.entities;

import core.model.enums.AppointmentStatus;
import core.model.enums.Specialty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Appointment {

    // id es final porque una cita nunca cambia de identidad una vez creada
    private final String id;
    private Patient patient;
    private Doctor doctor;
    private Specialty specialty;
    private LocalDateTime datetime;
    private String reason;
    private boolean type;
    // final para garantizar que la lista no se reemplace, solo se modifique
    private final List<Prescription> prescriptions;
    private AppointmentStatus status;
    // Los campos clínicos arrancan null y solo se asignan al completar la cita desde el Manager
    private String diagnosis;
    private String observations;
    private String recommendedTreatment;
    private String followUp;

    public Appointment(String id, Patient patient, Doctor doctor, Specialty specialty,
                       LocalDateTime datetime, String reason, boolean type) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.specialty = specialty;
        this.datetime = datetime;
        this.reason = reason;
        this.type = type;
        // Toda cita nace en REQUESTED — las transiciones de estado las maneja AppointmentManager
        this.status = AppointmentStatus.REQUESTED;
        this.prescriptions = new ArrayList<>();
        // Registro bidireccional: la cita se añade a las listas del paciente y del doctor en construcción
        patient.addAppointment(this);
        doctor.addAppointment(this);
    }

    public String getId()                     { return id; }
    public Patient getPatient()               { return patient; }
    public Doctor getDoctor()                 { return doctor; }
    public Specialty getSpecialty()           { return specialty; }
    public LocalDateTime getDatetime()        { return datetime; }
    public String getReason()                 { return reason; }
    public boolean isType()                   { return type; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public AppointmentStatus getStatus()      { return status; }
    public String getDiagnosis()              { return diagnosis; }
    public String getObservations()           { return observations; }
    public String getRecommendedTreatment()   { return recommendedTreatment; }
    public String getFollowUp()               { return followUp; }

    // Solo status, datetime y reason son mutables por diseño — los datos de identidad (id, patient, doctor) son inmutables
    public void setStatus(AppointmentStatus status)           { this.status = status; }
    public void setDatetime(LocalDateTime datetime)           { this.datetime = datetime; }
    public void setReason(String reason)                      { this.reason = reason; }
    public void setDiagnosis(String diagnosis)                { this.diagnosis = diagnosis; }
    public void setObservations(String observations)          { this.observations = observations; }
    public void setRecommendedTreatment(String rt)            { this.recommendedTreatment = rt; }
    public void setFollowUp(String followUp)                  { this.followUp = followUp; }

    public void addPrescription(Prescription p) { prescriptions.add(p); }
}