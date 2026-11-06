package com.mymitraa.taskflow.api_domain;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "files_downloaded_count")
public class FilesDownloadedCountDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String fileId;
    private String fileName;
    private String downloadedBy;
    private Date downloadedAt;
    private Integer count = 1;

    public FilesDownloadedCountDomain() {
        this.downloadedAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadedBy() {
        return downloadedBy;
    }

    public void setDownloadedBy(String downloadedBy) {
        this.downloadedBy = downloadedBy;
    }

    public Date getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(Date downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
