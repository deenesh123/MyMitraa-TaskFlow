package com.mymitraa.taskflow.api_domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object representing dropdown / search parameter lists
 * used for advance search filters across TaskFlow modules.
 */
public class AdvanceSearchList implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> ticketNumbers = new ArrayList<>();
    private List<String> customers = new ArrayList<>();
    private List<String> projects = new ArrayList<>();
    private List<String> modules = new ArrayList<>();
    private List<String> subModules = new ArrayList<>();
    private List<String> epics = new ArrayList<>();
    private List<String> statuses = new ArrayList<>();
    private List<String> priorities = new ArrayList<>();
    private List<String> teams = new ArrayList<>();
    private List<String> taskOwners = new ArrayList<>();
    private List<String> assignedBys = new ArrayList<>();
    private List<String> taskTypes = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AdvanceSearchList list = new AdvanceSearchList();

        public Builder ticketNumbers(List<String> ticketNumbers) {
            list.setTicketNumbers(ticketNumbers);
            return this;
        }

        public Builder statuses(List<String> statuses) {
            list.setStatuses(statuses);
            return this;
        }

        public Builder projects(List<String> projects) {
            list.setProjects(projects);
            return this;
        }

        public Builder modules(List<String> modules) {
            list.setModules(modules);
            return this;
        }

        public AdvanceSearchList build() {
            return list;
        }
    }

    public List<String> getTicketNumbers() {
        return ticketNumbers;
    }

    public void setTicketNumbers(List<String> ticketNumbers) {
        this.ticketNumbers = ticketNumbers;
    }

    public AdvanceSearchList() {
    }

    public List<String> getCustomers() {
        return customers;
    }

    public void setCustomers(List<String> customers) {
        this.customers = customers;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public List<String> getSubModules() {
        return subModules;
    }

    public void setSubModules(List<String> subModules) {
        this.subModules = subModules;
    }

    public List<String> getEpics() {
        return epics;
    }

    public void setEpics(List<String> epics) {
        this.epics = epics;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public List<String> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<String> priorities) {
        this.priorities = priorities;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public List<String> getTaskOwners() {
        return taskOwners;
    }

    public void setTaskOwners(List<String> taskOwners) {
        this.taskOwners = taskOwners;
    }

    public List<String> getAssignedBys() {
        return assignedBys;
    }

    public void setAssignedBys(List<String> assignedBys) {
        this.assignedBys = assignedBys;
    }

    public List<String> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<String> taskTypes) {
        this.taskTypes = taskTypes;
    }
}
