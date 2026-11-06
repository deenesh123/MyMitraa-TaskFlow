package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "completion_files")
public class DCompletionFileInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("file_name")
    private String fileName;

    @Field("well_id")
    private String wellId; // Serves as ticketNumber reference

    @Field("category")
    private String category;

    @Field("comments")
    private String comments;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
