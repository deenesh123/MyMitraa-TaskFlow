package com.mymitraa.taskflow.api_errors;

public class RegisteredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegisteredException() {
        super();
    }

    public RegisteredException(String message) {
        super(message);
    }

    public RegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisteredException(Throwable cause) {
        super(cause);
    }
}