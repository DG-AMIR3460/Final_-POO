package core.controller;

/**
 * Enumeración de códigos de estado HTTP utilizados en las respuestas del sistema.
 * Cada constante representa un estado estándar con su código numérico correspondiente.
 */
public enum StatusCode {
    OK(200),            // Operación exitosa
    BAD_REQUEST(400),   // Solicitud inválida o con datos incorrectos
    NOT_FOUND(404),     // Recurso no encontrado
    CONFLICT(409),      // Conflicto con el estado actual del recurso
    INTERNAL_ERROR(500); // Error interno del servidor

    private final int code;
    
    // Constructor
    StatusCode(int code) { this.code = code; }

    public int getCode() { return code; }
}
