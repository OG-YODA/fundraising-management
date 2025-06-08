package demo.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
// This class extends ApiException to represent a 404 Not Found error.
// It provides a constructor that takes a message and sets the HTTP status code to 404.
