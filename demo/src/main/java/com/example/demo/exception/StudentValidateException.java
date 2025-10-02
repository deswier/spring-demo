package com.example.demo.exception;

public class StudentValidateException extends Exception {

    public StudentValidateException() {
    }

    public StudentValidateException(String msg) {
        super(msg);
    }

    public StudentValidateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public StudentValidateException(Throwable throwable) {
        super(throwable);
    }

}
