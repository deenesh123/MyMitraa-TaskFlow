# MyMitraa TaskFlow — Phase 1.5 and Safe Backend Copy

## 1. PROJECT OVERVIEW

Project name: MyMitraa TaskFlow

Objective: Build a complete enterprise project and task management application with:

- A completely new frontend.
- A customized backend based on the existing PM-TOOL source code.
- A new, isolated MongoDB database.
- MyMitraa branding, naming and configuration.
- Real frontend-to-backend API integration.

This is NOT a new backend development project. We must reuse and adapt the existing backend wherever technically suitable.

## 2. APPROVED PROJECT DECISIONS

The following decisions are approved:

| Setting | Approved value |
|---|---|
| Product name | MyMitraa TaskFlow |
| Java package | `com.mymitraa.taskflow` |
| Application name | MyMitraa TaskFlow |
| MongoDB database | `mymitraa_taskflow` |
| Backend strategy | Separate working copy |
| Frontend | Build from scratch |
| Original backend | Preserve untouched |

These are approved target values. They do not authorize immediate bulk changes.

## 3. PROJECT LOCATIONS

### Original backend source — READ-ONLY

`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

This is the original backend. It must remain untouched.

### Claude Code Lab

`C:\Users\LENOVO\Claude-Code-Lab`

### Project workspace

`C:\Users\LENOVO\Claude-Code-Lab\workspace`

### REQUIRED NEW PROJECT STRUCTURE

Create the following structure:

```text
C:\Users\LENOVO\Claude-Code-Lab\workspace\
└── projects\
    └── mymitraa-taskflow\
        ├── backend\
        │   └── mymitraa_taskflow\
        ├── frontend\
        ├── documentation\
        └── memory\
```

IMPORTANT NAMING REQUIREMENT:

The copied backend directory MUST be named:

`mymitraa_taskflow`

Do NOT retain `pep_optima_well_service` as the working-copy directory name.

The original source directory must retain its original name and location.

The Java package will be migrated separately to:

`com.mymitraa.taskflow`

The Maven artifact ID, Maven project name, internal references and application configuration will be reviewed separately before modification.

Do not confuse the working-copy folder rename with Java package migration.

## 4. PHASE 1 — COMPLETE BACKEND COMPATIBILITY REVIEW

Inspect the original PM-TOOL backend thoroughly in read-only mode.

Identify:

1. Programming language and framework.
2. Spring Boot and JHipster architecture.
3. Java version and Maven configuration.
4. Controllers and API routes.
5. Services and business logic.
6. Domain models and DTOs.
7. MongoDB repositories and relationships.
8. Authentication and authorization.
9. Security configuration and JWT handling.
10. File upload and download functionality.
11. Email and notification integrations.
12. External services and dependencies.
13. Configuration and environment variables.
14. Build and deployment configuration.
15. Tests and existing test coverage.

Determine whether the backend supports the requirements of MyMitraa TaskFlow.

## 5. PHASE 1.5 — FRONTEND MODULE COMPATIBILITY

Use the supplied documents:

- `implementation_plan.md`
- `MiTRAA_TaskFlow_API_Reference_v3.docx`

Inspect and map these 10 frontend modules:

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

For each module, classify:

- Fully supported by existing backend.
- Partially supported by existing backend.
- Not supported by existing backend.

For every existing API, identify:

- Controller.
- HTTP method.
- Actual endpoint.
- Request parameters.
- Request payload.
- Response structure.
- Authentication requirements.
- Related services and database models.

Identify missing functionality and required backend development.

Do not invent endpoints, payloads or database fields.

## 6. PHASE 2 — KELLTON TO MYMITRAA IMPACT ANALYSIS

Search the entire backend for references to:

- Kellton
- kelltontech
- optima
- WellService
- Existing company branding and application names.

Categorize references into:

### A. Branding references

- Application display name.
- Company name.
- Email templates.
- Swagger documentation.
- UI labels.
- Logs and messages.
- Notification templates.

### B. Java package references

Existing package:

`com.kelltontech.optima`

Target package:

`com.mymitraa.taskflow`

Identify all affected:

- Java package declarations.
- Import statements.
- Directory structures.
- Component scanning.
- Configuration classes.
- Test classes.
- Maven references.
- Resource references.
- Reflection and class-loading references.

### C. Technical and infrastructure references

- File paths.
- Storage directories.
- Database settings.
- Mail configuration.
- Deployment configuration.
- Environment variables.
- External service integrations.

Do not blindly replace every occurrence of Kellton or Optima. Determine whether each reference is branding, a technical dependency or historical data.

## 7. PHASE 3 — SAFE BACKEND WORKING COPY

Original source:

`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

Destination:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\backend\mymitraa_taskflow`

### Before copying

1. Verify the original source exists.
2. Verify the destination parent can be created.
3. Check available disk space.
4. Check whether the destination already exists.
5. Record the original file count and approximate size.
6. Inspect the original Git repository status.
7. Identify important configuration files.
8. Ensure credentials are not printed in the report.

If the destination already exists, STOP. Do not overwrite or merge with existing files without approval.

### Copy requirements

1. Create the destination directory.
2. Copy the complete backend source into it.
3. Preserve Java source files.
4. Preserve Maven configuration.
5. Preserve application resources.
6. Preserve configuration files.
7. Preserve required hidden project files.
8. Exclude generated build output such as `target` directories.
9. Do not copy Git metadata into the working copy.
10. Do not copy unrelated projects or files.

The destination directory MUST be named `mymitraa_taskflow`.

Use a copy operation only. Never move, rename, edit or delete the original source.

### After copying

Verify:

- Original source still exists.
- Original source has not been modified.
- Destination exists at the exact approved path.
- Expected Java files are present.
- Maven configuration is present.
- Application configuration is present.
- Resources and tests are present.
- Copy errors are documented.
- Source and destination file counts are reasonably consistent after excluding generated output and Git metadata.

Record the copy verification results.

## 8. PHASE 4 — JAVA PACKAGE MIGRATION PLAN

Target package:

`com.mymitraa.taskflow`

Prepare a complete migration plan covering:

- Package directory hierarchy.
- Java package declarations.
- Import statements.
- Component scanning.
- Configuration references.
- Test package structure.
- Maven configuration.
- Reflection and class-loading references.
- Build scripts.
- Resource references.

Do not rename packages during this phase.

The actual migration will happen only after the working copy has been verified and the migration plan approved.

## 9. PHASE 5 — MONGODB ISOLATION PLAN

Approved target database:

`mymitraa_taskflow`

Inspect the current MongoDB configuration.

Identify:

- Database connection configuration.
- MongoDB host configuration.
- Database name.
- Collection names.
- Indexes.
- Data models.
- Repository dependencies.
- Existing database initialization and migration mechanisms.

Prepare a plan to configure a separate MyMitraa database.

STRICT REQUIREMENTS:

- Never modify the original database.
- Never drop existing collections.
- Never overwrite existing data.
- Do not run database migrations.
- Do not connect to a live database during discovery.
- Do not expose database credentials.
- Use environment variables for sensitive configuration.
- Identify any database migration risks.

## 10. PHASE 6 — NEW FRONTEND PLAN

There is currently NO frontend source code.

Create a new frontend using the supplied implementation plan.

Target stack:

- React
- TypeScript
- Tailwind CSS

Follow the implementation plan's design system, branding, component architecture and functional requirements.

The frontend must include:

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

Reuse existing backend functionality wherever possible.

Do not build fake functionality or invent APIs to make screens appear operational.

## 11. SECURITY AND CONFIGURATION RULES

The initial discovery identified potentially exposed email credentials and a JWT secret.

Treat those values as compromised.

- Never reproduce them in reports.
- Never commit secrets.
- Do not reuse exposed credentials.
- Use newly generated secrets for the new environment.
- Keep credentials in environment variables.
- Use separate database credentials.
- Review JWT signing and token expiration settings.
- Review CORS and authentication configuration before deployment.

Do not connect to production systems or send real emails during discovery.

## 12. REQUIRED DOCUMENTATION

Create or update the following files inside the project documentation and memory directories:

1. `mymitraa-backend-compatibility-report.md`
2. `mymitraa-api-mapping.md`
3. `mymitraa-renaming-impact-report.md`
4. `mymitraa-database-migration-plan.md`
5. `mymitraa-implementation-roadmap.md`
6. `mymitraa-backend-copy-verification.md`
7. `work_state.md`

Use existing workspace memory conventions where appropriate.

Do not overwrite unrelated workspace memory or existing ALS project records.

## 13. STRICT DEVELOPMENT RULES

1. Preserve the original PM-TOOL backend.
2. Keep all new project work inside the approved MyMitraa project directory.
3. Do not modify global Claude Code configuration.
4. Do not modify unrelated projects.
5. Do not remove existing business logic.
6. Do not delete controllers or services.
7. Do not perform destructive database operations.
8. Do not install unnecessary dependencies.
9. Do not expose credentials or tokens.
10. Do not perform bulk replacements without a reviewed impact report.
11. Do not start frontend implementation before completing the architecture review.
12. Keep a progress checklist.
13. Report every file created or modified.

## 14. EXECUTION ORDER

Follow this exact order:

1. Complete backend discovery.
2. Complete frontend compatibility assessment.
3. Complete Kellton renaming impact analysis.
4. Prepare database isolation plan.
5. Prepare package migration plan.
6. Create the separate backend working copy named `mymitraa_taskflow`.
7. Verify the working copy.
8. Update project documentation and work state.
9. Present the complete report.

## 15. STOP CONDITION

After completing the above tasks:

STOP.

Do not begin:
- Java package renaming.
- Maven artifact renaming.
- Application branding modifications.
- Database configuration changes.
- Database migrations.
- Backend feature development.
- Frontend implementation.

Present the results and wait for my explicit approval before proceeding to the next implementation phase.

The goal is a safe, maintainable MyMitraa TaskFlow application built from the existing backend without damaging the original PM-TOOL project.