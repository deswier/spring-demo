package com.example.demo.exception;

public class ViolationFieldError {

    public ViolationFieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    private final String field;
    private final String message;

}
