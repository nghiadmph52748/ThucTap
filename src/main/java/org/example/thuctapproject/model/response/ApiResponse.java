package org.example.thuctapproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    // Standardized response fields
    private int code;       // HTTP-like status code: 200, 400, 404, 500
    private String message; // human-readable message
    private T data;         // payload or error details

    // Convenience factories
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message != null ? message : "Success", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}