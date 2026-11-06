package com.mymitraa.taskflow.api_successcode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard API response wrapper matching original PM-TOOL / MyMitraa TaskFlow specification.
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

    public CustomResponse(String successCode, String successMessage, Object data, Map<String, ?> metaInfo) {
        this.successCode = successCode;
        this.successMessage = successMessage;
        this.data = data;
        this.metaInfo = metaInfo != null ? metaInfo : new HashMap<>();
        this.code = 200;
        this.message = successCode;
    }

    public CustomResponse(String successCode, String successMessage, Object data) {
        this(successCode, successMessage, data, new HashMap<>());
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
        this.message = successCode;
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
