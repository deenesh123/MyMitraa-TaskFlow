package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import java.util.List;

public class AssignTaskVM implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ticketNumber;
    private String taskOwner;
    private String assignedBy;
    private List<String> taskIds;

    public AssignTaskVM() {}

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public String getTaskOwner() { return taskOwner; }
    public void setTaskOwner(String taskOwner) { this.taskOwner = taskOwner; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public List<String> getTaskIds() { return taskIds; }
    public void setTaskIds(List<String> taskIds) { this.taskIds = taskIds; }
}
