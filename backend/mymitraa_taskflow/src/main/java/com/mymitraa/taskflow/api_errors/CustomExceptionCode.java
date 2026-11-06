package com.mymitraa.taskflow.api_errors;

public enum CustomExceptionCode {
    FEEDBACK_ID_REQUIRED("WS_ERR1001", "Feedback ID is required"),
    DATA_NOT_FOUND("WS_ERR1002", "Data not found"),
    TASK_NOT_FOUND_EXCEPTION("WS_ERR1003", "Task not found"),
    TASK_ASSIGN_EXCEPTION("WS_ERR1004", "Exception occurred while assigning task"),
    TASK_CLOSE_EXCEPTION("WS_ERR1005", "Exception occurred while closing task"),
    DATE_RANGE_EXCEPTION("WS_ERR1006", "Date range is invalid"),
    DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION("WS_ERR1007", "Exception while fetching daily task tracker details"),
    ADVANCE_SEARCH_EXCEPTION("WS_ERR1008", "Exception in advance search"),
    SUBTASK_SAVING_EXCEPTION("WS_ERR1009", "Exception occurred while saving subtask"),
    SUBTASK_FEEDBACKHISTORY_ID_NOT_FOUND_EXCEPTION("WS_ERR1010", "Parent feedback ID not found"),
    EXCEPTION_OCCURRED_WHILE_SAVING_SUBTASK("WS_ERR1011", "Exception occurred while saving subtask"),
    EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY("WS_ERR1012", "Exception occurred while saving feedback history"),
    GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION("WS_ERR1013", "Exception while fetching global file uploads"),
    EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS("WS_ERR1014", "Exception while fetching uploaded files"),
    FILE_UPLOAD_SERVICE_EXCEPTION("WS_ERR1015", "File upload failed"),
    EXCEL_GENERATION_EXCEPTION("WS_ERR1016", "Excel parsing or generation failed"),
    FILE_EXTENSION_EXCEPTION("WS_ERR1017", "Invalid file extension"),
    FILE_IS_EMPTY("WS_ERR1018", "Uploaded file is empty"),
    MEETING_TIME_TRACKER_DETAILS_FETCHING_EXCEPTION("WS_ERR2001", "Exception fetching meeting time tracker details"),
    MEETING_TIME_TRACKER_DATA_SAVING_EXCEPTION("WS_ERR2002", "Exception saving meeting time tracker data"),
    MEETING_TIME_TRACKER_DATA_NOT_FOUND_EXCEPTION("WS_ERR2003", "Meeting time tracker data not found"),
    MEETING_TIME_TRACKER_DATA_UPDATING_EXCEPTION("WS_ERR2004", "Exception updating meeting time tracker"),
    MEETING_TIME_TRACKER_DATA_DELETING_EXCEPTION("WS_ERR2005", "Exception deleting meeting time tracker"),
    OPTIMA_LEAVE_TRACKER_DATA_SAVING_EXCEPTION("WS_ERR3001", "Exception saving leave record"),
    OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION("WS_ERR3002", "Leave record not found"),
    OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION("WS_ERR3003", "Exception fetching leave records"),
    OPTIMA_LEAVE_TRACKER_DATA_DELETING_EXCEPTION("WS_ERR3004", "Exception deleting leave record"),
    OPTIMA_LEAVE_TRACKER_DATA_UPDATING_EXCEPTION("WS_ERR3005", "Exception updating leave record"),
    FORMATION_EXCEPTION("WS_ERR3006", "Excel generation exception"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVING_EXCEPTION("WS_ERR4001", "Exception saving weekly update"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION("WS_ERR4002", "Weekly update not found"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION("WS_ERR4003", "Exception fetching weekly updates"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_DELETING_EXCEPTION("WS_ERR4004", "Exception deleting weekly update"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_UPDATING_EXCEPTION("WS_ERR4005", "Exception updating weekly update"),
    TASK_OWNER_EMAIL_DATA_SAVING_EXCEPTION("WS_ERR5001", "Exception saving task owner email"),
    FTP_FILES_COUNT_EXCEPTION("WS_ERR6001", "Exception counting FTP files"),
    EXCEPTION_OCCORED_WHILE_SAVING_DOWNLOADED_COUNT("WS_ERR6002", "Exception saving downloaded count"),
    FTP_FILES_DATA_EXCEPTION("WS_ERR6003", "Exception fetching FTP files data"),
    EXCEPTION_OCCORED_WHILE_FETCHING_FTP_DATA("WS_ERR6004", "Exception fetching FTP data"),
    EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_("WS_ERR6005", "Exception occurred while uploading files"),
    CHRON_PROCESS_EXCEPTION("WS_ERR7001", "Chron process failed");

    private final String errCode;
    private final String errMsg;

    CustomExceptionCode(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
