package packagee.model.enums;

public enum Specialty {
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

    public static Specialty fromDisplayName(String display) {
        for (Specialty s : values()) {
            if (s.displayName.equalsIgnoreCase(display)) return s;
        }
        throw new IllegalArgumentException("Unknown specialty: " + display);
    }

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
            default -> valueOf(value.toUpperCase());
        };
    }
}
