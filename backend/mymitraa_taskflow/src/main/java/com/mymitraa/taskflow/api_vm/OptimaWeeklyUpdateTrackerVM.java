package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;

/**
 * View model for Optima Weekly Update Tracker requests.
 */
public class OptimaWeeklyUpdateTrackerVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String projectName;
    private String customer;
    private String projectManager;
    private String projectLead;
    private String status;
    private String overallStatus;
    private String weekNumber;
    private String milestonesCompleted;
    private String keyHighlights;
    private String challenges;
    private String nextWeekPlan;
    private String ragStatus;
    private String comments;
    private MultipartFile[] files;

    public OptimaWeeklyUpdateTrackerVM() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public String getProjectLead() {
        return projectLead;
    }

    public void setProjectLead(String projectLead) {
        this.projectLead = projectLead;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getMilestonesCompleted() {
        return milestonesCompleted;
    }

    public void setMilestonesCompleted(String milestonesCompleted) {
        this.milestonesCompleted = milestonesCompleted;
    }

    public String getKeyHighlights() {
        return keyHighlights;
    }

    public void setKeyHighlights(String keyHighlights) {
        this.keyHighlights = keyHighlights;
    }

    public String getChallenges() {
        return challenges;
    }

    public void setChallenges(String challenges) {
        this.challenges = challenges;
    }

    public String getNextWeekPlan() {
        return nextWeekPlan;
    }

    public void setNextWeekPlan(String nextWeekPlan) {
        this.nextWeekPlan = nextWeekPlan;
    }

    public String getRagStatus() {
        return ragStatus;
    }

    public void setRagStatus(String ragStatus) {
        this.ragStatus = ragStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
