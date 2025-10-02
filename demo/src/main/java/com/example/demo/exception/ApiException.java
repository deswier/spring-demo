package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiException {
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final OffsetDateTime timestamp = OffsetDateTime.now();
    private final List<ViolationFieldError> fields;

    public ApiException(int status, String error, String message, String path, List<ViolationFieldError> fields) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fields = fields;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public List<ViolationFieldError> getFields() {
        return fields;
    }

}
