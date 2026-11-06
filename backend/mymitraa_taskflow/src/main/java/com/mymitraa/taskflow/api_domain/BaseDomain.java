package com.mymitraa.taskflow.api_domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;

public abstract class BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}