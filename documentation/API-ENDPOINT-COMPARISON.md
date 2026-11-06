# MyMitraa TaskFlow — API Endpoint Comparison

This document provides a comprehensive endpoint-by-endpoint mapping and comparison between the Express development preview server and the authoritative Java Spring Boot REST API for MyMitraa TaskFlow.

---

## Architecture & Verification Categories

- **Authoritative Backend**: Java 21 LTS + Spring Boot 3.3.4 (`com.mymitraa.taskflow`) + MongoDB (`mymitraa_taskflow`)
- **Development Preview Server**: Node.js + Express (`server.ts`) supporting dual-route legacy and modern TaskFlow REST contracts
- **Verification Classifications**:
  1. **[VERIFIED - LIVE EXECUTED]**: Executed via HTTP against active preview dev server
  2. **[CONFIRMED - SOURCE AUDITED]**: Static AST & source inspection completed; all imports, method signatures, DTOs, and mappings verified
  3. **[EXTERNAL BLOCKED]**: Blocked from local sandbox runtime execution due to lack of JDK 21 / Maven in container

---

## 1. Authentication & System Health

| HTTP Method | API Path | Express (`server.ts`) Route | Java Spring Boot Controller & Method | Request DTO / Payload | Response Structure | Security / Auth | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/auth/login` | `/api/auth/login` | `WebSecurityConfiguration` / JWT Auth filter | `{ username, password }` | `{ token, user: { id, username, email, role } }` | Public | [CONFIRMED - SOURCE AUDITED] / [VERIFIED - LIVE EXECUTED] |
| `GET` | `/api/auth/me` | `/api/auth/me` | `SecurityContextHolder` | None (Bearer header) | `{ user: { id, username, email, role } }` | Authenticated (JWT) | [CONFIRMED - SOURCE AUDITED] / [VERIFIED - LIVE EXECUTED] |
| `GET` | `/api/health` | `/api/health` | Actuator (`/actuator/health`) | None | `{ status: "UP", service, database, ... }` | Public | [CONFIRMED - SOURCE AUDITED] / [VERIFIED - LIVE EXECUTED] |

---

## 2. Module 1: Feedback History (`/api/feedback_history`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/feedback_history/save` | `POST /api/feedback_history` | `FeedbackHistoryController.save()` | Multipart / VM JSON (`FeedbackHistoryVM`) | `CustomResponse("FEEDBACK_HISTORY_SAVED_SUCCESS")` | `@AuthenticationPrincipal CustomUserDetails` | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/feedback_history/get_by_id/{id}` | `GET /api/feedback_history/:id` | `FeedbackHistoryController.getById(@PathVariable String id)` | Path: `id` | `CustomResponse("FEEDBACK_HISTORY_FETCHED_SUCCESSFULLY")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/feedback_history/get_all_with_pagination` | `POST /api/feedback_history/paginate` | `FeedbackHistoryController.getAllWithPagination()` | Body: `SearchVM`, Query: `Pageable` | `CustomResponse` containing `Page<FeedbackHistory>` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/feedback_history/update` | `PUT /api/feedback_history/:id` | `FeedbackHistoryController.update()` | Body: `FeedbackHistoryVM` | `CustomResponse("FEEDBACK_HISTORY_UPDATED_SUCCESSFULLY")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/feedback_history/dashboard` | `GET /api/feedback_history/dashboard` | `FeedbackHistoryController.dashboard()` | Principal / query params | `CustomResponse("DASHBOARD_FETCHED_SUCCESSFULLY")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `DELETE` | `/api/feedback_history/delete_subtask/{id}/{subTaskId}` | `DELETE /api/feedback_history/:id/subtask/:subTaskId` | `FeedbackHistoryController.deleteSubTask()` | Path: `id`, `subTaskId` | `CustomResponse("SUBTASK_DELETED_SUCCESSFULLY")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `POST` | `/api/feedback_history/add_worklog_hours` | `POST /api/feedback_history/worklog` | `FeedbackHistoryController.addWorklogHours()` | Body: `EffortRecordVM` | `CustomResponse("WORKLOG_ADDED_SUCCESSFULLY")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 3. Module 2: Global File Uploads (`/api/global_uploads`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/global_uploads/upload_file` | `POST /api/global_uploads/upload` | `GlobalFileUploadsController.addWorkOverSchedule()` | Multipart: `multipartFile`, Params: `category`, `dateTime` | `CustomResponse("FILE_UPLOADED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/global_uploads/get_uploaded_files` | `PUT /api/global_uploads/list` | `GlobalFileUploadsController.getUploadedFiles()` | Body: `SearchVM`, Query: `Pageable` | `CustomResponse` containing `Page<GlobalFileUploads>` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `POST` | `/api/global_uploads/save_downloaded_count` | `POST /api/global_uploads/download_count` | `GlobalFileUploadsController.countDownloadedFiles()` | Body: `FilesDownloadedCountDomain` | `CustomResponse("DOWNLOAD_RECORDED")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/global_uploads/get_global_numerical_analysis` | `GET /api/global_uploads/analysis` | `GlobalFileUploadsController.numericalAnalysisForAlEngineering()` | `@AuthenticationPrincipal CustomUserDetails` | `CustomResponse("ANALYSIS_FETCHED")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `DELETE` | `/api/global_uploads/delete_by_id/{id}` | `DELETE /api/global_uploads/:id` | `GlobalFileUploadsController.deleteByFileUploadId()` | Path: `id` | `CustomResponse("FILE_DELETED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `POST` | `/api/global_uploads/update_table_data` | `POST /api/global_uploads/update_table_data` | `GlobalFileUploadsController.updateTableData()` | Body: `GlobalFileUploadVM` | `CustomResponse("TABLE_UPDATED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/global_uploads/dropdown_lists` | `GET /api/global_uploads/dropdown_lists` | `GlobalFileUploadsController.getGlobalImportDropdownLists()` | Principal | `CustomResponse("DROPDOWNS_FETCHED")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 4. Module 3: Optima Leave Tracker (`/api/optima_leave_tracker`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/optima_leave_tracker/save` | `POST /api/optima_leave_tracker` | `OptimaLeaveTrackerController.save()` | Body: `OptimaLeaveTrackerVM` | `CustomResponse("LEAVE_SAVED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/optima_leave_tracker/get_by_id/{id}` | `GET /api/optima_leave_tracker/:id` | `OptimaLeaveTrackerController.getById()` | Path: `id` | `CustomResponse("LEAVE_FETCHED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/optima_leave_tracker/get_all_with_pagination` | `PUT /api/optima_leave_tracker/paginate` | `OptimaLeaveTrackerController.getAllWithPagination()` | Body: `SearchVM`, Query: `Pageable` | `CustomResponse` containing `Page<OptimaLeaveTracker>` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/optima_leave_tracker/update` | `PUT /api/optima_leave_tracker/:id` | `OptimaLeaveTrackerController.update()` | Body: `OptimaLeaveTrackerVM` | `CustomResponse("LEAVE_UPDATED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/optima_leave_tracker/dashboard` | `GET /api/optima_leave_tracker/dashboard` | `OptimaLeaveTrackerController.dashboard()` | Principal | `CustomResponse("LEAVE_DASHBOARD_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 5. Module 4: Optima Weekly Update Tracker (`/api/optima_weekly_update_tracker`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/optima_weekly_update_tracker/save` | `POST /api/optima_weekly_update_tracker` | `OptimaWeeklyUpdateTrackerController.save()` | Body: `OptimaWeeklyUpdateTrackerVM` | `CustomResponse("UPDATE_SAVED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/optima_weekly_update_tracker/get_by_id/{id}` | `GET /api/optima_weekly_update_tracker/:id` | `OptimaWeeklyUpdateTrackerController.getById()` | Path: `id` | `CustomResponse("UPDATE_FETCHED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/optima_weekly_update_tracker/get_all_with_pagination` | `PUT /api/optima_weekly_update_tracker/paginate` | `OptimaWeeklyUpdateTrackerController.getAllWithPagination()` | Body: `SearchVM`, Query: `Pageable` | `CustomResponse` containing `Page<OptimaWeeklyUpdateTracker>` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/optima_weekly_update_tracker/update` | `PUT /api/optima_weekly_update_tracker/:id` | `OptimaWeeklyUpdateTrackerController.update()` | Body: `OptimaWeeklyUpdateTrackerVM` | `CustomResponse("UPDATE_UPDATED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `POST` | `/api/optima_weekly_update_tracker/send_mail` | `POST /api/optima_weekly_update_tracker/send_mail` | `OptimaWeeklyUpdateTrackerController.sendMail()` | Body: `MailRequestVM` | `CustomResponse("MAIL_SENT_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 6. Module 5: Task Owner Email (`/api/task_owner_email`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/task_owner_email/send` | `POST /api/email/task-owner` | `TaskOwnerEmailController.sendTaskOwnerEmail()` | Body: `TaskOwnerEmailVM` | `CustomResponse("EMAIL_SENT_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/task_owner_email/logs` | `GET /api/email/logs` | `TaskOwnerEmailController.getEmailLogs()` | Query: `limit`, `category` | `CustomResponse` containing `List<TaskOwnerEmail>` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/task_owner_email/config` | `GET /api/email/config` | `TaskOwnerEmailController.getEmailConfig()` | None | `CustomResponse` (sanitized SMTP config) | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 7. Module 6: Meeting Time Tracker (`/api/meeting_time_tracker`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/meeting_time_tracker/save` | `POST /api/meeting_time_tracker` | `MeetingTimeTrackerController.save()` | Body: `MeetingTimeTracker` entity | `CustomResponse("MEETING_SAVED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/meeting_time_tracker/get_by_id/{id}` | `GET /api/meeting_time_tracker/:id` | `MeetingTimeTrackerController.getById()` | Path: `id` | `CustomResponse("MEETING_FETCHED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/meeting_time_tracker/get_all_with_pagination` | `PUT /api/meeting_time_tracker/paginate` | `MeetingTimeTrackerController.getAllWithPagination()` | Body: `SearchVM`, Query: `Pageable` | `CustomResponse` containing `Page<MeetingTimeTracker>` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/meeting_time_tracker/update` | `PUT /api/meeting_time_tracker/:id` | `MeetingTimeTrackerController.update()` | Body: `MeetingTimeTracker` entity | `CustomResponse("MEETING_UPDATED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 8. Module 7: Dropdown Management (`/api/dropdowns`)

| HTTP Method | API Path | Express Preview Route | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirement | Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/dropdowns/save` | `POST /api/dropdowns` | `DropdownManagementController.save()` | Body: `DropdownManagementVM` | `CustomResponse("DROPDOWN_SAVED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/dropdowns/get_by_id/{id}` | `GET /api/dropdowns/:id` | `DropdownManagementController.getById()` | Path: `id` | `CustomResponse("DROPDOWN_FETCHED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/dropdowns/get_all` | `GET /api/dropdowns` | `DropdownManagementController.getAllDropdowns()` | None | `CustomResponse` containing `List<DropdownManagement>` | Public / Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `PUT` | `/api/dropdowns/update` | `PUT /api/dropdowns/:id` | `DropdownManagementController.update()` | Body: `DropdownManagementVM` | `CustomResponse("DROPDOWN_UPDATED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `DELETE` | `/api/dropdowns/delete/{id}` | `DELETE /api/dropdowns/:id` | `DropdownManagementController.delete()` | Path: `id` | `CustomResponse("DROPDOWN_DELETED_SUCCESS")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `GET` | `/api/dropdowns/get_parameter_list` | `GET /api/dropdowns/parameters` | `DropdownManagementController.getParameterList()` | `@AuthenticationPrincipal CustomUserDetails` | `CustomResponse` containing `AdvanceSearchList` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `POST` | `/api/dropdowns/dropdown` | `POST /api/dropdowns/dropdown` | `DropdownManagementController.saveDropdown()` | Body: `DropdownManagementVM` | `CustomResponse("DROPDOWN_ITEM_SAVED")` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 9. Additional Controllers & REST Services

| Controller | API Path | Java Methods | MongoDB Collection | Auth Requirement | Status |
|---|---|---|---|---|---|
| `TaskController` | `/api/tasks` | `save`, `get/{id}`, `getAll`, `update/{id}`, `delete/{id}` | `tasks` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `LeaveRequestController` | `/api/leave_requests` | `save`, `get/{id}`, `getAll`, `update/{id}`, `delete/{id}` | `leave_requests` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `DocumentController` | `/api/documents` | `upload`, `get/{id}`, `getAll`, `update/{id}`, `delete/{id}`, `get-by-filename` | `documents` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `ProjectStatusController` | `/api/project_status` | `save`, `get/{id}`, `getAll`, `update/{id}`, `delete/{id}` | `project_statuses` | Authenticated | [CONFIRMED - SOURCE AUDITED] |
| `TimelineEntryController` | `/api/timeline` | `save`, `get/{id}`, `getAll`, `update/{id}`, `delete/{id}`, `by-entity`, `by-date-range` | `timeline_entries` | Authenticated | [CONFIRMED - SOURCE AUDITED] |

---

## 10. Summary Matrix

| Metric | Express Preview Server | Java Spring Boot REST API |
|---|---|---|
| **Total Mapped Endpoints** | 44 | 44 |
| **Authentication Enforcement** | JWT Bearer Token | Spring Security 6 (`JwtAuthenticationFilter`) |
| **Persistence Engine** | Express in-memory + mock fallback in dev | MongoDB 7.0+ (`mymitraa_taskflow`) |
| **Email Gateway** | `nodemailer` (in-memory simulator/SMTP) | `JavaMailSender` (`ZohoMailConfig`) |
| **Error Contract** | `{ success: boolean, message: string, data: any }` | `CustomResponse` with code, description, data, metaInfo |
