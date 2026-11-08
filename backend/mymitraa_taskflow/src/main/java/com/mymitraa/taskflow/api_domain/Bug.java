package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bugs")
public class Bug extends BaseDomain {

    private String bugCode;
    private String title;
    private String project;
    private String module;
    private String severity; // Critical, High, Medium, Low
    private String priority; // Critical P0, Urgent P1, Normal P2, Low P3
    private String status;   // Open, Assigned, In Progress, Fixed, Ready for Retest, Retest Passed, Reopened, Closed
    private String reportedBy;
    private String assignedTo;
    private String linkedTaskId;
    private String reproductionSteps;
    private String expectedResult;
    private String actualResult;
    private String environment;
    private String createdAt;

    public String getBugCode() {
        return bugCode;
    }

    public void setBugCode(String bugCode) {
        this.bugCode = bugCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getLinkedTaskId() {
        return linkedTaskId;
    }

    public void setLinkedTaskId(String linkedTaskId) {
        this.linkedTaskId = linkedTaskId;
    }

    public String getReproductionSteps() {
        return reproductionSteps;
    }

    public void setReproductionSteps(String reproductionSteps) {
        this.reproductionSteps = reproductionSteps;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
