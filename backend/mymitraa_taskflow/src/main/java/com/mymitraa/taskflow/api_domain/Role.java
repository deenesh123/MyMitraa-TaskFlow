package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role extends BaseDomain {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}