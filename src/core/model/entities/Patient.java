package core.model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient extends User {

    private String email;
    private LocalDate birthdate;
    private boolean gender;
    private long phone;
    private String address;
    private final List<Appointment> appointments;
    private Hospitalization hospitalization;

    public Patient(long id, String username, String firstname, String lastname,
                   String password, String email, LocalDate birthdate,
                   boolean gender, long phone, String address) {
        super(id, username, firstname, lastname, password);
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.appointments = new ArrayList<>();
    }

    public String getEmail()              { return email; }
    public LocalDate getBirthdate()       { return birthdate; }
    public boolean isGender()             { return gender; }
    public long getPhone()                { return phone; }
    public String getAddress()            { return address; }
    public Hospitalization getHospitalization() { return hospitalization; }
    public List<Appointment> getAppointments()  { return appointments; }

    public void setEmail(String email)           { this.email = email; }
    public void setBirthdate(LocalDate birthdate){ this.birthdate = birthdate; }
    public void setGender(boolean gender)        { this.gender = gender; }
    public void setPhone(long phone)             { this.phone = phone; }
    public void setAddress(String address)       { this.address = address; }
    public void setHospitalization(Hospitalization h) { this.hospitalization = h; }

    public void addAppointment(Appointment a) { appointments.add(a); }
}
