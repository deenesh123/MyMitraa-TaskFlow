package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "meetings")
public class Meeting extends BaseDomain {

    private String title;
    private String description;
    private String owner;
    private Date date;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}