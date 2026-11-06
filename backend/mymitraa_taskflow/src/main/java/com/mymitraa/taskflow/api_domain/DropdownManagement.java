package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dropdown_management")
public class DropdownManagement extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("domain_name")
    private String domainName;

    @Field("value")
    private String value;

    @Field("sub_value")
    private String subValue;

    @Field("module")
    private String module;

    @Field("sub_module")
    private String subModule;

    @Field("comments")
    private String comments;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSubValue() {
        return subValue;
    }

    public void setSubValue(String subValue) {
        this.subValue = subValue;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSubModule() {
        return subModule;
    }

    public void setSubModule(String subModule) {
        this.subModule = subModule;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}