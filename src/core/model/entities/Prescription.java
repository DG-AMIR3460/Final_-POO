package core.model.entities;

public class Prescription {

    // Todos los campos son final — una prescripción es inmutable por diseño; no tiene sentido modificar una orden médica emitida
    private final Appointment appointment;
    private final String medicationName;
    private final double dose;
    private final String administrationRoute;
    private final int treatmentDuration;
    private final String additionalInstructions;
    private final int frequency;

    public Prescription(Appointment appointment, String medicationName, double dose,
                        String administrationRoute, int treatmentDuration,
                        String additionalInstructions, int frequency) {
        this.appointment = appointment;
        this.medicationName = medicationName;
        this.dose = dose;
        this.administrationRoute = administrationRoute;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstructions = additionalInstructions;
        this.frequency = frequency;
        // Registro bidireccional desde el constructor — la prescripción se enlaza a su cita sin que el Manager intervenga
        appointment.addPrescription(this);
    }

    public Appointment getAppointment()        { return appointment; }
    public String getMedicationName()          { return medicationName; }
    public double getDose()                    { return dose; }
    public String getAdministrationRoute()     { return administrationRoute; }
    public int getTreatmentDuration()          { return treatmentDuration; }
    public String getAdditionalInstructions()  { return additionalInstructions; }
    public int getFrequency()                  { return frequency; }
}