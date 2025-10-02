package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiException {

    private final String message;
    private final int status;
    private final ZonedDateTime zonedDateTime;

    public ApiException(String message, HttpStatus status, ZonedDateTime zonedDateTime) {
        this.message = message;
        this.status = status.value();
        this.zonedDateTime = zonedDateTime;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }
}
