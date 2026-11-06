package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;

public class LeaveSummaryVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String role;
    private int plannedLeave;
    private int sickLeave;
    private int wfhApproved;
    private int wfhUnapproved;
    private int unplannedLeave;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPlannedLeave() {
        return plannedLeave;
    }

    public void setPlannedLeave(int plannedLeave) {
        this.plannedLeave = plannedLeave;
    }

    public int getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(int sickLeave) {
        this.sickLeave = sickLeave;
    }

    public int getWfhApproved() {
        return wfhApproved;
    }

    public void setWfhApproved(int wfhApproved) {
        this.wfhApproved = wfhApproved;
    }

    public int getWfhUnapproved() {
        return wfhUnapproved;
    }

    public void setWfhUnapproved(int wfhUnapproved) {
        this.wfhUnapproved = wfhUnapproved;
    }

    public int getUnplannedLeave() {
        return unplannedLeave;
    }

    public void setUnplannedLeave(int unplannedLeave) {
        this.unplannedLeave = unplannedLeave;
    }
}
