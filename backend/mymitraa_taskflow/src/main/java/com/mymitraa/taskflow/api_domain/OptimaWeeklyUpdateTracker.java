package com.mymitraa.taskflow.api_domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "optima_weekly_update_tracker")
public class OptimaWeeklyUpdateTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("project_id")
    private String projectId;

    @Field("project_name")
    private String projectName;

    @Field("update_title")
    private String updateTitle;

    @Field("update_description")
    private String updateDescription;

    @Field("customer_name")
    private String customerName;

    @Field("customer_geography")
    private String customerGeography;

    @Field("industry")
    private String industry;

    @Field("project_status")
    private String projectStatus;

    @Field("status")
    private String status;

    @Field("priority")
    private String priority;

    @Field("current_status")
    private String currentStatus;

    @Field("last_week_status")
    private String lastWeekStatus;

    @Field("current_state")
    private String currentState;

    @Field("project_summery")
    private String projectSummery;

    @Field("risk")
    private String risk;

    @Field("kt_provider")
    private String ktProvider;

    @Field("code_base")
    private String codeBase;

    @Field("dev_url")
    private String devUrl;

    @Field("qa_url")
    private String qaUrl;

    @Field("la_url")
    private String laUrl;

    @Field("other_url")
    private String otherUrl;

    @Field("dev_arrow")
    private boolean devArrow;

    @Field("qa_arrow")
    private boolean qaArrow;

    @Field("la_arrow")
    private boolean laArrow;

    @Field("other_arrow")
    private boolean otherArrow;

    @Field("documentation")
    private String documentation;

    @Field("hld_link")
    private String hldLink;

    @Field("lld_link")
    private String lldLink;

    @Field("hld_attachment")
    private String hldAttachment;

    @Field("lld_attachment")
    private String lldAttachment;

    @Field("total_env")
    private String totalEnv;

    @Field("ref")
    private String ref;

    @Field("start_date")
    private Date startDate;

    @Field("end_date")
    private Date endDate;

    @Field("project_start_date")
    private Date projectStartDate;

    @Field("go_live_date")
    private Date goLiveDate;

    @Field("weekly_start_date")
    private String weeklyStartDate;

    @Field("weekly_end_date")
    private String weeklyEndDate;

    @Field("update_date")
    private Date updateDate;

    @Field("updated_by")
    private String updatedBy;

    @Field("week_number")
    private Integer weekNumber;

    @Field("year")
    private Integer year;

    @Field("task_owner")
    private String taskOwner;

    @Field("team")
    private String team;

    @Field("be_developers")
    private List<String> beDevelopers;

    @Field("fe_developers")
    private List<String> feDevelopers;

    @Field("python_developers")
    private List<String> pythonDevelopers;

    @Field("testers")
    private List<String> testers;

    @Field("ux_designers")
    private List<String> uxDesigners;

    @Field("shared_resources")
    private List<String> sharedResources;

    @Field("delete_flag")
    private Boolean deleteFlag = false;

    @Field("created_on")
    private Date createdOn;

    @Field("created_by")
    private String createdBy;

    @Field("modified_date_time")
    private Date modifiedDateTime;

    @Field("modified_by")
    private String modifiedBy;
}