package com.mymitraa.taskflow.api_vm;

import java.io.Serializable;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

public class GlobalFileUploadVM implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String category;
    private String description;
    private Date inspectionDate;
    private Date operationDateTime;
    private MultipartFile file;

    public GlobalFileUploadVM() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(Date inspectionDate) { this.inspectionDate = inspectionDate; }
    public Date getOperationDateTime() { return operationDateTime; }
    public void setOperationDateTime(Date operationDateTime) { this.operationDateTime = operationDateTime; }
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
