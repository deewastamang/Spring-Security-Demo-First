package com.ecommerce.project.deewas.eShop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
class GlobalExceptionHandler {

    // Handles any generics exception
    @ExceptionHandler
    public ResponseEntity<CustomErrorResponse> handleException(Exception e) {
        //create a CustomErrorResponse
        CustomErrorResponse error = new CustomErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        //return response entity
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}