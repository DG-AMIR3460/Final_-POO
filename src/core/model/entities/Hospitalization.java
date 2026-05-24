package core.model.entities;

import core.model.enums.HospitalizationStatus;
import core.model.enums.RoomType;
import java.time.LocalDate;

public class Hospitalization {

    // id es final porque la identidad de una hospitalización no cambia una vez creada
    private final String id;
    private Patient patient;
    private Doctor doctor;
    private LocalDate date;
    private String reason;
    private RoomType roomType;
    private String observations;
    private HospitalizationStatus status;

    // Constructor de conveniencia para el escenario donde el paciente solicita la hospitalización — arranca siempre en REQUESTED
    public Hospitalization(String id, Patient patient, Doctor doctor, LocalDate date,
                           String reason, RoomType roomType, String observations) {
        this(id, patient, doctor, date, reason, roomType, observations, HospitalizationStatus.REQUESTED);
    }

    // Constructor principal que recibe el estado explícitamente — usado cuando el doctor crea una hospitalización directa (ONGOING)
    public Hospitalization(String id, Patient patient, Doctor doctor, LocalDate date,
                           String reason, RoomType roomType, String observations,
                           HospitalizationStatus status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.reason = reason;
        this.roomType = roomType;
        this.observations = observations;
        this.status = status;
        // Registro bidireccional en construcción: un paciente solo puede tener una hospitalización activa a la vez
        patient.setHospitalization(this);
        doctor.addHospitalization(this);
    }

    public String getId()                    { return id; }
    public Patient getPatient()              { return patient; }
    public Doctor getDoctor()                { return doctor; }
    public LocalDate getDate()               { return date; }
    public String getReason()                { return reason; }
    public RoomType getRoomType()            { return roomType; }
    public String getObservations()          { return observations; }
    public HospitalizationStatus getStatus() { return status; }

    // Solo status y observations son mutables — el resto de los datos de la hospitalización son fijos al crearla
    public void setStatus(HospitalizationStatus status) { this.status = status; }
    public void setObservations(String observations)    { this.observations = observations; }
}