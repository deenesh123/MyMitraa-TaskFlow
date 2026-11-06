package com.mymitraa.taskflow.api_errors;

public enum CustomExceptionCode {
    RECORD_NOT_FOUND("RECORD_NOT_FOUND", "Requested entity not found"),
    VALIDATION_FAILED("VALIDATION_FAILED", "Validation constraints violated"),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "User is not authorized to perform this operation"),
    DATABASE_ERROR("DATABASE_ERROR", "Underlying database operation failed");

    private final String code;
    private final String message;

    CustomExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
