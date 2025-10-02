package com.example.demo.exception;

import java.util.List;

public class StudentValidateException extends RuntimeException {

    private final List<ViolationFieldError> fields;

    public StudentValidateException(String message) {
        super(message);
        this.fields = null;
    }

    public StudentValidateException(String message, List<ViolationFieldError> fields) {
        super(message);
        this.fields = fields;
    }

    public StudentValidateException(String message, String field) {
        super(message);

        ViolationFieldError fieldError = new ViolationFieldError(field, message);

        this.fields = List.of(fieldError);
    }

    public List<ViolationFieldError> getFields() {
        return fields;
    }

}
