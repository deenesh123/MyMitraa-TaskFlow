package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import jakarta.validation.constraints.Size;

public class SearchVM implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 100)
    private String search;

    @Size(max = 50)
    private String sortBy;

    private String sortDir;
    private String sortName;
    private String sortType;
    private String parameter;
    private String parametervalue;
    private String startDate;
    private String endDate;
    private String date1;
    private String date2;
    private String status;
    private String category;
    private String collection;
    private String collectionName;
    private String customerGeography;
    private String projectStatus;
    private String priority;
    private String project;
    private String customer;
    private String task_owner;
    private String team;
    private boolean enablePagination = true;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParametervalue() {
        return parametervalue;
    }

    public void setParametervalue(String parametervalue) {
        this.parametervalue = parametervalue;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCustomerGeography() {
        return customerGeography;
    }

    public void setCustomerGeography(String customerGeography) {
        this.customerGeography = customerGeography;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isEnablePagination() {
        return enablePagination;
    }

    public void setEnablePagination(boolean enablePagination) {
        this.enablePagination = enablePagination;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTask_owner() {
        return task_owner;
    }

    public void setTask_owner(String task_owner) {
        this.task_owner = task_owner;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}