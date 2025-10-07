package com.example.demo.exception;

import com.example.demo.student.exception.StudentValidateException;
import com.example.demo.user.exception.UserValidateException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleInvalid(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        List<ViolationFieldError> fields = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ViolationFieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        String message = messageSource.getMessage("validation.failed", null, LocaleContextHolder.getLocale());

        ApiException body = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
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

    @ExceptionHandler(UserValidateException.class)
    public ResponseEntity<ApiException> handleUserValidate(UserValidateException ex,
            HttpServletRequest req) {

        String message = messageSource.getMessage(ex.getMessage(), ex.getArgs().toArray(), ex.getMessage(), LocaleContextHolder.getLocale());

        ApiException body = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleGlobalEx(Exception ex,
            HttpServletRequest req) {

        ApiException body = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }
}
