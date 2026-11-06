package com.mymitraa.taskflow.api_successcode;

import java.io.Serializable;
import java.util.Map;

public class CustomResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int responseCode;
    private String responseMessage;
    private Object response;
    private Map<String, String> metaInfo;

    public CustomResponse() {
    }

    public CustomResponse(Object response) {
        this.responseCode = 200;
        this.responseMessage = "Success";
        this.response = response;
    }

    public CustomResponse(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public CustomResponse(int responseCode, String responseMessage, Object response) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.response = response;
    }

    public CustomResponse(int responseCode, String responseMessage, Object response, Map<String, String> metaInfo) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.response = response;
        this.metaInfo = metaInfo;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Map<String, String> getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(Map<String, String> metaInfo) {
        this.metaInfo = metaInfo;
    }
}
