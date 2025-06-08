package demo.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("status", ex.getStatus());
        body.put("timestamp", Instant.now());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("status", 500);
        body.put("timestamp", Instant.now());
        return ResponseEntity.status(500).body(body);
    }
}
// This class is a global exception handler for the application.
// It uses @RestControllerAdvice to handle exceptions globally across all controllers.