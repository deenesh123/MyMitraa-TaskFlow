package com.mymitraa.taskflow.api_successcode;

public enum SuccessCode {

    RECORD_FETCHED_SUCCESSFULLY(200, "Record fetched successfully", "RecordService"),
    RECORD_SAVED_SUCCESSFULLY(201, "Record saved successfully", "RecordService"),
    RECORD_UPDATED_SUCCESSFULLY(200, "Record updated successfully", "RecordService"),
    RECORD_DELETED_SUCCESSFULLY(200, "Record deleted successfully", "RecordService"),

    FEEDBACK_HISTORY_FETCHED_SUCCESSFULLY(200, "Feedback History fetched successfully", "FeedbackHistoryService"),
    FEEDBACK_HISTORY_SAVED_SUCCESSFULLY(201, "Feedback History saved successfully", "FeedbackHistoryService"),
    FEEDBACK_HISTORY_UPDATED_SUCCESSFULLY(200, "Feedback History updated successfully", "FeedbackHistoryService"),
    FEEDBACK_HISTORY_DELETED_SUCCESSFULLY(200, "Feedback History deleted successfully", "FeedbackHistoryService"),
    FEEDBACK_HISTORY_DOWNLOADED_SUCCESSFULLY(200, "Feedback History downloaded successfully", "FeedbackHistoryService"),

    SAVE_FEEDBACK_SUCCESSFULLY(200, "Feedback saved successfully", "FeedbackHistoryService"),
    EDIT_FEEDBACK_SUCCESSFULLY(200, "Feedback edited successfully", "FeedbackHistoryService"),
    GET_FEEDBACK_SUCCESSFULLY(200, "Feedback fetched successfully", "FeedbackHistoryService"),
    DELETE_FEEDBACK_SUCCESSFULLY(200, "Feedback deleted successfully", "FeedbackHistoryService"),
    DAILY_TASK_TRACKER_FETCHED_SUCCESSFULLY(200, "Daily task tracker fetched successfully", "FeedbackHistoryService"),
    TASK_ASSIGNED_SUCCESSFULLY(200, "Task assigned successfully", "FeedbackHistoryService"),
    TASK_CLOSED_SUCCESSFULLY(200, "Task closed successfully", "FeedbackHistoryService"),

    DROPDOWN_MANAGEMENT_FETCHED_SUCCESSFULLY(200, "Dropdown Management fetched successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_SAVED_SUCCESSFULLY(201, "Dropdown Management saved successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_DATA_SAVED_SUCCESS(200, "Dropdown Management saved successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_UPDATED_SUCCESSFULLY(200, "Dropdown Management updated successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_DELETED_SUCCESSFULLY(200, "Dropdown Management deleted successfully", "DropdownManagementService"),
    DROPDOWN_MANAGEMENT_DOWNLOADED_SUCCESSFULLY(200, "Dropdown Management downloaded successfully", "DropdownManagementService"),

    GLOBAL_FILE_UPLOADED_SUCCESSFULLY(200, "Global file uploaded successfully", "GlobalFileUploadsService"),
    GLOBAL_FILE_FETCH_SUCCESSFULLY(200, "Global file fetched successfully", "GlobalFileUploadsService"),
    GLOBAL_FILE_NAMES_FETCH_SUCCESSFULLY(200, "Global file names fetched successfully", "GlobalFileUploadsService"),
    GLOBAL_FILE_REJCETED_SUCCESSFULLY(200, "Global file rejected successfully", "GlobalFileUploadsService"),
    FTP_FILES_DOWNLOADED_SUCCESSFULLY(200, "FTP files downloaded successfully", "GlobalFileUploadsService"),
    DATA_CLEANED_SUCCESSFULLY(200, "Data cleaned successfully", "GlobalFileUploadsService"),
    IMPORT_10_SAVED_SUCCESS(200, "Import Done Successfully", "GlobalFileUploadsService"),

    OPTIMA_LEAVE_TRACKER_FETCHED_SUCCESSFULLY(200, "Optima Leave Tracker fetched successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_SAVED_SUCCESSFULLY(201, "Optima Leave Tracker saved successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_DATA_SAVED_SUCCESS(200, "Optima Leave Tracker saved successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_UPDATED_SUCCESSFULLY(200, "Optima Leave Tracker updated successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_DELETED_SUCCESSFULLY(200, "Optima Leave Tracker deleted successfully", "OptimaLeaveTrackerService"),
    OPTIMA_LEAVE_TRACKER_DOWNLOADED_SUCCESSFULLY(200, "Optima Leave Tracker downloaded successfully", "OptimaLeaveTrackerService"),

    OPTIMA_WEEKLY_UPDATE_TRACKER_FETCHED_SUCCESSFULLY(200, "Optima Weekly Update Tracker fetched successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_SAVED_SUCCESSFULLY(201, "Optima Weekly Update Tracker saved successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVED_SUCCESS(200, "Optima Weekly Update Tracker saved successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_UPDATED_SUCCESSFULLY(200, "Optima Weekly Update Tracker updated successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DELETED_SUCCESSFULLY(200, "Optima Weekly Update Tracker deleted successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DOWNLOADED_SUCCESSFULLY(200, "Optima Weekly Update Tracker downloaded successfully", "OptimaWeeklyUpdateTrackerService"),
    OPTIMA_WEEKLY_REPORT_MAIL_SENT_SUCCESSFULLY(200, "Weekly report email sent successfully", "OptimaWeeklyUpdateTrackerService"),

    TASK_OWNER_EMAIL_FETCHED_SUCCESSFULLY(200, "Task Owner Email fetched successfully", "TaskOwnerEmailService"),
    TASK_OWNER_EMAIL_SAVED_SUCCESSFULLY(201, "Task Owner Email saved successfully", "TaskOwnerEmailService"),
    TASK_OWNER_EMAIL_DATA_SAVED_SUCCESS(200, "Task Owner Email saved successfully", "TaskOwnerEmailService"),

    MEETING_TIME_TRACKER_FETCHED_SUCCESSFULLY(200, "Meeting Time Tracker fetched successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_SAVED_SUCCESSFULLY(201, "Meeting Time Tracker saved successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_DATA_SAVED_SUCCESS(200, "Meeting Time Tracker saved successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_UPDATED_SUCCESSFULLY(200, "Meeting Time Tracker updated successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_DELETED_SUCCESSFULLY(200, "Meeting Time Tracker deleted successfully", "MeetingTimeTrackerService"),
    MEETING_TIME_TRACKER_DOWNLOADED_SUCCESSFULLY(200, "Meeting Time Tracker downloaded successfully", "MeetingTimeTrackerService"),

    SUBTASK_FETCHED_SUCCESSFULLY(200, "Subtask fetched successfully", "SubtaskService"),
    SUBTASK_SAVED_SUCCESSFULLY(201, "Subtask saved successfully", "SubtaskService"),
    SUBTASK_UPDATED_SUCCESSFULLY(200, "Subtask updated successfully", "SubtaskService"),
    SUBTASK_DELETED_SUCCESSFULLY(200, "Subtask deleted successfully", "SubtaskService");

    private final int successCode;
    private final String successMessage;
    private final String serviceName;

    SuccessCode(int successCode, String successMessage, String serviceName) {
        this.successCode = successCode;
        this.successMessage = successMessage;
        this.serviceName = serviceName;
    }

    public int getSuccessCode() {
        return successCode;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public String getServiceName() {
        return serviceName;
    }
}
