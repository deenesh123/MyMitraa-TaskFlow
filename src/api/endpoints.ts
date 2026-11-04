/**
 * Exact REST Endpoint definitions matching MiTRAA TaskFlow API Reference v3 (102 endpoints)
 */

export const API_ENDPOINTS = {
  // Task Tracker (Feedback) - /api/feedback
  FEEDBACK: {
    SAVE: '/api/feedback/save_feedback',
    EDIT: '/api/feedback/edit_feedback',
    ASSIGN: '/api/feedback/assign_task',
    CLOSE: '/api/feedback/close_task',
    DELETE: (id: string) => `/api/feedback/get_feedback_by_id/${id}`,
    GET_ONE: (id: string) => `/api/feedback/get_feedback_by_id/${id}`,
    GET_ALL: '/api/feedback/get_all_feedback',
    NUMERICAL_COUNT: '/api/feedback/feedback_numerical_count',
    TICKET_NUMBERS: '/api/feedback/get_all_ticket_numbers',
    PARAMETERS: '/api/feedback/get_parameter_list',
    ADVANCE_SEARCH: '/api/feedback/advance_search',
    SUBTASK_ADVANCE_SEARCH: '/api/feedback/subtask_advance_search',
    SEARCHABLE_FIELDS: '/api/feedback/searchable-fields',
    STATUS_RECORD: '/api/feedback/get_status_record',
    ADD_SUBTASK: '/api/feedback/add_subtask',
    UPDATE_SUBTASK: (historyId: string, epicId: string) => `/api/feedback/update_subtask/${historyId}/${epicId}`,
    DELETE_SUBTASK: (historyId: string, epicId: string) => `/api/feedback/delete_subtask/${historyId}/${epicId}`,
    UPLOAD: '/api/feedback/upload',
    UPLOAD_TICKET: (ticketNumber: string) => `/api/feedback/upload/${ticketNumber}`,
    DELETE_FILE: (ticketNumber: string) => `/api/feedback/delete_file/${ticketNumber}`,
    GET_UPLOADED_FILES: '/api/feedback/get_uploaded_files',
    DAILY_TASK_TRACKER: '/api/feedback/daily_task_tracker',
    DASHBOARD: '/api/feedback/dashboard',
    ADD_WORKLOG_HOURS: '/api/feedback/add_worklog_hours',
  },

  // Meeting Time Tracker - /api/meeting_time_tracker
  MEETING: {
    SAVE: '/api/meeting_time_tracker/save',
    GET_ONE: (id: string) => `/api/meeting_time_tracker/get/${id}`,
    GET_ALL: '/api/meeting_time_tracker/getAll',
    GET_PAGE: '/api/meeting_time_tracker/get_with_pagination',
    UPDATE: (id: string) => `/api/meeting_time_tracker/update/${id}`,
    DOWNLOAD: '/api/meeting_time_tracker/download',
    PARAMETERS: '/api/meeting_time_tracker/get_parameter_list',
    ADVANCE_SEARCH: '/api/meeting_time_tracker/advance_search',
    DELETE: (id: string) => `/api/meeting_time_tracker/delete/${id}`,
    DASHBOARD: '/api/meeting_time_tracker/dashboard',
  },

  // Leave / WFH Tracker - /api/optima_leave_tracker
  LEAVE: {
    SAVE: '/api/optima_leave_tracker/save',
    GET_ONE: (id: string) => `/api/optima_leave_tracker/get/${id}`,
    GET_ALL: '/api/optima_leave_tracker/getAll',
    GET_PAGE: '/api/optima_leave_tracker/get_with_pagination',
    UPDATE: (id: string) => `/api/optima_leave_tracker/update/${id}`,
    DOWNLOAD: '/api/optima_leave_tracker/download',
    PARAMETERS: '/api/optima_leave_tracker/get_parameter_list',
    ADVANCE_SEARCH: '/api/optima_leave_tracker/advance_search',
    DELETE: (id: string) => `/api/optima_leave_tracker/delete/${id}`,
    DASHBOARD: '/api/optima_leave_tracker/dashboard',
  },

  // Weekly Update Tracker - /api/optima_weekly_update_tracker
  WEEKLY_UPDATE: {
    SAVE: '/api/optima_weekly_update_tracker/save',
    GET_ONE: (id: string) => `/api/optima_weekly_update_tracker/get/${id}`,
    GET_ALL: '/api/optima_weekly_update_tracker/getAll',
    GET_PAGE: '/api/optima_weekly_update_tracker/get_with_pagination',
    UPDATE: '/api/optima_weekly_update_tracker/update',
    DOWNLOAD: '/api/optima_weekly_update_tracker/download',
    PARAMETERS: '/api/optima_weekly_update_tracker/get_parameter_list',
    ADVANCE_SEARCH: '/api/optima_weekly_update_tracker/advance_search',
    DELETE: (id: string) => `/api/optima_weekly_update_tracker/delete/${id}`,
    DASHBOARD: '/api/optima_weekly_update_tracker/dashboard',
    UPLOAD_FILE: (id: string) => `/api/optima_weekly_update_tracker/uploadfile/${id}`,
    GET_FETCH_ALL: (projectName: string) => `/api/optima_weekly_update_tracker/get_fetch_all/${projectName}`,
    SEND_WEEKLY_MAIL: '/api/optima_weekly_update_tracker/send-weekly-mail',
  },

  // Dropdown Management - /api/dropdown_management
  DROPDOWN: {
    SAVE: '/api/dropdown_management/save',
    GET_ONE: (id: string) => `/api/dropdown_management/get/${id}`,
    GET_ALL: '/api/dropdown_management/getAll',
    GET_PAGE: '/api/dropdown_management/get_with_pagination',
    UPDATE: (id: string) => `/api/dropdown_management/update/${id}`,
    DOWNLOAD: '/api/dropdown_management/download',
    PARAMETERS: '/api/dropdown_management/get_parameter_list',
    ADVANCE_SEARCH: '/api/dropdown_management/advance_search',
    DELETE: (id: string) => `/api/dropdown_management/delete/${id}`,
    DROPDOWN: '/api/dropdown_management/dropdown',
    DROPDOWN_SUBVALUE: '/api/dropdown_management/dropdown-subvalue',
    DROPDOWN_SUBVALUE_MODULE: '/api/dropdown_management/dropdown-subvalue-module',
    DROPDOWN_SUBVALUE_MODULE_SUBMODULE: '/api/dropdown_management/dropdown-subvalue-module-submodule',
  },

  // Task Owner Email - /api/task_owner_email
  TASK_OWNER_EMAIL: {
    SAVE: '/api/task_owner_email/save',
  },
} as const;
