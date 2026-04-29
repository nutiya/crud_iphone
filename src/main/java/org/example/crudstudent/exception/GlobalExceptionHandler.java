package org.example.crudstudent.exception;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.example.crudstudent.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        Map<String, String> fieldErrors =new HashMap<>();
        for(FieldError error : ex.getBindingResult().getFieldErrors()){
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        400,
                        "validation failed",
                        fieldErrors,
                        request.getRequestURI()
                )
        );
    }



    private ResponseEntity<ApiResponse<Object>> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ){
        return ResponseEntity.status(status)
                .body(
                        new ApiResponse<>(
                                LocalDateTime.now(),
                                status.value(),
                                message,
                                null,
                                request.getRequestURI()
                        )
                );
    }
}
