package com.heval.ecommerce.dto.response;

import java.time.LocalDateTime;

public record ApiExceptionResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ApiExceptionResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}