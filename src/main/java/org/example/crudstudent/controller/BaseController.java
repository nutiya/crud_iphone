package org.example.crudstudent.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crudstudent.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDateTime;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> buildResponse(
            int status, String message, T data, String path
    ){
        return ResponseEntity.status(status).body(
                new ApiResponse<>(LocalDateTime.now(), status, message, data, path)
        );
    }


    protected <T> ResponseEntity<ApiResponse<T>> ok(String message, T data){
        return buildResponse(200, message, data, getCurrentPath());
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(String message, T data, URI location){
        return ResponseEntity
                .created(location)
                .body(new ApiResponse<>(LocalDateTime.now(), 201, message, data, getCurrentPath()));

    }


    private String getCurrentPath(){
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();

        return request.getRequestURI();
    }
}
