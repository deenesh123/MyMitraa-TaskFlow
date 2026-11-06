package com.mymitraa.taskflow.api_successcode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard API response wrapper for MyMitraa TaskFlow REST controllers.
 * Mirrors the legacy PM-TOOL CustomResponse contract.
 */
public class CustomResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String successCode;
    private String successMessage;
    private Object data;
    private Map<String, ?> metaInfo;
    private int code = 200;
    private String message;

    public CustomResponse() {
        this.metaInfo = new HashMap<>();
    }

    public CustomResponse(String successCode, String successMessage, Object data) {
        this.successCode = successCode;
        this.successMessage = successMessage;
        this.data = data;
        this.message = successCode;
        this.metaInfo = new HashMap<>();
    }

    public CustomResponse(String successCode, String successMessage, Object data, Map<String, ?> metaInfo) {
        this.successCode = successCode;
        this.successMessage = successMessage;
        this.data = data;
        this.metaInfo = metaInfo != null ? metaInfo : new HashMap<>();
        this.message = successCode;
    }

    public CustomResponse(SuccessCode successCode, Object data) {
        if (successCode != null) {
            this.successCode = successCode.getSuccessCode();
            this.successMessage = successCode.getSuccessMessage();
            this.message = successCode.getSuccessCode();
        }
        this.data = data;
        this.metaInfo = new HashMap<>();
    }

    public CustomResponse(SuccessCode successCode, Object data, Map<String, ?> metaInfo) {
        if (successCode != null) {
            this.successCode = successCode.getSuccessCode();
            this.successMessage = successCode.getSuccessMessage();
            this.message = successCode.getSuccessCode();
        }
        this.data = data;
        this.metaInfo = metaInfo != null ? metaInfo : new HashMap<>();
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, ?> getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(Map<String, ?> metaInfo) {
        this.metaInfo = metaInfo;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
