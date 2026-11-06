package com.mymitraa.taskflow.api_domain;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.mongodb.core.mapping.Field;

@Entity
@Table(name = "work_log_entries")
public class WorkLogEntry {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("feedback_history_id")
    private String feedbackHistoryId;

    @Field("work_log_date")
    private Date workLogDate;

    @Field("hours_worked")
    private Double hoursWorked;

    @Field("description")
    private String description;

    @Field("task_type")
    private String taskType;

    @Field("sub_task_id")
    private String subTaskId;

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

    public Date getWorkLogDate() {
        return workLogDate;
    }

    public void setWorkLogDate(Date workLogDate) {
        this.workLogDate = workLogDate;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }
}