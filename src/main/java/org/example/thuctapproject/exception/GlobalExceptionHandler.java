package org.example.thuctapproject.exception;

import org.example.thuctapproject.model.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handle @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> body = ApiResponse.of(400, "Validation failed", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle custom ApiException thrown in services/controllers
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        int code;
        try {
            code = Integer.parseInt(ex.getCode());
        } catch (Exception ignore) {
            code = 400;
        }
        HttpStatus status = switch (code) {
            case 404 -> HttpStatus.NOT_FOUND;
            case 403 -> HttpStatus.FORBIDDEN;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
        ApiResponse<Object> body = ApiResponse.error(code, ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    // Catch-all handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex) {
        ApiResponse<Object> body = ApiResponse.error(500, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}