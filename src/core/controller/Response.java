package core.controller;

/**
 * Representa la respuesta estándar retornada por los controladores del sistema.
 * Encapsula un código de estado, un mensaje descriptivo y opcionalmente datos adicionales.
 */
public class Response {

    private final StatusCode statusCode;
    private final String message;
    private final Object data;
    
    // Constructor para respuestas sin datos adicionales
    public Response(StatusCode statusCode, String message) {
        this(statusCode, message, null);
    }
    
    // Constructor para respuestas con datos adicionales
    public Response(StatusCode statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
    
    //Getters y Indicativo del status
    public StatusCode getStatusCode() { return statusCode; }
    public String getMessage()        { return message; }
    public Object getData()           { return data; }
    public boolean isOk()             { return statusCode == StatusCode.OK; }
}
