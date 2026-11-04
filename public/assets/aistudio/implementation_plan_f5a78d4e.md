# MiTRAA TaskFlow Implementation Plan

MiTRAA TaskFlow is an enterprise project and task management web application built with React, TypeScript, and Tailwind CSS following the official Frontend BRD (v2) and REST API Reference (v3). It features the 5-colour palette (Violet, Fuchsia, Lime, Sky Blue, Charcoal), soft pastel cards with large rounded corners, the penguin mascot, and a unified API client supporting both live endpoints and realistic mock data.

***

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions and document reconciliations have been established based on the BRD and API Reference:

1. **Product Name & Branding**: Confirmed as **MiTRAA TaskFlow** with the custom violet product mark, soft pastel background gradient, and penguin mascot assets.
2. **Overall Progress Formula**: Reconciled the reference mockup conflict (78% vs 62/124). Progress will strictly follow $\text{Closed} \div \text{Total}$ (e.g., $62 / 124 = 50\%$) with a unified formula across the circular progress ring and the "You're on track!" banner.
3. **Calendar View & Timeline View Data Rule**: Tasks are retrieved via `get_all_feedback` / `advance_search`. The user identity is extracted from the auth session token (no user picker for regular users). Bars span `estimated_start_date` to `estimated_close_date`.
4. **Task Effort Rule**: `actual_effort` is strictly read-only and automatically accumulated from daily `work_log_entries` (date, hours, description).
5. **API Gaps & Fallbacks**:
   - **Trend Percentages**: `feedback_numerical_count` provides current counts only; previous-period trends will calculate from available date history or mock trend metrics.
   - **Upcoming Schedule**: Leverages `optima_leave_tracker` (leaves, WFH, holidays) and upcoming `meeting_time_tracker` entries.
   - **Notification Bell**: Positioned in the top bar with popover list (mocked / empty state with unread badge counter).
   - **Dropdown Management Upload**: Only `/download` exists in the backend controller; upload action is marked as an API gap.
   - **Dual Execution Mode**: Central `USE_MOCK` toggle (defaults to `true` when backend URL is unset or offline) with high-fidelity realistic seed data matching BRD sample figures (657 meetings, 320 status records, 50 documents, 9 unlogged users).

***

## 1. System Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                          MiTRAA TaskFlow Root                          │
│                  (ToastProvider, AuthProvider, ApiClient)              │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
    ┌───────────────────────────────┴─────────────────────────────────┐
    │                       AppLayout Frame                           │
    ├───────────────────────────────┬─────────────────────────────────┤
    │ TopBar (Ctrl+K Search,        │ Left Sidebar (12 Menu Entries,  │
    │  Quick Actions, User Profile) │  Mascot Card, Collapse Toggle)  │
    └───────────────────────────────┴─────────────────────────────────┘
                                    │
    ┌───────────────────────────────┴─────────────────────────────────┐
    │                         Route Router                            │
    ├─────────────────────────────────────────────────────────────────┤
    │ • /                          → Home Dashboard (HD-1 to HD-9)    │
    │ • /task-dashboard            → Reports (DB-1 to DB-4)           │
    │ • /meeting-tracker           → Meeting Tracker (MT-1 to MT-7)   │
    │ • /leave-wfh-dashboard       → Leaves / WFH Tracker (LW-1..LW-6)│
    │ • /project-weekly-status     → Project Status (PS-1 to PS-6)    │
    │ • /task-upload-files         → View Documents (VD-1 to VD-6)    │
    │ • /users/task-calendar-view  → Calendar View (CV-1 to CV-6)     │
    │ • /users/timeline-view       → Timeline View (Gantt Gantt-bars) │
    │ • /users/dropdown-management → Dropdown Management (DM-1..DM-6) │
    │ • /task-tracker              → Task Tracker list & Work Log     │
    └─────────────────────────────────────────────────────────────────┘
                                    │
    ┌───────────────────────────────┴─────────────────────────────────┐
    │                   Shared Component Foundation                   │
    ├─────────────────────────────────────────────────────────────────┤
    │ • DataTable: pagination (10/25/100), sort, actions, row hover   │
    │ • FilterRow: wrapping responsive filters, Search & Clear        │
    │ • DateRangePicker: formatted "DD-Mon-YYYY – DD-Mon-YYYY"        │
    │ • StatusChip: strict 5-colour palette + functional Amber/Red    │
    │ • ModalForm & ConfirmDialog: accessible, focus-trapped, escape  │
    │ • MascotState: empty ("Data Not Available") & error recovery    │
    │ • GlobalSearchDialog: Ctrl+K / Cmd+K with module parameter list │
    └─────────────────────────────────────────────────────────────────┘
```

***

## 2. Directory & File Organization

```
/src
├── /api
│   ├── client.ts              # Typed Axios/Fetch client, Authorization token, CustomResponse<T>
│   ├── endpoints.ts           # Exact paths mapped to API Reference controllers
│   ├── mockData.ts            # High-fidelity mock seed data matching BRD captures
│   └── types.ts               # Shared interfaces (FeedbackHistory, Meeting, Leave, WeeklyUpdate, etc.)
├── /components
│   ├── /layout
│   │   ├── AppLayout.tsx      # Unified layout container
│   │   ├── TopBar.tsx         # Ctrl+K search, notifications, user avatar dropdown
│   │   ├── Sidebar.tsx        # 12 grouped menu items + mascot card + collapse
│   │   └── SearchModal.tsx    # Ctrl+K modal with module parameter filtering
│   ├── /common
│   │   ├── DataTable.tsx      # Standard table with actions, paging (10/25/100)
│   │   ├── FilterBar.tsx      # Responsive wrap filters with Search & Clear
│   │   ├── DateRangeBox.tsx   # Single input formatted DD-Mon-YYYY – DD-Mon-YYYY
│   │   ├── StatusBadge.tsx    # Status chips (Violet, Sky Blue, Lime, Fuchsia, Amber)
│   │   ├── Modal.tsx          # Accessible dialog with Charcoal CTA & cancel
│   │   ├── ConfirmDialog.tsx  # Confirmation modal for delete actions
│   │   ├── Toast.tsx          # Global notification alerts
│   │   ├── MascotEmpty.tsx    # Penguin mascot with "Data Not Available"
│   │   └── Skeleton.tsx       # Placeholder shape loading states
│   └── /mascot
│       └── MascotPenguin.tsx  # Multi-pose SVG/Canvas penguin mascot (waving, celebrating, clipboard, puzzled)
├── /pages
│   ├── /home
│   │   ├── HomeDashboard.tsx  # Welcome banner, overall progress ring, 4 KPI cards, My Tasks, Upcoming, Banner
│   │   ├── OverallProgress.tsx
│   │   ├── KpiCards.tsx
│   │   ├── MyTasksPanel.tsx
│   │   └── UpcomingSchedule.tsx
│   ├── /reports
│   │   └── ReportsPage.tsx    # 4 tabs/views: Status Overview, Effort, Meeting Time, Leaves/WFH
│   ├── /meetings
│   │   ├── MeetingTracker.tsx # 657 meetings table, filter row, Add Meeting modal
│   │   └── MeetingForm.tsx
│   ├── /leaves
│   │   ├── LeaveWfhTracker.tsx# Month calendar, 5-color bars, legend, Add Leave/WFH modal
│   │   └── LeaveForm.tsx
│   ├── /project-status
│   │   ├── ProjectStatus.tsx  # Weekly board (People, Envs, Status), week banner, Add Update modal
│   │   └── WeeklyUpdateForm.tsx
│   ├── /documents
│   │   ├── ViewDocuments.tsx  # File list, upload chip, preview, delete modal
│   │   └── FilePreviewModal.tsx
│   ├── /calendar
│   │   └── TaskCalendarView.tsx# Project selector, status chips, multi-day task bars, +N more hover
│   ├── /timeline
│   │   └── TimelineView.tsx   # Gantt timeline view grouped by project/owner
│   ├── /dropdowns
│   │   ├── DropdownManagement.tsx# Selector, wrap filter, Add Task Owner modal, table
│   │   └── DropdownForm.tsx
│   └── /tasks
│       ├── TaskTrackerPage.tsx# Task list with Work Log modal (actual_effort read-only)
│       └── WorkLogModal.tsx
├── /utils
│   ├── dateUtils.ts           # DD-Mon-YYYY parser and formatter
│   └── colorTokens.ts         # Fixed 5-colour tokens and contrast guard
├── App.tsx                    # Route registration & state provider
└── index.css                  # Tailwind styles & font tokens
```

***

## 3. Screen Specifications & Requirements Map

| Screen | Route | Key Requirements & Elements |
|---|---|---|
| **Home Dashboard** | `/` | **HD-1**: Welcome banner with tag line, headline, "Create New Task" button, mascot.<br>**HD-2**: Overall progress circular ring ($\text{Closed} \div \text{Total}$).<br>**HD-3**: 4 KPI cards (Total Tasks with On Hold note, In Progress, Closed, Open) with soft illustrations.<br>**HD-4**: My Tasks with All, Today, This Week, Overdue tabs; ticket #, title, team chip, priority chip, due date, owner.<br>**HD-5**: Upcoming Schedule date tile with leave/WFH/holidays and meetings.<br>**HD-6**: Progress banner ("You're on track!") with mascot & progress bar.<br>**HD-7**: Month period selector.<br>**HD-8**: Amber alert strip: "People not filling tasks in project management tool" (9 people).<br>**HD-9**: Isolated loading/empty/error states per card. |
| **Reports** | `/task-dashboard` | **DB-1**: Status overview donut chart, priority breakdown, team workload, type breakdown, alert strip.<br>**DB-2**: Effort daily hours by owner, effort distribution by team/project, project vs actual effort.<br>**DB-3**: Meeting time stacked bars per person in minutes (52h, 2h 45m).<br>**DB-4**: Leaves/WFH distribution pie and bar summary ("1 day", not "1 days"). |
| **Meeting Tracker** | `/meeting-tracker` | **MT-1**: Add New meeting form (date, customer, project, module, team, organizer, attendees, lead/architect attended, times).<br>**MT-2**: Filter row with search parameter, date range, download icon.<br>**MT-3 to MT-7**: Table with 3-dot action menu, DD-Mon-YYYY dates, 100/page default. |
| **Leaves / WFH** | `/leave-wfh-dashboard` | **LW-1 to LW-6**: Month calendar view (no license key banner), 5-color bars (Leave, Unplanned, WFH Approved, WFH Unapproved dashed, Holiday), "+N more" collapse, legend below. |
| **Project Status** | `/project-weekly-status` | **PS-1 to PS-6**: Weekly update board with People (UX, BE, Python, Testers, Shared), Environments (Dev, QA, LA, Other URLs & arrow toggles), Status (Last week, current, next steps), Monday–Friday week banner, mascot empty state. |
| **View Documents** | `/task-upload-files` | **VD-1 to VD-6**: 7 filters, Search & Clear Filters, upload with filename chip, date range filter applied to data, preview/download/delete actions. |
| **Calendar View** | `/users/task-calendar-view` | **CV-1 to CV-6**: Project dropdown, 4 status chips, month grid with estimated start-to-close bars, hover tooltip showing full details, "+N more" popover. |
| **Timeline View** | `/users/timeline-view` | Gantt timeline showing tasks over time grouped by project or task owner, using start and end dates. |
| **Dropdown Management** | `/users/dropdown-management` | **DM-1 to DM-6**: Dropdown details selector, header button "Add Task Owner", wrapping filter row, download button, S.No, Actions, Recorded Date, Task Owner, Comments. |

***

## 4. Visual Design & Color Rules

- **Fixed 5-Colour Palette**:
  - **Violet (`#7B3AEC`)**: Active menu pill, primary CTA highlights, Open status, primary chart series.
  - **Fuchsia (`#D946EF`)**: Accent gradient end, On Hold status, Holiday.
  - **Lime (`#A3D801`)**: Closed status, Approved WFH, progress fill.
  - **Sky Blue (`#00A6FC`)**: In Progress status, secondary chart series, info badges.
  - **Charcoal (`#191627`)**: Text, dark primary pill buttons (`Create New Task`), sidebar mascot card.
- **Functional Colors**:
  - **Amber (`#D97706` / `#F59E0B`)**: Overdue, Unplanned Leave, warning alerts ("People not filling tasks").
  - **Red (`#DC2626`)**: Critical P0 priority, destructive delete confirmations.
- **Card Aesthetics**: Large rounded corners (`rounded-2xl`, `rounded-3xl`), soft tint backgrounds matching status, subtle shadows (`shadow-sm`, `shadow-md`), zero harsh outlines.
- **Dates**: Strict `DD-Mon-YYYY` format (e.g., `03-Sep-2026`) across all tables, calendars, and forms.

***

## 5. Next Steps for Execution

1. Build core foundation: API client (`src/api/client.ts`), types (`src/api/types.ts`), and mock data store (`src/api/mockData.ts`).
2. Build shared UI components: Layout (TopBar, Sidebar, MascotCard), DataTable, FilterBar, DateRangeBox, ModalForm, ConfirmDialog, MascotEmpty.
3. Build **Home Dashboard** with all 9 requirements (HD-1 through HD-9).
4. Build remaining 7 screens sequentially: Dropdown Management, Meeting Tracker, Leaves/WFH Tracker, Project Status, View Documents, Calendar View, Timeline View, and Reports.
5. Verify build via `compile_applet` and output requirement verification table and API endpoints catalog.
