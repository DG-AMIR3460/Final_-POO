package core.model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Patient extiende User agregando los datos clínicos y personales propios del rol — herencia justificada por la jerarquía de usuarios del sistema
public class Patient extends User {

    private String email;
    private LocalDate birthdate;
    // El género se representa como boolean (true = Male) para evitar dependencia de un enum externo en la entidad
    private boolean gender;
    private long phone;
    private String address;
    // final porque la lista de citas no se reemplaza, solo crece con addAppointment
    private final List<Appointment> appointments;
    // Un paciente solo puede tener una hospitalización activa a la vez — por eso es un objeto simple, no una lista
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
        // hospitalization arranca null intencionalmente — solo se asigna cuando se crea una Hospitalization
        this.appointments = new ArrayList<>();
    }

    public String getEmail()              { return email; }
    public LocalDate getBirthdate()       { return birthdate; }
    public boolean isGender()             { return gender; }
    public long getPhone()                { return phone; }
    public String getAddress()            { return address; }
    public Hospitalization getHospitalization() { return hospitalization; }
    // Exponer la lista directamente permite que AppointmentManager itere sobre ella para verificar disponibilidad
    public List<Appointment> getAppointments()  { return appointments; }

    public void setEmail(String email)           { this.email = email; }
    public void setBirthdate(LocalDate birthdate){ this.birthdate = birthdate; }
    public void setGender(boolean gender)        { this.gender = gender; }
    public void setPhone(long phone)             { this.phone = phone; }
    public void setAddress(String address)       { this.address = address; }
    // setHospitalization en lugar de add refuerza la restricción de una sola hospitalización activa por paciente
    public void setHospitalization(Hospitalization h) { this.hospitalization = h; }
    // Lo invoca el constructor de Appointment para mantener la asociación bidireccional sin intervención del Manager
    public void addAppointment(Appointment a) { appointments.add(a); }
}