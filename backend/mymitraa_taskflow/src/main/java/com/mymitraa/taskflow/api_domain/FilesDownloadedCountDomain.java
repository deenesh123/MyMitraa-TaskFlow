package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "files_downloaded_count")
public class FilesDownloadedCountDomain extends BaseDomain {

    private String fileName;
    private String category;
    private Date date;
    private String downloadedBy;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDownloadedBy() {
        return downloadedBy;
    }

    public void setDownloadedBy(String downloadedBy) {
        this.downloadedBy = downloadedBy;
    }
}
