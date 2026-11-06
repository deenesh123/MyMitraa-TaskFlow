package com.mymitraa.taskflow.api_domain;

import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "feedback")
public class FeedbackHistory extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Id
    private String feedbackHistoryId;

    @Field("ticket_number")
    private String ticketNumber;

    @Field("subject")
    private String subject;

    @Field("comments")
    private String comments;

    @Field("description")
    private String description;

    @Field("status")
    private String status;

    @Field("priority")
    private String priority;

    @Field("project")
    private String project;

    @Field("module")
    private String module;

    @Field("sub_module")
    private String subModule;

    @Field("epic")
    private String epic;

    @Field("customer")
    private String customer;

    @Field("user_stories")
    private String userStories;

    @Field("team")
    private String team;

    @Field("task_type")
    private String taskType;

    @Field("task_owner")
    private String taskOwner;

    @Field("assigned_by")
    private String assignedBy;

    @Field("classification")
    private String classification;

    @Field("category")
    private String category;

    @Field("category_description")
    private String categoryDescription;

    @Field("estimated_effort")
    private Double estimatedEffort;

    @Field("actual_effort")
    private Double actualEffort;

    @Field("productivity_savings")
    private Double productivitySavings;

    @Field("who_reported")
    private String whoReported;

    @Field("reviewer_comments")
    private String reviewerComments;

    @Field("reviewed_by")
    private String reviewedBy;

    @Field("estimated_start_date")
    private Date estimatedStartDate;

    @Field("estimated_close_date")
    private Date estimatedCloseDate;

    @Field("actual_start_date")
    private Date actualStartDate;

    @Field("actual_close_date")
    private Date actualCloseDate;

    @Field("date_assigned")
    private Date dateAssigned;

    @Field("file_name")
    private String fileName;

    @Field("attachments")
    private List<String> attachments;

    @Field("work_log_entries")
    private List<WorkLogEntry> workLogEntries;

    @Field("uploaded")
    private Boolean uploaded;

    @Field("task_description")
    private String taskDescription;

    @Field("subtask_count")
    private Integer subtaskCount;

    @Field("last_subtask_update")
    private Date lastSubtaskUpdate;

    @Field("instance")
    private String instance;

    @Field("project_category")
    private String projectCategory;

    @Field("coding")
    private Double coding;

    @Field("ai_tool")
    private Double aiTool;

    @Field("internal_meeting")
    private Double internalMeeting;

    @Field("product")
    private String product;

    // Getters and setters for all fields
    public String getFeedbackHistoryId() {
        return feedbackHistoryId;
    }

    public void setFeedbackHistoryId(String feedbackHistoryId) {
        this.feedbackHistoryId = feedbackHistoryId;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getUserStories() {
        return userStories;
    }

    public void setUserStories(String userStories) {
        this.userStories = userStories;
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
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

    public Double getProductivitySavings() {
        return productivitySavings;
    }

    public void setProductivitySavings(Double productivitySavings) {
        this.productivitySavings = productivitySavings;
    }

    public String getWhoReported() {
        return whoReported;
    }

    public void setWhoReported(String whoReported) {
        this.whoReported = whoReported;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Date getEstimatedStartDate() {
        return estimatedStartDate;
    }

    public void setEstimatedStartDate(Date estimatedStartDate) {
        this.estimatedStartDate = estimatedStartDate;
    }

    public Date getEstimatedCloseDate() {
        return estimatedCloseDate;
    }

    public void setEstimatedCloseDate(Date estimatedCloseDate) {
        this.estimatedCloseDate = estimatedCloseDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Date getActualCloseDate() {
        return actualCloseDate;
    }

    public void setActualCloseDate(Date actualCloseDate) {
        this.actualCloseDate = actualCloseDate;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public List<WorkLogEntry> getWorkLogEntries() {
        return workLogEntries;
    }

    public void setWorkLogEntries(List<WorkLogEntry> workLogEntries) {
        this.workLogEntries = workLogEntries;
    }

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Integer getSubtaskCount() {
        return subtaskCount;
    }

    public void setSubtaskCount(Integer subtaskCount) {
        this.subtaskCount = subtaskCount;
    }

    public Date getLastSubtaskUpdate() {
        return lastSubtaskUpdate;
    }

    public void setLastSubtaskUpdate(Date lastSubtaskUpdate) {
        this.lastSubtaskUpdate = lastSubtaskUpdate;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}