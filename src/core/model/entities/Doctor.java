package core.model.entities;

import core.model.enums.Specialty;
import java.util.ArrayList;
import java.util.List;

// Doctor extiende User y agrega los atributos propios del rol médico — herencia justificada porque comparte identidad con Patient y Administrator
public class Doctor extends User {

    private Specialty specialty;
    private String licenceNumber;
    private String assignedOffice;
    // final para garantizar que las listas no se reemplacen accidentalmente; solo se modifican con add
    private final List<Appointment> appointments;
    private final List<Hospitalization> hospitalizations;

    public Doctor(long id, String username, String firstname, String lastname,
                  String password, Specialty specialty, String licenceNumber, String assignedOffice) {
        // Se delega la inicialización de los campos de User al constructor padre
        super(id, username, firstname, lastname, password);
        this.specialty = specialty;
        this.licenceNumber = licenceNumber;
        this.assignedOffice = assignedOffice;
        // Las listas se inicializan aquí para que nunca estén null; evita NullPointerException al consultar disponibilidad
        this.appointments = new ArrayList<>();
        this.hospitalizations = new ArrayList<>();
    }

    public Specialty getSpecialty()              { return specialty; }
    public String getLicenceNumber()             { return licenceNumber; }
    public String getAssignedOffice()            { return assignedOffice; }
    // Exponer la lista directamente permite que AppointmentManager itere sobre ella sin métodos extra en Doctor
    public List<Appointment> getAppointments()   { return appointments; }
    public List<Hospitalization> getHospitalizations() { return hospitalizations; }

    public void setSpecialty(Specialty specialty)       { this.specialty = specialty; }
    public void setLicenceNumber(String licenceNumber)  { this.licenceNumber = licenceNumber; }
    public void setAssignedOffice(String assignedOffice){ this.assignedOffice = assignedOffice; }

    // Estos métodos los invoca Appointment y Hospitalization desde su constructor para mantener la asociación bidireccional
    public void addAppointment(Appointment a)    { appointments.add(a); }
    public void addHospitalization(Hospitalization h) { hospitalizations.add(h); }
}