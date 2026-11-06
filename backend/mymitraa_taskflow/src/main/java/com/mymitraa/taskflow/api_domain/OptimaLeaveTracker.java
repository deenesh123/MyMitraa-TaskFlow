package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "optima_leave_tracker")
public class OptimaLeaveTracker extends BaseDomain {

    private static final long serialVersionUID = 1L;

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

    @Field("day")
    private String day;

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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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
}