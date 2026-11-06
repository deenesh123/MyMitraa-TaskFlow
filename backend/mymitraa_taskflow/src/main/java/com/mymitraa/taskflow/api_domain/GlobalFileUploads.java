package com.mymitraa.taskflow.api_domain;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "global_file_uploads")
public class GlobalFileUploads extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("category")
    private String category;

    @Field("operation_date_time")
    private Date operationDateTime;

    @Field("file_name")
    private String fileName;

    @Field("original_file_name")
    private String originalFileName;

    @Field("file_type")
    private String fileType;

    @Field("file_path")
    private String filePath;

    @Field("uploaded_by")
    private String uploadedBy;

    @Field("status")
    private String status;

    @Field("approved")
    private Boolean approved = false;

    @Field("approved_by")
    private String approvedBy;

    @Field("approved_date")
    private Date approvedDate;

    @Field("rejected_by")
    private String rejectedBy;

    @Field("rejection_date")
    private Date rejectionDate;

    @Field("description")
    private String description;

    @Field("index")
    private Double index;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getOperationDateTime() {
        return operationDateTime;
    }

    public void setOperationDateTime(Date operationDateTime) {
        this.operationDateTime = operationDateTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public Date getRejectionDate() {
        return rejectionDate;
    }

    public void setRejectionDate(Date rejectionDate) {
        this.rejectionDate = rejectionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getIndex() {
        return index;
    }

    public void setIndex(Double index) {
        this.index = index;
    }
}