package com.mymitraa.taskflow.api_domain;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "subtask")
public class Subtask extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("subtask_id")
    private String subtaskId;

    @Field("ticket_number")
    private String ticketNumber;

    @Field("parent_ticket_number")
    private String parentTicketNumber;

    @Field("parent_feedback_history_id")
    private String parentFeedbackHistoryId;

    @Field("project")
    private String project;

    @Field("epic")
    private String epic;

    @Field("priority")
    private String priority;

    @Field("customer")
    private String customer;

    @Field("subject")
    private String subject;

    @Field("description")
    private String description;

    @Field("product")
    private String product;

    @Field("estimated_start_date")
    private Date estimatedStartDate;

    @Field("estimated_close_date")
    private Date estimatedCloseDate;

    @Field("actual_start_date")
    private Date actualStartDate;

    @Field("actual_close_date")
    private Date actualCloseDate;

    @Field("module")
    private String module;

    @Field("sub_module")
    private String subModule;

    @Field("team")
    private String team;

    @Field("task_type")
    private String taskType;

    @Field("classification")
    private String classification;

    @Field("coding")
    private Double coding = 0.0;

    @Field("ai_tool")
    private Double aiTool = 0.0;

    @Field("internal_meeting")
    private Double internalMeeting = 0.0;

    @Field("estimated_effort")
    private Double estimatedEffort = 0.0;

    @Field("actual_effort")
    private Double actualEffort;

    @Field("productivity_savings")
    private Double productivitySavings;

    @Field("task_owner")
    private String taskOwner;

    @Field("category")
    private String category;

    @Field("category_description")
    private String categoryDescription;

    @Field("task_description")
    private String taskDescription;

    @Field("user_stories")
    private String userStories;

    @Field("status")
    private String status;

    @Field("assigned_by")
    private String assignedBy;

    @Field("date_assigned")
    private Date dateAssigned;

    @Field("who_reported")
    private String whoReported;

    @Field("reviewer_comments")
    private String reviewerComments;

    @Field("reviewed_by")
    private String reviewedBy;

    @Field("work_log_entries")
    private List<WorkLogEntry> workLogEntries;

    @Field("stored_attachment_names")
    private List<String> storedAttachmentNames;

    public String getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(String subtaskId) {
        this.subtaskId = subtaskId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getParentTicketNumber() {
        return parentTicketNumber;
    }

    public void setParentTicketNumber(String parentTicketNumber) {
        this.parentTicketNumber = parentTicketNumber;
    }

    public String getParentFeedbackHistoryId() {
        return parentFeedbackHistoryId;
    }

    public void setParentFeedbackHistoryId(String parentFeedbackHistoryId) {
        this.parentFeedbackHistoryId = parentFeedbackHistoryId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
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

    public String getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
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

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getUserStories() {
        return userStories;
    }

    public void setUserStories(String userStories) {
        this.userStories = userStories;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
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

    public List<WorkLogEntry> getWorkLogEntries() {
        return workLogEntries;
    }

    public void setWorkLogEntries(List<WorkLogEntry> workLogEntries) {
        this.workLogEntries = workLogEntries;
    }

    public List<String> getStoredAttachmentNames() {
        return storedAttachmentNames;
    }

    public void setStoredAttachmentNames(List<String> storedAttachmentNames) {
        this.storedAttachmentNames = storedAttachmentNames;
    }
}
