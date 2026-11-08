package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MeetingVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "Meeting title is required")
    @Size(max = 200, message = "Meeting title must not exceed 200 characters")
    private String title;

    private String description;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}