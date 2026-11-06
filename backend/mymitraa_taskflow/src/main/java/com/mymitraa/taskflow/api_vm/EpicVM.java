package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;

public class EpicVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subject;
    private String description;
    private String team;
    private String product;
    private String module;
    private String taskType;
    private String classification;
    private String estimatedEffort;
    private String estimatedStartDate;
    private String estimatedCloseDate;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getEstimatedEffort() {
        return estimatedEffort;
    }

    public void setEstimatedEffort(String estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }

    public String getEstimatedStartDate() {
        return estimatedStartDate;
    }

    public void setEstimatedStartDate(String estimatedStartDate) {
        this.estimatedStartDate = estimatedStartDate;
    }

    public String getEstimatedCloseDate() {
        return estimatedCloseDate;
    }

    public void setEstimatedCloseDate(String estimatedCloseDate) {
        this.estimatedCloseDate = estimatedCloseDate;
    }
}
