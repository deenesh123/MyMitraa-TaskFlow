package com.mymitraa.taskflow.api_errors;

public class CustomParameterizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String exception;
    private String message;
    private String errorCode;

    public CustomParameterizedException(String exception, String message, String errorCode) {
        super(message);
        this.exception = exception;
        this.message = message;
        this.errorCode = errorCode;
    }

    public CustomParameterizedException(String message) {
        super(message);
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
