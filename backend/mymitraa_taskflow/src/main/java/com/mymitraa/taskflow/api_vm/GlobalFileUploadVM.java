package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

public class GlobalFileUploadVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private MultipartFile multipartFile;
    private String category;
    private String fileName;
    private String filePath;
    private String fileType;
    private String description;
    private Date operationDateTime;
    private boolean approved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOperationDateTime() {
        return operationDateTime;
    }

    public void setOperationDateTime(Date operationDateTime) {
        this.operationDateTime = operationDateTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
