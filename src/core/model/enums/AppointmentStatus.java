package core.model.enums;

// Los cuatro estados definen el ciclo de vida completo de una cita — las transiciones válidas las controla AppointmentManager
public enum AppointmentStatus {
    REQUESTED,  // estado inicial al crear la cita
    PENDING,    // el doctor aceptó; está por realizarse
    COMPLETED,  // estado terminal con datos clínicos asociados
    CANCELED    // puede venir desde REQUESTED o PENDING, nunca desde COMPLETED
}