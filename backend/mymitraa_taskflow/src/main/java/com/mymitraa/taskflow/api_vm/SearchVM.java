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
}