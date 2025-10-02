package com.example.demo.exception;

import java.util.List;

public class StudentValidateException extends RuntimeException {

    public StudentValidateException(String message) {
        super(message);
        this.fields = null;
    }

    public StudentValidateException(String message, List<ViolationFieldError> fields) {
        super(message);
        this.fields = fields;
    }

    public List<ViolationFieldError> getFields() { return fields; }

    private final List<ViolationFieldError> fields;

}
