package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;

public class AssignTaskVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private String taskOwner;
    private String team;
    private String taskType;
    private String taskDescription;
    private String category;
    private String categoryDescription;
    private String classification;
    private String estimatedEffort;
    private String estimatedStartDate;
    private String actualStartDate;
    private String whoReported;
    private String epicId;
    private String priority;

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
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

    public String getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(String actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public String getWhoReported() {
        return whoReported;
    }

    public void setWhoReported(String whoReported) {
        this.whoReported = whoReported;
    }

    public String getEpicId() {
        return epicId;
    }

    public void setEpicId(String epicId) {
        this.epicId = epicId;
    }
}
