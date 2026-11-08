package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class FeedbackHistoryVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String feedbackHistoryId;
    private String comments;
    private String subject;
    private String estimatedEffort;
    private String estimatedStartDate;
    private String estimatedCloseDate;
    private String actualStartDate;
    private String actualCloseDate;
    private String team;
    private String taskOwner;
    private String taskType;
    private String description;
    private String taskDescription;
    private String priority;
    private String epic;
    private String epicId;
    private String customer;
    private String module;
    private String project;
    private String subModule;
    private String userStories;
    private String fileName;
    private String classification;
    private String product;
    private String category;
    private String categoryDescription;
    private String whoReported;
    private String status;
    private String reviewedBy;
    private String reviewerComments;
    private String assignedBy;
    private Double coding;
    private Double aiTool;
    private Double internalMeeting;
    private Double actualEffort;
    private List<MultipartFile> attachment;

    public Double getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Double actualEffort) {
        this.actualEffort = actualEffort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeedbackHistoryId() {
        return feedbackHistoryId;
    }

    public void setFeedbackHistoryId(String feedbackHistoryId) {
        this.feedbackHistoryId = feedbackHistoryId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(String actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public String getActualCloseDate() {
        return actualCloseDate;
    }

    public void setActualCloseDate(String actualCloseDate) {
        this.actualCloseDate = actualCloseDate;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public String getEpicId() {
        return epicId;
    }

    public void setEpicId(String epicId) {
        this.epicId = epicId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSubModule() {
        return subModule;
    }

    public void setSubModule(String subModule) {
        this.subModule = subModule;
    }

    public String getUserStories() {
        return userStories;
    }

    public void setUserStories(String userStories) {
        this.userStories = userStories;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public String getWhoReported() {
        return whoReported;
    }

    public void setWhoReported(String whoReported) {
        this.whoReported = whoReported;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Double getCoding() {
        return coding;
    }

    public void setCoding(Double coding) {
        this.coding = coding;
    }

    public Double getAiTool() {
        return aiTool;
    }

    public void setAiTool(Double aiTool) {
        this.aiTool = aiTool;
    }

    public Double getInternalMeeting() {
        return internalMeeting;
    }

    public void setInternalMeeting(Double internalMeeting) {
        this.internalMeeting = internalMeeting;
    }

    public List<MultipartFile> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<MultipartFile> attachment) {
        this.attachment = attachment;
    }
}
