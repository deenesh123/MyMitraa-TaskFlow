package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.mymitraa.taskflow.api_domain.WorkLogEntry;

/**
 * View Model for Feedback History / Task requests.
 */
public class FeedbackHistoryVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String ticketNumber;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private String project;
    private String module;
    private String subModule;
    private String customer;
    private String team;
    private String taskOwner;
    private String assignedBy;
    private String estimatedStartDate;
    private String estimatedCloseDate;
    private Double estimatedEffort;
    private Double actualEffort;
    private String comments;
    private List<WorkLogEntry> workLogEntries;
    private MultipartFile[] files;

    public FeedbackHistoryVM() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public String getSubModule() {
        return subModule;
    }

    public void setSubModule(String subModule) {
        this.subModule = subModule;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
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

    public Double getEstimatedEffort() {
        return estimatedEffort;
    }

    public void setEstimatedEffort(Double estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }

    public Double getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Double actualEffort) {
        this.actualEffort = actualEffort;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<WorkLogEntry> getWorkLogEntries() {
        return workLogEntries;
    }

    public void setWorkLogEntries(List<WorkLogEntry> workLogEntries) {
        this.workLogEntries = workLogEntries;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
