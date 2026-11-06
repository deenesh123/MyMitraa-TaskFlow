package com.mymitraa.taskflow.api_domain;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "email_recipients")
public class EmailRecipients extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("operation")
    private String operation;

    @Field("recipients")
    private List<String> recipients;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}
