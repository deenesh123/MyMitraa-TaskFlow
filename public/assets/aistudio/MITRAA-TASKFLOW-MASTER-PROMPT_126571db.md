# MiTRAA TaskFlow — Master Project Instructions

## PROJECT OBJECTIVE

Build a complete enterprise project and task management application called **MyMitraa TaskFlow**.

This project will use:

1. An entirely new frontend developed from the supplied MiTRAA implementation plan.
2. The existing PM-TOOL backend as the starting point.
3. Backend customization to replace the existing Kellton identity/configuration with MyMitraa wherever appropriate.
4. Real API integration between the new frontend and customized backend.

This is an existing-backend customization and new-frontend development project, NOT a new backend development project.

## PROJECT LOCATIONS

Existing backend source:

C:\Users\LENOVO\Downloads\PM-TOOL

Claude Code Lab:

C:\Users\LENOVO\Claude-Code-Lab

Project workspace:

C:\Users\LENOVO\Claude-Code-Lab\workspace

Frontend documentation:
- implementation_plan.md
- MiTRAA_TaskFlow_API_Reference_v3.docx
- MiTRAA_TaskFlow_Frontend_BRD_v3.docx

## PHASE 1 — COMPLETE PROJECT DISCOVERY

Before making changes:

1. Inspect the entire PM-TOOL backend structure.
2. Identify its framework, language, dependencies and architecture.
3. Inspect controllers, services, models, DTOs, database, authentication and middleware.
4. Identify all existing references to Kellton, including:
   - Application branding.
   - Company names and display labels.
   - Namespaces and package identifiers.
   - Configuration files.
   - Environment variables.
   - Database names and connection settings.
   - Import paths and references.
   - File storage paths.
   - Email templates and notification branding.
   - Authentication and authorization configuration.
   - Deployment and build configuration.
5. Identify which references are business branding and which are technical identifiers.
6. Inspect the supplied API reference and implementation plan.

Do not assume that every occurrence of Kellton should be blindly replaced.

Prepare a complete discovery report before implementation.

## PHASE 2 — BACKEND CUSTOMIZATION

Customize the existing backend for MyMitraa.

Requirements:

- Replace applicable Kellton branding with MyMitraa.
- Update application names and product identity.
- Update namespaces, package identifiers and import paths where appropriate.
- Update project configuration and application settings.
- Review database naming and connection configuration.
- Review file upload and storage configuration.
- Review authentication and authorization settings.
- Review email templates and notifications.
- Review logging and deployment configuration.
- Update API documentation where required.

IMPORTANT:

- Preserve existing business logic.
- Preserve existing database relationships.
- Preserve existing API functionality.
- Preserve authentication and permissions.
- Do not remove existing controllers or services.
- Do not invent new API contracts.
- Do not expose or overwrite credentials.
- Identify database migration requirements before renaming database objects.
- Keep a record of every modified file.

## PHASE 3 — NEW FRONTEND DEVELOPMENT

There is currently NO frontend source code to continue.

Create a new frontend based on the supplied implementation plan.

Follow the specified technology stack and architecture:
- React
- TypeScript
- Tailwind CSS

Implement the 10 application modules:

1. Home Dashboard
2. Reports
3. Meeting Tracker
4. Leave/WFH
5. Project Status
6. View Documents
7. Calendar
8. Timeline
9. Dropdown Management
10. Task Tracker

Follow the supplied design system, branding, components, data relationships and functional requirements.

Use the existing backend APIs wherever possible.

Do not create fake endpoints to make the UI appear functional.

## PHASE 4 — FRONTEND AND BACKEND INTEGRATION

Connect the new frontend with the customized backend.

Implement:

- API client and endpoint configuration.
- Authentication and token handling.
- TypeScript API interfaces.
- Centralized response handling.
- Error handling.
- Search and pagination.
- CRUD operations.
- File uploads and downloads.
- Form validation.
- Role-based access.
- Loading and empty states.

Use real backend responses.

Use mock data only where the backend genuinely does not provide the required functionality, and clearly separate mock mode from live mode.

## PHASE 5 — TESTING

Verify:

- Backend builds successfully.
- Frontend builds successfully.
- Existing backend functionality is preserved.
- Authentication works.
- API integrations work.
- All 10 modules function correctly.
- Database operations work.
- File uploads and downloads work.
- No broken imports or configuration references remain.
- No accidental credentials are committed.

## CRITICAL DEVELOPMENT RULES

1. Work inside the isolated Claude Code Lab.
2. Keep the original PM-TOOL source untouched as the reference.
3. Create a separate working copy before modifying backend code.
4. Never overwrite the original backend.
5. Do not modify global Claude Code settings.
6. Do not modify unrelated projects.
7. Do not install unnecessary dependencies.
8. Do not delete existing functionality.
9. Do not perform destructive database operations.
10. Do not make changes to production systems.
11. Use environment variables for sensitive configuration.
12. Maintain a project progress checklist.

## EXECUTION STRATEGY

Work in controlled phases.

First:
- Inspect the backend.
- Inspect the documentation.
- Identify Kellton references.
- Identify required MyMitraa changes.
- Prepare the architecture and API mapping.
- Recommend the working directory structure.

Then STOP and present the analysis report for approval.

Do not start bulk replacements, backend modifications, database changes or frontend implementation until approval.

After approval, implement one phase at a time.

After each phase, report:
- Files created.
- Files modified.
- Features implemented.
- Build and test results.
- Remaining issues.
- Next recommended phase.

The final result must be a working MyMitraa TaskFlow application using the customized existing backend and a newly developed frontend.