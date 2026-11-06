package com.mymitraa.taskflow.api_domain;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public abstract class BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("delete_flag")
    private Boolean deleteFlag = false;

    @Field("created_by")
    private String createdBy;

    @Field("created_on")
    private Date createdOn;

    @Field("modified_by")
    private String modifiedBy;

    @Field("modified_date_time")
    private Date modifiedDateTime;

    @Field("deleted_by")
    private String deletedBy;

    @Field("deleted_on")
    private Date deletedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag != null ? deleteFlag : false;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(Date modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Date deletedOn) {
        this.deletedOn = deletedOn;
    }
}