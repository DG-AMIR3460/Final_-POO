package core.model.enums;

public enum Specialty {

    // Cada constante lleva su displayName porque los nombres del enum no pueden contener espacios ni caracteres especiales
    GENERAL_MEDICINE("General Medicine"),
    CARDIOLOGY("Cardiology"),
    PEDIATRICS("Pediatrics"),
    NEUROLOGY("Neurology"),
    TRAUMATOLOGY_ORTHOPEDICS("Traumatology & Orthopedics"),
    GYNECOLOGY_OBSTETRICS("Gynecology & Obstetrics"),
    DERMATOLOGY("Dermatology"),
    PSYCHIATRY("Psychiatry"),
    ONCOLOGY("Oncology"),
    OPHTHALMOLOGY("Ophthalmology"),
    INTERNAL_MEDICINE("Internal Medicine");

    private final String displayName;

    Specialty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Búsqueda lineal sobre values() — tolera variaciones de capitalización desde la vista con equalsIgnoreCase
    public static Specialty fromDisplayName(String display) {
        for (Specialty s : values()) {
            if (s.displayName.equalsIgnoreCase(display)) return s;
        }
        // Falla explícitamente si el string no corresponde a ninguna especialidad válida — el Controller lo captura y retorna BAD_REQUEST
        throw new IllegalArgumentException("Unknown specialty: " + display);
    }

    // fromJson maneja alias abreviados que pueden venir de fuentes externas o JSON legacy; casos no listados caen al valueOf estándar
    public static Specialty fromJson(String value) {
        return switch (value.toUpperCase()) {
            case "ORTHOPEDICS"         -> TRAUMATOLOGY_ORTHOPEDICS;
            case "GYNECOLOGY"          -> GYNECOLOGY_OBSTETRICS;
            case "GENERAL_MEDICINE"    -> GENERAL_MEDICINE;
            case "CARDIOLOGY"          -> CARDIOLOGY;
            case "PEDIATRICS"          -> PEDIATRICS;
            case "NEUROLOGY"           -> NEUROLOGY;
            case "DERMATOLOGY"         -> DERMATOLOGY;
            case "PSYCHIATRY"          -> PSYCHIATRY;
            case "ONCOLOGY"            -> ONCOLOGY;
            case "OPHTHALMOLOGY"       -> OPHTHALMOLOGY;
            case "INTERNAL_MEDICINE"   -> INTERNAL_MEDICINE;
            // El default delega en valueOf para no duplicar el mapeo de los casos que ya coinciden con el nombre del enum
            default -> valueOf(value.toUpperCase());
        };
    }
}