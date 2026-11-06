# MyMitraa TaskFlow — API Endpoint Comparison

This document provides a detailed endpoint comparison between the Node/Express development preview server and the authoritative Java Spring Boot REST API.

---

## 1. Authentication & System Endpoints

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/auth/login` | `server.ts` (inline router) | `WebSecurityConfiguration` / JWT Auth filter | `{ username, password }` | `{ token, user: { id, username, email, role } }` | Public | Parity achieved |
| `GET` | `/api/auth/me` | `server.ts` (inline router) | Spring Security Context Holder | Headers: `Authorization: Bearer <token>` | `{ user: { id, username, email, role } }` | Authenticated (JWT) | Parity achieved |
| `GET` | `/api/health` | `server.ts` (inline router) | Spring Boot Actuator `/actuator/health` | None | `{ status: "ok", timestamp, service }` | Public | Parity achieved |

---

## 2. Module 1: Feedback History

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/feedback_history/save` | `server.ts` (`/api/feedback_history`) | `FeedbackHistoryController.save()` | Multipart / VM JSON (`FeedbackHistoryVM`) | `CustomResponse(200, "Saved", payload)` | Authenticated (`CustomUserDetails`) | Parity achieved |
| `GET` | `/api/feedback_history/get_by_id/{id}` | `server.ts` (`/api/feedback_history/:id`) | `FeedbackHistoryController.getById()` | URL Path: `id` | `CustomResponse(200, "Fetched", entity)` | Authenticated | Parity achieved |
| `PUT` | `/api/feedback_history/get_all_with_pagination` | `server.ts` (`/api/feedback_history/paginate`) | `FeedbackHistoryController.getAllWithPagination()` | Body: `SearchVM`, Query: `page`, `size` | `CustomResponse(200, "Fetched", Page)` | Authenticated | Parity achieved |
| `PUT` | `/api/feedback_history/update` | `server.ts` (`PUT /api/feedback_history/:id`) | `FeedbackHistoryController.update()` | Body: `FeedbackHistoryVM` | `CustomResponse(200, "Updated", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/feedback_history/dashboard` | `server.ts` (`/api/feedback_history/dashboard`) | `FeedbackHistoryController.dashboard()` | Body / Query parameters | `CustomResponse(200, "Fetched", stats)` | Authenticated | Parity achieved |

---

## 3. Module 2: Global File Uploads

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/global_uploads/upload_file` | `server.ts` (`/api/global_uploads/upload`) | `GlobalFileUploadsController.addWorkOverSchedule()` | Multipart: `multipartFile`, Params: `category`, `operationDateTime` | `CustomResponse(200, "Uploaded", meta)` | Authenticated | Parity achieved |
| `PUT` | `/api/global_uploads/get_uploaded_files` | `server.ts` (`/api/global_uploads/list`) | `GlobalFileUploadsController.getUploadedFiles()` | Body: `SearchVM`, Query: `page`, `size` | `CustomResponse(200, "Fetched", Page)` | Authenticated | Parity achieved |
| `POST` | `/api/global_uploads/save_downloaded_count` | `server.ts` (`/api/global_uploads/download_count`) | `GlobalFileUploadsController.countDownloadedFiles()` | Body: `FilesDownloadedCountDomain` | `CustomResponse(200, "Counted", result)` | Authenticated | Parity achieved |
| `GET` | `/api/global_uploads/get_global_numerical_analysis` | `server.ts` (`/api/global_uploads/analysis`) | `GlobalFileUploadsController.numericalAnalysisForAlEngineering()` | Query / User Principal | `CustomResponse(200, "Fetched", stats)` | Authenticated | Parity achieved |
| `DELETE` | `/api/global_uploads/delete_by_id/{id}` | `server.ts` (`DELETE /api/global_uploads/:id`) | `GlobalFileUploadsController.deleteByFileUploadId()` | URL Path: `globalFileUploadsId` | `CustomResponse(200, "Deleted", id)` | Authenticated | Parity achieved |

---

## 4. Module 3: Optima Leave Tracker

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/optima_leave_tracker/save` | `server.ts` (`/api/optima_leave_tracker`) | `OptimaLeaveTrackerController.save()` | Body: `OptimaLeaveTrackerVM` | `CustomResponse(200, "Saved", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/optima_leave_tracker/get_by_id/{id}` | `server.ts` (`/api/optima_leave_tracker/:id`) | `OptimaLeaveTrackerController.getById()` | URL Path: `id` | `CustomResponse(200, "Fetched", entity)` | Authenticated | Parity achieved |
| `PUT` | `/api/optima_leave_tracker/get_all_with_pagination` | `server.ts` (`/api/optima_leave_tracker/paginate`) | `OptimaLeaveTrackerController.getAllWithPagination()` | Body: `SearchVM`, Query: `page`, `size` | `CustomResponse(200, "Fetched", Page)` | Authenticated | Parity achieved |
| `PUT` | `/api/optima_leave_tracker/update` | `server.ts` (`PUT /api/optima_leave_tracker/:id`) | `OptimaLeaveTrackerController.update()` | Body: `OptimaLeaveTrackerVM` | `CustomResponse(200, "Updated", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/optima_leave_tracker/dashboard` | `server.ts` (`/api/optima_leave_tracker/dashboard`) | `OptimaLeaveTrackerController.dashboard()` | User Principal | `CustomResponse(200, "Fetched", stats)` | Authenticated | Parity achieved |

---

## 5. Module 4: Optima Weekly Update Tracker

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/optima_weekly_update_tracker/save` | `server.ts` (`/api/optima_weekly_update_tracker`) | `OptimaWeeklyUpdateTrackerController.save()` | Body: `OptimaWeeklyUpdateTrackerVM` | `CustomResponse(200, "Saved", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/optima_weekly_update_tracker/get_by_id/{id}` | `server.ts` (`/api/optima_weekly_update_tracker/:id`) | `OptimaWeeklyUpdateTrackerController.getById()` | URL Path: `id` | `CustomResponse(200, "Fetched", entity)` | Authenticated | Parity achieved |
| `PUT` | `/api/optima_weekly_update_tracker/get_all_with_pagination` | `server.ts` (`/api/optima_weekly_update_tracker/paginate`) | `OptimaWeeklyUpdateTrackerController.getAllWithPagination()` | Body: `SearchVM`, Query: `page`, `size` | `CustomResponse(200, "Fetched", Page)` | Authenticated | Parity achieved |
| `PUT` | `/api/optima_weekly_update_tracker/update` | `server.ts` (`PUT /api/optima_weekly_update_tracker/:id`) | `OptimaWeeklyUpdateTrackerController.update()` | Body: `OptimaWeeklyUpdateTrackerVM` | `CustomResponse(200, "Updated", entity)` | Authenticated | Parity achieved |
| `POST` | `/api/optima_weekly_update_tracker/send_mail` | `server.ts` (`/api/optima_weekly_update_tracker/send_mail`) | `OptimaWeeklyUpdateTrackerController.sendMail()` | Body: `MailRequestVM` | `CustomResponse(200, "Sent", status)` | Authenticated | Parity achieved |

---

## 6. Module 5: Task Owner Email

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/task_owner_email/send` | `server.ts` (`/api/email/task-owner`) | `TaskOwnerEmailController.sendTaskOwnerEmail()` | Body: `TaskOwnerEmailVM` | `CustomResponse(200, "Email Sent", entry)` | Authenticated | Parity achieved |
| `GET` | `/api/task_owner_email/logs` | `server.ts` (`/api/email/logs`) | `TaskOwnerEmailController.getEmailLogs()` | Query: `limit`, `category` | `CustomResponse(200, "Logs", List)` | Authenticated | Parity achieved |
| `GET` | `/api/task_owner_email/config` | `server.ts` (`/api/email/config`) | `TaskOwnerEmailController.getEmailConfig()` | None | `CustomResponse(200, "Config", sanitizedConfig)` | Authenticated | Parity achieved |

---

## 7. Module 6: Meeting Time Tracker

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/meeting_time_tracker/save` | `server.ts` (`/api/meeting_time_tracker`) | `MeetingTimeTrackerController.save()` | Body: `MeetingTimeTracker` entity | `CustomResponse(200, "Saved", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/meeting_time_tracker/get_by_id/{id}` | `server.ts` (`/api/meeting_time_tracker/:id`) | `MeetingTimeTrackerController.getById()` | URL Path: `id` | `CustomResponse(200, "Fetched", entity)` | Authenticated | Parity achieved |
| `PUT` | `/api/meeting_time_tracker/get_all_with_pagination` | `server.ts` (`/api/meeting_time_tracker/paginate`) | `MeetingTimeTrackerController.getAllWithPagination()` | Body: `SearchVM`, Query: `page`, `size` | `CustomResponse(200, "Fetched", Page)` | Authenticated | Parity achieved |
| `PUT` | `/api/meeting_time_tracker/update` | `server.ts` (`PUT /api/meeting_time_tracker/:id`) | `MeetingTimeTrackerController.update()` | Body: `MeetingTimeTracker` entity | `CustomResponse(200, "Updated", entity)` | Authenticated | Parity achieved |

---

## 8. Module 7: Dropdown Management

| HTTP Method | API Path | Express Implementation | Java Spring Boot Controller & Method | Request Structure | Response Structure | Auth Requirements | Implementation Status |
|---|---|---|---|---|---|---|---|
| `POST` | `/api/dropdowns/save` | `server.ts` (`/api/dropdowns`) | `DropdownManagementController.save()` | Body: `DropdownManagementVM` | `CustomResponse(200, "Saved", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/dropdowns/get_by_id/{id}` | `server.ts` (`/api/dropdowns/:id`) | `DropdownManagementController.getById()` | URL Path: `id` | `CustomResponse(200, "Fetched", entity)` | Authenticated | Parity achieved |
| `GET` | `/api/dropdowns/get_all` | `server.ts` (`GET /api/dropdowns`) | `DropdownManagementController.getAllDropdowns()` | None | `CustomResponse(200, "Fetched", List)` | Public / Authenticated | Parity achieved |
| `PUT` | `/api/dropdowns/update` | `server.ts` (`PUT /api/dropdowns/:id`) | `DropdownManagementController.update()` | Body: `DropdownManagementVM` | `CustomResponse(200, "Updated", entity)` | Authenticated | Parity achieved |
| `DELETE` | `/api/dropdowns/delete/{id}` | `server.ts` (`DELETE /api/dropdowns/:id`) | `DropdownManagementController.delete()` | URL Path: `id` | `CustomResponse(200, "Deleted", id)` | Authenticated | Parity achieved |

---

## 9. Parity & Gaps Summary
- **Endpoint Structure**: All 7 core business modules have matching controller endpoints in Java Spring Boot (`com.mymitraa.taskflow.api_controller`) and corresponding development mock routes in `server.ts`.
- **Response Format**: Both implementations return uniform JSON with payload data, status code, and metadata envelopes (`CustomResponse` in Java, `{ success, data }` in Express).
- **Authentication**: JWT bearer token headers (`Authorization: Bearer <token>`) are enforced in both implementations.
