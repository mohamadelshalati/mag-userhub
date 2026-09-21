package com.mag.app;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String method,
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errors
) {
    public static ApiError of(String method, int status, String error, String message, String path) {
        return new ApiError(method, Instant.now(), status, error, message, path, null);
    }

    public static ApiError of(String method, int status, String error, String message, String path, Map<String, String> errors) {
        return new ApiError(method, Instant.now(), status, error, message, path, errors);
    }
}