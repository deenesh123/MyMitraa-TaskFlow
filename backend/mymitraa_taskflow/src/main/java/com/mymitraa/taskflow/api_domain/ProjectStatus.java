package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project_status")
public class ProjectStatus extends BaseDomain {

    private String title;
    private String description;
    private String status;
    private String owner;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}