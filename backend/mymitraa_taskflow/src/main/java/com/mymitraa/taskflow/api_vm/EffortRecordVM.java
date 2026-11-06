package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import java.util.Date;

public class EffortRecordVM implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String team;
    private Date startDate;
    private Date endDate;
    private Double totalHours;

    public EffortRecordVM() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }
}
