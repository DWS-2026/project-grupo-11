package es.footleague.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. ERROR 404 - Resource not found
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} from IP {}", request.getRequestURI(), request.getRemoteAddr());
        return buildResponse(HttpStatus.NOT_FOUND, "The requested resource does not exist.");
    }

    // 2. ERROR 403 - Access Denied (Important for your security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "ANONYMOUS";
        log.warn("SECURITY: Access denied to {} from IP {} - User: {}", request.getRequestURI(), request.getRemoteAddr(), username);
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.");
    }

    // 3. ERROR 405 - Method not allowed (e.g. POST where only GET exists)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not allowed: {} {} from IP {}", ex.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, 
            "The " + ex.getMethod() + " method is not allowed for this endpoint.");
    }

    // 4. ERROR 400 - Validation failure (Bean Validation)
    // Triggered when @RequestBody fails @NotNull, @Min, etc. annotations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error at {} from IP {}: {}", request.getRequestURI(), request.getRemoteAddr(), details);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation error: " + details);
    }

    // 5. ERROR 400 - Illegal or malformed arguments
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument at {} from IP {}: {}", request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 6. ERROR 500 - Generic unhandled error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalError(Exception ex, HttpServletRequest request) {
        log.error("CRITICAL: Unexpected error at {} from IP {}: {}", request.getRequestURI(), request.getRemoteAddr(), ex.getMessage(), ex);
        // In production it is not recommended to show the real exception message for security reasons
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    // Helper method to avoid code repetition
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", java.time.LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}