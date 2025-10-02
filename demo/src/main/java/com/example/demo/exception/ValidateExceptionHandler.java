package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ValidateExceptionHandler {

    @ExceptionHandler(value =  StudentValidateException.class)
    public ResponseEntity<Object> handleStudentValidateException (StudentValidateException e) {
        // create payload containing exception details
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now());

        // return response entity
        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST);
    }
}
