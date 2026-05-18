package core.model.entities;

import core.model.enums.Specialty;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {

    private Specialty specialty;
    private String licenceNumber;
    private String assignedOffice;
    private final List<Appointment> appointments;
    private final List<Hospitalization> hospitalizations;

    public Doctor(long id, String username, String firstname, String lastname,
                  String password, Specialty specialty, String licenceNumber, String assignedOffice) {
        super(id, username, firstname, lastname, password);
        this.specialty = specialty;
        this.licenceNumber = licenceNumber;
        this.assignedOffice = assignedOffice;
        this.appointments = new ArrayList<>();
        this.hospitalizations = new ArrayList<>();
    }

    public Specialty getSpecialty()              { return specialty; }
    public String getLicenceNumber()             { return licenceNumber; }
    public String getAssignedOffice()            { return assignedOffice; }
    public List<Appointment> getAppointments()   { return appointments; }
    public List<Hospitalization> getHospitalizations() { return hospitalizations; }

    public void setSpecialty(Specialty specialty)       { this.specialty = specialty; }
    public void setLicenceNumber(String licenceNumber)  { this.licenceNumber = licenceNumber; }
    public void setAssignedOffice(String assignedOffice){ this.assignedOffice = assignedOffice; }

    public void addAppointment(Appointment a)    { appointments.add(a); }
    public void addHospitalization(Hospitalization h) { hospitalizations.add(h); }
}
