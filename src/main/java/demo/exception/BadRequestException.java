package demo.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message, 400);
    }
}
// This class extends ApiException to represent a 400 Bad Request error.
// It provides a constructor that takes a message and sets the HTTP status code to 400.