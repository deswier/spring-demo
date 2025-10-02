package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleInvalid(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        List<ViolationFieldError> fields = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ViolationFieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ApiException body = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                req.getRequestURI(),
                fields);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(StudentValidateException.class)
    public ResponseEntity<ApiException> handleStudentValidate(StudentValidateException ex,
            HttpServletRequest req) {
        List<ViolationFieldError> fields = ex.getFields();

        ApiException body = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                fields);

        return ResponseEntity.badRequest().body(body);
    }
}
