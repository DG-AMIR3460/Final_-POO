package core.model.enums;

public enum RoomType {
    STANDARD,
    ICU,
    NICU,
    IMC,
    ISOLATION;

    // Los nombres del enum coinciden exactamente con los display names — por eso toDisplayName simplemente retorna name()
    public String toDisplayName() {
        return name();
    }

    // fromDisplayName centraliza la conversión de String a enum; el toUpperCase absorbe variaciones de capitalización desde la vista
    public static RoomType fromDisplayName(String name) {
        return valueOf(name.toUpperCase());
    }
}