package com.mymitraa.taskflow.api_domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter parameter options for advanced search dropdowns across TaskFlow modules.
 */
public class AdvanceSearchList implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> customerList = new ArrayList<>();
    private List<String> projectList = new ArrayList<>();
    private List<String> teamList = new ArrayList<>();
    private List<String> taskOwnerList = new ArrayList<>();
    private List<String> statusList = new ArrayList<>();
    private List<String> priorityList = new ArrayList<>();
    private List<String> moduleList = new ArrayList<>();

    public AdvanceSearchList() {
    }

    public List<String> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<String> customerList) {
        this.customerList = customerList;
    }

    public List<String> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<String> projectList) {
        this.projectList = projectList;
    }

    public List<String> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<String> teamList) {
        this.teamList = teamList;
    }

    public List<String> getTaskOwnerList() {
        return taskOwnerList;
    }

    public void setTaskOwnerList(List<String> taskOwnerList) {
        this.taskOwnerList = taskOwnerList;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public List<String> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<String> priorityList) {
        this.priorityList = priorityList;
    }

    public List<String> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<String> moduleList) {
        this.moduleList = moduleList;
    }
}
