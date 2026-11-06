package com.mymitraa.taskflow.api_domain;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class WorkLogEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("date")
    private Date date;

    @Field("hours")
    private Double hours;

    @Field("description")
    private String description;

    @Field("comments")
    private String comments;

    @Field("created_by")
    private String createdBy;

    @Field("created_on")
    private Date createdOn;

    @Field("task_type")
    private String taskType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getWorkLogDate() {
        return date;
    }

    public void setWorkLogDate(Date workLogDate) {
        this.date = workLogDate;
    }

    public Double getHours() {
        return hours != null ? hours : 0.0;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHoursWorked() {
        return getHours();
    }

    public void setHoursWorked(Double hoursWorked) {
        setHours(hoursWorked);
    }

    public String getDescription() {
        return description != null ? description : comments;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.comments == null) this.comments = description;
    }

    public String getComments() {
        return comments != null ? comments : description;
    }

    public void setComments(String comments) {
        this.comments = comments;
        if (this.description == null) this.description = comments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}