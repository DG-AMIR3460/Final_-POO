package main.model.enums;

public enum RoomType {
    STANDARD,
    ICU,
    NICU,
    IMC,
    ISOLATION;

    public String toDisplayName() {
        return name();
    }

    public static RoomType fromDisplayName(String name) {
        return valueOf(name.toUpperCase());
    }
}
