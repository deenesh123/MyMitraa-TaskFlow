package com.mymitraa.taskflow.api_errors;

public enum RegisteredException {
    FEEDBACK_HISTORY_EXCEPTION("FeedbackHistoryException"),
    FEED_BACK_ID_REQUIRED("FeedbackIdRequiredException"),
    TASK_NOT_FOUND_EXCEPTION("TaskNotFoundException"),
    TASK_ASSIGN_EXCEPTION("TaskAssignException"),
    TASK_CLOSE_EXCEPTION("TaskCloseException"),
    INVALID_DATE("InvalidDateException"),
    DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION("DailyTaskTrackerFetchingException"),
    ADVACNE_SEARCH_EXCEPTION("AdvanceSearchException"),
    SUBTASK_SAVING_EXCEPTION("SubtaskSavingException"),
    SUBTASK_FEEDBACKHISTORY_ID_NOT_FOUND_EXCEPTION("SubtaskParentNotFoundException"),
    GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION("GlobalFileUploadsFetchException"),
    FILE_UPLOAD_SERVICE_EXCEPTION("FileUploadServiceException"),
    MEETING_TIME_TRACKER_DETAILS_FETCHING_EXCEPTION("MeetingTimeTrackerFetchingException"),
    OPTIMA_LEAVE_TRACKER_DATA_SAVING_EXCEPTION("OptimaLeaveTrackerSavingException"),
    OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION("OptimaLeaveTrackerNotFoundException"),
    OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION("OptimaLeaveTrackerFetchingException"),
    OPTIMA_LEAVE_TRACKER_DATA_DELETING_EXCEPTION("OptimaLeaveTrackerDeletingException"),
    OPTIMA_LEAVE_TRACKER_DATA_UPDATING_EXCEPTION("OptimaLeaveTrackerUpdatingException"),
    FORMATION_EXCEPTION("FormationException"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVING_EXCEPTION("OptimaWeeklyUpdateTrackerSavingException"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION("OptimaWeeklyUpdateTrackerNotFoundException"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION("OptimaWeeklyUpdateTrackerFetchingException"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_DELETING_EXCEPTION("OptimaWeeklyUpdateTrackerDeletingException"),
    OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_UPDATING_EXCEPTION("OptimaWeeklyUpdateTrackerUpdatingException"),
    TASK_OWNER_EMAIL_DATA_SAVING_EXCEPTION("TaskOwnerEmailSavingException"),
    FTP_FILES_COUNT_EXCEPTION("FtpFilesCountException"),
    FTP_FILES_DATA_EXCEPTION("FtpFilesDataException");

    private final String exception;

    RegisteredException(String exception) {
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }
}