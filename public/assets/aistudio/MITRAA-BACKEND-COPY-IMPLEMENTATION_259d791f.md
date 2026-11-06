# MyMitraa TaskFlow — Controlled Backend Working Copy Creation

We are moving from backend migration planning to actual backend working-copy creation.

The user authorizes creation of an isolated backend working copy using the verified complete-backend-migration-manifest.md.

## 1. Project paths

Original PM-TOOL source — READ ONLY:

`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

Destination:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\backend\mymitraa_taskflow`

Documentation:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\documentation`

Target package:

`com.mymitraa.taskflow`

Target application:

`MyMitraa TaskFlow`

Target database:

`mymitraa_taskflow`

## 2. Mandatory pre-copy checks

Before copying:

1. Read `complete-backend-migration-manifest.md`.
2. Read `backend-copy-manifest.md`.
3. Read `final-approval-readiness.md`.
4. Verify all source paths against the actual original source.
5. Identify duplicate entries and distinguish components from actual files.
6. Generate an exact list of files to be copied.
7. Verify that each file is necessary for the approved functionality or its required infrastructure.
8. Exclude unrelated legacy modules.
9. Exclude production credentials and secrets.
10. Confirm the destination is inside the isolated MyMitraa project.

If any dependency is missing or its source path is uncertain, do not invent it. Record the issue.

## 3. Copy authorization

The user authorizes copying:

- The 14 previously approved Java source files.
- Their verified required services and service implementations.
- Their verified required repositories.
- Required DTOs, VMs and MVs.
- Required security and authentication source files.
- Required configuration classes.
- Required utility and exception classes.
- Required Maven project structure and build files.
- Required non-secret resources.
- Required file-upload, email and infrastructure source components.

Only include files necessary for the MyMitraa approved backend scope.

Do not copy the entire original PM-TOOL project blindly.

## 4. Strict source protection

The original PM-TOOL backend must remain unchanged.

Do not modify, rename, delete or move any original file.

Use copy operations only.

If a copy operation fails, stop and report the error. Do not attempt destructive workarounds.

Do not request unrestricted permissions for unrelated filesystem operations.

## 5. Security restrictions

Never copy or reuse:

- Original JWT secrets.
- Original MongoDB passwords or usernames.
- Original SMTP passwords.
- Production API keys.
- Production database connection strings.
- Private certificates or production keys.

Preserve the configuration structure, but replace sensitive values with environment-variable placeholders.

Do not disable authentication or authorization.

Do not create weak default credentials.

Do not connect to any production database.

## 6. Package migration is NOT authorized yet

At this stage, preserve the original Java package declarations.

Do not rename:

`com.kelltontech.optima`

to:

`com.mymitraa.taskflow`

Package migration will be performed in a separate verified phase after copy verification.

## 7. Database and configuration restrictions

Do not connect to MongoDB.

Do not run database migrations.

Do not change any production configuration.

Do not start the backend.

Prepare safe development configuration templates only if included in the approved manifest. Use placeholders for secrets.

The intended development database name is:

`mymitraa_taskflow`

## 8. Copy verification

After copying, verify:

- Exact number of source files selected.
- Exact number of files copied.
- Every copied file has a corresponding original source file.
- Relative folder structure is preserved where required.
- No production secrets were copied.
- No unrelated modules were included.
- Original source remains unchanged.
- Destination is inside the MyMitraa workspace.

Create:

`documentation/backend-copy-verification.md`

Include a file-by-file verification table.

## 9. Stop condition

After completing the controlled copy:

- Do not rename Java packages.
- Do not upgrade Spring Boot or Java.
- Do not change database configuration.
- Do not generate replacement business logic.
- Do not begin frontend development.
- Do not connect to external services.
- Do not start the backend.

Report the exact copy results, any missing dependencies and any failures.

**STOP after working-copy creation and verification. Wait for explicit user approval before package migration and configuration implementation.**