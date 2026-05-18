package core.controller;

public enum StatusCode {
    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_ERROR(500);

    private final int code;

    StatusCode(int code) { this.code = code; }

    public int getCode() { return code; }
}
