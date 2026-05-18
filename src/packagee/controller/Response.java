package packagee.controller;

public class Response {

    private final StatusCode statusCode;
    private final String message;
    private final Object data;

    public Response(StatusCode statusCode, String message) {
        this(statusCode, message, null);
    }

    public Response(StatusCode statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public StatusCode getStatusCode() { return statusCode; }
    public String getMessage()        { return message; }
    public Object getData()           { return data; }
    public boolean isOk()             { return statusCode == StatusCode.OK; }
}
