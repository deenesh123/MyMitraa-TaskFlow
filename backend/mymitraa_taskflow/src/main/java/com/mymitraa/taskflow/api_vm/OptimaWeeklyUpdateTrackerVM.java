package com.mymitraa.taskflow.api_vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimaWeeklyUpdateTrackerVM {
    private String id;
    private String projectId;
    private String projectName;
    private String customerName;
    private String customerGeography;
    private String industry;
    private String projectStatus;
    private String status;
    private String priority;
    private String currentStatus;
    private String lastWeekStatus;
    private String currentState;
    private String projectSummery;
    private String risk;
    private String ktProvider;
    private String codeBase;
    private String devUrl;
    private String qaUrl;
    private String laUrl;
    private String otherUrl;
    private boolean devArrow;
    private boolean qaArrow;
    private boolean laArrow;
    private boolean otherArrow;
    private String documentation;
    private String hldLink;
    private String lldLink;
    private String hldAttachment;
    private String lldAttachment;
    private String totalEnv;
    private String ref;
    private Date startDate;
    private Date endDate;
    private Date projectStartDate;
    private Date goLiveDate;
    private String weeklyStartDate;
    private String weeklyEndDate;
    private String taskOwner;
    private String team;
    private List<String> beDevelopers;
    private List<String> feDevelopers;
    private List<String> pythonDevelopers;
    private List<String> testers;
    private List<String> uxDesigners;
    private List<String> sharedResources;
}
