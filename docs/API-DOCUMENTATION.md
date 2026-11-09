# MyMitraa TaskFlow — API Architecture & Endpoint Reference

This document catalogs all 20 Spring Boot REST controllers in `com.mymitraa.taskflow.api_controller`, including their endpoint paths, required roles, HTTP methods, and payload structures.

---

## 1. Unified Response Contract

All endpoints return a standardized `CustomResponse<T>` wrapper:

```json
{
  "status": "SUCCESS",
  "message": "Operation completed successfully",
  "data": { ... },
  "metaInfo": {
    "totalCount": 42,
    "page": 1,
    "token": "eyJhbGciOi..."
  }
}
```

Error response example (HTTP 4xx / 5xx):
```json
{
  "status": "ERROR",
  "message": "Resource not found or unauthorized",
  "data": null,
  "metaInfo": null
}
```

---

## 2. Controller & Endpoint Catalog

### 1. AuthController (`/api/auth`)
- `POST /api/auth/login` — Public. Accepts `{ username, password }`. Returns `CustomResponse<UserAccount>` with `metaInfo.token`.
- `POST /api/auth/register` — Public. Creates pending/active user account. Does not issue token.
- `POST /api/auth/logout` — Authenticated. Invalidates session.
- `GET /api/auth/current` — Authenticated. Validates active token, returns authenticated `UserAccount`.

### 2. TaskController (`/api/task` and `/api/tasks`)
- `GET /api/task` — List tasks with query params (`projectId`, `assignedTo`, `status`, `priority`).
- `GET /api/task/{id}` — Get task by MongoDB ObjectID.
- `POST /api/task` — Create new task.
- `PUT /api/task/{id}` — Update task details.
- `DELETE /api/task/{id}` — Delete task (`ADMIN` / `MANAGER`).

### 3. ProjectController (`/api/project` and `/api/projects`)
- `GET /api/project` — List active projects.
- `GET /api/project/{id}` — Get project by ID.
- `POST /api/project` — Create new project.
- `PUT /api/project/{id}` — Update project metadata.
- `DELETE /api/project/{id}` — Archive or delete project (`ADMIN`).

### 4. ProjectStatusController (`/api/projectStatus`)
- `GET /api/projectStatus` — List project status lookup entries.
- `POST /api/projectStatus` — Add custom status (`ADMIN`).

### 5. UserController (`/api/user` and `/api/users`)
- `GET /api/user` — List users (`ADMIN` or authenticated user directory).
- `GET /api/user/{id}` — Get user profile.
- `PUT /api/user/{id}` — Update profile.
- `DELETE /api/user/{id}` — Deactivate/delete user (`ADMIN` only).

### 6. RoleController (`/api/role`)
- `GET /api/role` — List system roles (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER`).
- `POST /api/role` — Create role definition (`SUPER_ADMIN`).

### 7. LeadController (`/api/lead`)
- `GET /api/lead` — List sales and client leads.
- `POST /api/lead` — Create new lead.
- `PUT /api/lead/{id}` — Update lead status, deal value, contacts.
- `DELETE /api/lead/{id}` — Delete lead (`ADMIN`).

### 8. BugController (`/api/bug`)
- `GET /api/bug` — List issue/bug tickets.
- `POST /api/bug` — File new bug report with severity, reproduction steps.
- `PUT /api/bug/{id}` — Update bug status (Open, In Progress, Resolved, Closed).

### 9. DocumentController (`/api/document`)
- `POST /api/document/upload` — Multipart form upload (`file`, `category`, `referenceId`).
- `GET /api/document/download/{id}` — Stream file attachment.
- `GET /api/document/list` — List documents for an entity.

### 10. AuditLogController (`/api/audit` and `/api/auditLog`)
- `GET /api/audit` — List system audit logs with filters (`ADMIN` only).
- `GET /api/audit/user/{userId}` — Logs for specific user.

### 11. EmailController (`/api/email`)
- `GET /api/email/config` — View SMTP configuration (host, port, sender email; passwords redacted).
- `POST /api/email/test` — Trigger live test email via JavaMailSender (`ADMIN` only).
- `GET /api/email/logs` — List email audit trail.

### 12. TaskOwnerEmailController (`/api/taskOwnerEmail`)
- `GET /api/taskOwnerEmail` — List task owner email assignment notifications.
- `POST /api/taskOwnerEmail` — Record assignment notification.

### 13. FeedbackHistoryController (`/api/feedbackHistory`)
- `GET /api/feedbackHistory` — List customer/manager feedback history.
- `POST /api/feedbackHistory` — Record feedback item.

### 14. LeaveRequestController (`/api/leaveRequest`)
- `GET /api/leaveRequest` — List leave requests (user filtered or all for `MANAGER`).
- `POST /api/leaveRequest` — Submit leave request.
- `PUT /api/leaveRequest/{id}/status` — Approve/Reject leave (`MANAGER`, `ADMIN`).

### 15. OptimaLeaveTrackerController (`/api/optimaLeaveTracker`)
- `GET /api/optimaLeaveTracker` — Leave tracking matrix and balances.
- `POST /api/optimaLeaveTracker` — Update leave ledger.

### 16. OptimaWeeklyUpdateTrackerController (`/api/optimaWeeklyUpdateTracker`)
- `GET /api/optimaWeeklyUpdateTracker` — List weekly project update digests.
- `POST /api/optimaWeeklyUpdateTracker` — Save update digest and trigger notification.

### 17. MeetingController (`/api/meeting`)
- `GET /api/meeting` — List scheduled meetings.
- `POST /api/meeting` — Schedule new meeting.
- `PUT /api/meeting/{id}` — Reschedule or update attendees.

### 18. MeetingTimeTrackerController (`/api/meetingTimeTracker`)
- `GET /api/meetingTimeTracker` — View logged meeting time records.
- `POST /api/meetingTimeTracker` — Log meeting duration.

### 19. TimelineEntryController (`/api/timelineEntry`)
- `GET /api/timelineEntry` — Chronological activity feed for tasks and projects.
- `POST /api/timelineEntry` — Record activity event.

### 20. DropdownManagementController (`/api/dropdownManagement`)
- `GET /api/dropdownManagement` — Dynamic dropdown options (priorities, departments, categories).
- `POST /api/dropdownManagement` — Add or modify dropdown items (`ADMIN`).

### 21. HealthController (`/api/health`)
- `GET /api/health` — Public liveness check returning `{ "status": "UP", "app": "MyMitraa TaskFlow" }`.
