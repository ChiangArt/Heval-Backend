package com.heval.ecommerce.exception;


import com.heval.ecommerce.dto.response.ApiExceptionResponse;
import com.heval.ecommerce.utility.ConstanUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class ResponseExceptionHandler {

    // EXCEPCION GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleGlobalException(Exception ex, WebRequest request) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ConstanUtility.ERROR_MESSAGE,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // MANEJO DE EXCEPCION DE VALIDACION
    @ExceptionHandler(ApiValidateException.class)
    public ResponseEntity<ApiExceptionResponse> handleApiValidate(ApiValidateException ex, HttpServletRequest request) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                ConstanUtility.ERROR_CODE,
                ConstanUtility.ERROR_MESSAGE,
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ERROR DE VALIDACION (DTOs con @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiExceptionResponse response = new ApiExceptionResponse(
                ConstanUtility.ERROR_CODE,
                ConstanUtility.ERROR_MESSAGE,
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
