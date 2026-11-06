package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "task_owner_email")
public class TaskOwnerEmail extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("task_owner")
    private String taskOwner;

    @Field("email")
    private String email;

    @Field("role")
    private String role;

    @Field("team")
    private String team;

    @Field("status")
    private String status;

    public String getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
