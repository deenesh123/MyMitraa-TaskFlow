package com.mymitraa.taskflow.api_domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "optima_leave_tracker")
public class OptimaLeaveTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("employee_name")
    private String employeeName;

    @Field("role")
    private String role;

    @Field("leave_type")
    private String leaveType;

    @Field("start_date")
    private Date startDate;

    @Field("end_date")
    private Date endDate;

    @Field("status")
    private String status;

    @Field("total_days")
    private Double totalDays;

    @Field("approved_by")
    private String approvedBy;

    @Field("approved_date")
    private Date approvedDate;

    @Field("comments")
    private String comments;

    @Field("delete_flag")
    private Boolean deleteFlag = false;

    @Field("created_on")
    private Date createdOn;

    @Field("created_by")
    private String createdBy;

    @Field("modified_date_time")
    private Date modifiedDateTime;

    @Field("modified_by")
    private String modifiedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : employeeName;
    }

    public void setName(String name) {
        this.name = name;
        if (this.employeeName == null) this.employeeName = name;
    }

    public String getEmployeeName() {
        return employeeName != null ? employeeName : name;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        if (this.name == null) this.name = employeeName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Double totalDays) {
        this.totalDays = totalDays;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(Date modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}