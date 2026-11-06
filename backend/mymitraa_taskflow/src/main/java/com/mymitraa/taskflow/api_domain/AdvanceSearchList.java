package com.mymitraa.taskflow.api_domain;

import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "advance_search_list")
public class AdvanceSearchList extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Field("table_name")
    private String tableName;

    @Field("parameter_list")
    private List<Map<String, String>> parameterList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Map<String, String>> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Map<String, String>> parameterList) {
        this.parameterList = parameterList;
    }
}
