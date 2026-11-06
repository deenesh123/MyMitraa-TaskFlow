package com.mymitraa.taskflow.api_domain;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "meeting_time_tracker")
public class MeetingTimeTracker extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("meeting_id")
    private String meetingId;

    @Field("meeting_by")
    private String meetingBy;

    @Field("customer")
    private String customer;

    @Field("project")
    private String project;

    @Field("team")
    private String team;

    @Field("module")
    private String module;

    @Field("category")
    private String category;

    @Field("date")
    private Date date;

    @Field("total_time")
    private Double totalTime;

    @Field("duration_minutes")
    private Double durationMinutes;

    @Field("comments")
    private String comments;

    @Field("meeting_link")
    private String meetingLink;

    @Field("status")
    private String status;

    @Field("attendee")
    private String attendee;

    @Field("check_in_time")
    private Date checkInTime;

    @Field("check_out_time")
    private Date checkOutTime;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingBy() {
        return meetingBy;
    }

    public void setMeetingBy(String meetingBy) {
        this.meetingBy = meetingBy;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public Double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttendee() {
        return attendee;
    }

    public void setAttendee(String attendee) {
        this.attendee = attendee;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}