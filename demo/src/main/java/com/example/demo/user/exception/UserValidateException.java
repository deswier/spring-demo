package com.example.demo.user.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class UserValidateException extends RuntimeException {

    private final List<String> args;

    public UserValidateException(String message) {
        super(message);
        this.args = null;
    }

    public UserValidateException(String message, List<String> args) {
        super(message);
        this.args = args;
    }

}
