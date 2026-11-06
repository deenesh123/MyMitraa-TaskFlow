package com.mymitraa.taskflow.api_vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimaLeaveTrackerVM {
    private String id;
    private String name;
    private String role;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private String leaveStartDate;
    private String leaveEndDate;
    private String comments;
    private String status;
    private Double totalDays;
}
