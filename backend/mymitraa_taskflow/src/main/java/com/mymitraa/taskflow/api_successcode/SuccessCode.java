package com.mymitraa.taskflow.api_successcode;

/**
 * Enumeration of API success codes, messages, and service names.
 */
public enum SuccessCode {

    SAVE_FEEDBACK_SUCCESSFULLY("SAVE_FEEDBACK_SUCCESSFULLY", "Feedback saved successfully", "FeedbackService"),
    EDIT_FEEDBACK_SUCCESSFULLY("EDIT_FEEDBACK_SUCCESSFULLY", "Feedback edited successfully", "FeedbackService"),
    FETCHED_FEEDBACK_SUCCESSFULLY("FETCHED_FEEDBACK_SUCCESSFULLY", "Feedback fetched successfully", "FeedbackService"),
    DELETE_FEEDBACK_SUCCESSFULLY("DELETE_FEEDBACK_SUCCESSFULLY", "Feedback deleted successfully", "FeedbackService"),
    FEEDBACK_NUMERICAL_COUNT_FETCHED_SUCCESSFULLY("FEEDBACK_NUMERICAL_COUNT_FETCHED_SUCCESSFULLY", "Numerical count fetched successfully", "FeedbackService"),
    GET_ALL_TICKET_NUMBERS_SUCCESSFULLY("GET_ALL_TICKET_NUMBERS_SUCCESSFULLY", "Ticket numbers fetched successfully", "FeedbackService"),
    FEEDBACK_ADVANCE_SEARCH_SUCCESSFULLY("FEEDBACK_ADVANCE_SEARCH_SUCCESSFULLY", "Advance search completed successfully", "FeedbackService"),
    SUBTASK_ADVANCE_SEARCH_SUCCESSFULLY("SUBTASK_ADVANCE_SEARCH_SUCCESSFULLY", "Subtask search completed successfully", "FeedbackService"),
    SEARCHABLE_FIELDS_FETCHED_SUCCESSFULLY("SEARCHABLE_FIELDS_FETCHED_SUCCESSFULLY", "Searchable fields fetched successfully", "FeedbackService"),
    GET_STATUS_RECORD_SUCCESSFULLY("GET_STATUS_RECORD_SUCCESSFULLY", "Status record fetched successfully", "FeedbackService"),
    ADD_SUBTASK_SUCCESSFULLY("ADD_SUBTASK_SUCCESSFULLY", "Subtask added successfully", "FeedbackService"),
    UPDATE_SUBTASK_SUCCESSFULLY("UPDATE_SUBTASK_SUCCESSFULLY", "Subtask updated successfully", "FeedbackService"),
    DELETE_SUBTASK_SUCCESSFULLY("DELETE_SUBTASK_SUCCESSFULLY", "Subtask deleted successfully", "FeedbackService"),
    ADD_WORKLOG_HOURS_SUCCESSFULLY("ADD_WORKLOG_HOURS_SUCCESSFULLY", "Worklog hours added successfully", "FeedbackService"),
    DAILY_TASK_TRACKER_SUCCESSFULLY("DAILY_TASK_TRACKER_SUCCESSFULLY", "Daily tracker data fetched successfully", "FeedbackService"),
    FEEDBACK_DASHBOARD_DATA_SUCCESSFULLY("FEEDBACK_DASHBOARD_DATA_SUCCESSFULLY", "Dashboard data fetched successfully", "FeedbackService"),

    GLOBAL_FILE_UPLOADED_SUCCESSFULLY("GLOBAL_FILE_UPLOADED_SUCCESSFULLY", "File uploaded successfully", "FileUploadService"),
    GET_UPLOADED_FILES_SUCCESSFULLY("GET_UPLOADED_FILES_SUCCESSFULLY", "Uploaded files fetched successfully", "FileUploadService"),
    FILE_DELETED_SUCCESSFULLY("FILE_DELETED_SUCCESSFULLY", "File deleted successfully", "FileUploadService"),

    TASK_SAVED_SUCCESS("TASK_SAVED_SUCCESS", "Task saved successfully", "TaskService"),
    TASK_FETCHED_SUCCESSFULLY("TASK_FETCHED_SUCCESSFULLY", "Task fetched successfully", "TaskService"),
    TASKS_FETCHED_SUCCESSFULLY("TASKS_FETCHED_SUCCESSFULLY", "Tasks fetched successfully", "TaskService"),
    TASK_UPDATED_SUCCESSFULLY("TASK_UPDATED_SUCCESSFULLY", "Task updated successfully", "TaskService"),
    TASK_DELETED_SUCCESSFULLY("TASK_DELETED_SUCCESSFULLY", "Task deleted successfully", "TaskService"),
    TASK_OWNER_EMAIL_DATA_SAVED_SUCCESS("TASK_OWNER_EMAIL_DATA_SAVED_SUCCESS", "Task owner email saved successfully", "TaskOwnerEmailService"),
    TASK_OWNER_EMAIL_SENT("TASK_OWNER_EMAIL_SENT", "Task owner email processed", "TaskOwnerEmailService"),

    MEETING_TIME_TRACKER_DATA_SAVED_SUCCESS("MEETING_TIME_TRACKER_DATA_SAVED_SUCCESS", "Meeting time tracker data saved successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_FETCHED_SUCCESSFULLY("MEETING_TIME_TRACKER_FETCHED_SUCCESSFULLY", "Meeting time tracker data fetched successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_UPDATED_SUCCESSFULLY("MEETING_TIME_TRACKER_UPDATED_SUCCESSFULLY", "Meeting time tracker updated successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_DELETED_SUCCESSFULLY("MEETING_TIME_TRACKER_DELETED_SUCCESSFULLY", "Meeting time tracker deleted successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_DOWNLOADED_SUCCESSFULLY("MEETING_TIME_TRACKER_DOWNLOADED_SUCCESSFULLY", "Meeting time tracker downloaded successfully", "MeetingTimeTrackerService"),

    OPTIMA_LEAVE_TRACKER_DATA_SAVED_SUCCESS("OPTIMA_LEAVE_TRACKER_DATA_SAVED_SUCCESS", "Leave tracker data saved successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_FETCHED_SUCCESSFULLY("OPTIMA_LEAVE_TRACKER_FETCHED_SUCCESSFULLY", "Leave tracker data fetched successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_UPDATED_SUCCESSFULLY("OPTIMA_LEAVE_TRACKER_UPDATED_SUCCESSFULLY", "Leave tracker updated successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_DELETED_SUCCESSFULLY("OPTIMA_LEAVE_TRACKER_DELETED_SUCCESSFULLY", "Leave tracker deleted successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_DOWNLOADED_SUCCESSFULLY("OPTIMA_LEAVE_TRACKER_DOWNLOADED_SUCCESSFULLY", "Leave tracker downloaded successfully", "OptimaLeaveTrackerService"),

    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVED_SUCCESS("OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVED_SUCCESS", "Weekly update tracker data saved successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_FETCHED_SUCCESSFULLY("OPTIMA_WEEKLY_UPDATE_TRACKER_FETCHED_SUCCESSFULLY", "Weekly update tracker data fetched successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_UPDATED_SUCCESSFULLY("OPTIMA_WEEKLY_UPDATE_TRACKER_UPDATED_SUCCESSFULLY", "Weekly update tracker updated successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DELETED_SUCCESSFULLY("OPTIMA_WEEKLY_UPDATE_TRACKER_DELETED_SUCCESSFULLY", "Weekly update tracker deleted successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DOWNLOADED_SUCCESSFULLY("OPTIMA_WEEKLY_UPDATE_TRACKER_DOWNLOADED_SUCCESSFULLY", "Weekly update tracker downloaded successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_REPORT_MAIL_SENT_SUCCESSFULLY("OPTIMA_WEEKLY_REPORT_MAIL_SENT_SUCCESSFULLY", "Weekly report mail sent successfully", "OptimaWeeklyUpdateTrackerService"),

    DROPDOWN_MANAGEMENT_DATA_SAVED_SUCCESS("DROPDOWN_MANAGEMENT_DATA_SAVED_SUCCESS", "Dropdown data saved successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_FETCHED_SUCCESSFULLY("DROPDOWN_MANAGEMENT_FETCHED_SUCCESSFULLY", "Dropdown data fetched successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_UPDATED_SUCCESSFULLY("DROPDOWN_MANAGEMENT_UPDATED_SUCCESSFULLY", "Dropdown data updated successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_DELETED_SUCCESSFULLY("DROPDOWN_MANAGEMENT_DELETED_SUCCESSFULLY", "Dropdown data deleted successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_DOWNLOADED_SUCCESSFULLY("DROPDOWN_MANAGEMENT_DOWNLOADED_SUCCESSFULLY", "Dropdown data downloaded successfully", "DropdownManagementService");

    private final String successCode;
    private final String successMessage;
    private final String serviceName;

    SuccessCode(String successCode, String successMessage, String serviceName) {
        this.successCode = successCode;
        this.successMessage = successMessage;
        this.serviceName = serviceName;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public String getServiceName() {
        return serviceName;
    }
}
