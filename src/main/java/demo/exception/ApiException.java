package demo.exception;

public class ApiException extends RuntimeException{
    private final String message;
    private final int status;

    public ApiException(String message, int status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
    
}
// This class extends RuntimeException to represent a custom API exception.
// It includes a message and an HTTP status code.
