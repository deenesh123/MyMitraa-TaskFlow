# MyMitraa TaskFlow — Read-Only Backend Copy Manifest

You are continuing the existing MyMitraa TaskFlow project.

**CURRENT PHASE: SOURCE VERIFICATION AND COPY MANIFEST ONLY.**

Do not copy files or implement anything in this task.

## 1. Safety rules

- Original PM-TOOL backend is READ ONLY.
- Do not modify, rename, delete, move, or copy original source files.
- Do not execute Maven builds or start the backend.
- Do not connect to any MongoDB database.
- Do not change global Claude configuration.
- Do not modify the ALS project or unrelated projects.
- Preserve all existing ALS information in memory files.
- Never print credentials, passwords, tokens, JWT secrets, or API keys.
- Use `[REDACTED]` if a secret is encountered.
- Do not invent source files, classes, methods, endpoints, or dependencies.
- Do not treat a referenced dependency as approved automatically.

## 2. Exact source and destination

Original source (READ ONLY):

`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

Intended isolated working-copy destination:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\backend\mymitraa_taskflow`

Target Java package after a separately approved migration:

`com.mymitraa.taskflow`

Target MongoDB database name:

`mymitraa_taskflow`

Do not perform package migration or database configuration now.

## 3. Exactly 14 approved source files

### Seven controllers

1. FeedbackHistoryController.java
2. GlobalFileUploadsController.java
3. OptimaLeaveTrackerController.java
4. OptimaWeeklyUpdateTrackerController.java
5. TaskOwnerEmailController.java
6. MeetingTimeTrackerController.java
7. DropdownManagementController.java

### Seven domain classes

1. FeedbackHistory.java
2. GlobalFileUploads.java
3. OptimaLeaveTracker.java
4. OptimaWeeklyUpdateTracker.java
5. MeetingTimeTracker.java
6. WorkLogEntry.java
7. BaseDomain.java

These are the only initially approved source files. Other files require separate approval.

## 4. Verify exact source files

For each of the 14 files:

- Confirm that it exists.
- Record its exact absolute source path.
- Record its Java package declaration.
- Record its class name.
- Record its direct imports.
- Identify its directly referenced classes.
- Identify the controller methods and endpoints where applicable.

Do not claim a file is verified unless you inspected its actual contents.

## 5. Dependency analysis

Inspect direct dependencies of the approved files.

Check:

- Service interfaces
- Service implementation classes
- Repository interfaces and implementations
- Domain classes
- DTOs
- VMs and MVs
- SearchVM
- AdvanceSearchList
- Security classes
- Configuration classes
- Utility classes
- Email classes
- File upload dependencies
- Required resources and configuration
- Maven dependencies, where relevant

For every dependency, provide:

| Field | Required information |
|---|---|
| Filename | Exact filename |
| Source path | Exact path if available |
| Referenced by | Approved source file and method |
| Purpose | What it is used for |
| Required | Essential / Optional / Unknown |
| Approval | Approved / Requires user approval / Missing / Not required |
| Reason | Explain the classification |

Do not automatically approve dependencies.

Do not create replacement classes or implement missing functionality.

If a dependency is missing, specify the exact missing filename and which approved source references it.

## 6. Exact endpoint audit

Inspect all seven approved controllers.

List every endpoint individually:

- HTTP method
- Exact route
- Controller class
- Method name
- Source file
- Request parameters or request body, if identifiable
- Response type, if identifiable

Count the complete endpoint list.

Do not use approximate statements such as "183+".

If an endpoint cannot be established from the actual source, mark it as unverified.

Compare the endpoint list with the latest available v3 API Reference document. Clearly distinguish:

- Exact match
- Partial match
- Missing from API reference
- Extra in source
- Cannot verify

Do not claim 100% coverage without evidence for every endpoint.

## 7. Buildability assessment

Determine whether the 14 approved files plus their required dependencies would be sufficient for a compilable, isolated backend working copy.

Check the requirements for:

- Maven project structure
- pom.xml
- Java version
- Spring Boot version
- Application configuration
- MongoDB configuration
- Security configuration
- Required resources
- External integrations
- Required dependency classes

Do not copy or modify any of these files.

Report what would be needed and whether each item has approval.

Do not connect to a real database or expose credentials.

## 8. Existing documentation review

Read the existing reports in:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\documentation`

Pay particular attention to:

- final-approval-readiness.md
- approved-source-inventory.md
- controller-domain-service-mapping.md
- api-reference-mapping.md
- frontend-screen-reference-mapping.md
- missing-source-files-request.md
- mymitraa-implementation-roadmap.md

Check consistency between the reports and actual inspected source files.

Do not silently preserve unsupported claims from earlier reports.

## 9. Required output

Create:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\documentation\backend-copy-manifest.md`

Include these sections:

1. Executive summary
2. Verification of all 14 approved source files
3. Exact source-to-destination mapping
4. Complete dependency inventory
5. Missing dependency list
6. Exact endpoint inventory
7. API Reference v3 comparison
8. Maven and buildability assessment
9. Security and configuration considerations
10. Files requiring additional user approval
11. Exact file counts by category
12. Final recommendation

Use only evidence from inspected files and available documentation.

## 10. Final status

Choose exactly one:

- READY FOR USER APPROVAL — CONTROLLED COPY
- NOT READY — MISSING SOURCE FILES
- NOT READY — DEPENDENCY APPROVAL REQUIRED
- NOT READY — API OR DOCUMENTATION GAPS
- NOT READY — BUILD STRUCTURE OR CONFIGURATION GAPS

Explain the reason for the selected status.

## 11. Strict stop condition

This task authorizes documentation only.

You must NOT:

- Copy any backend files.
- Create a backend working copy.
- Rename Java packages.
- Change database settings.
- Modify the frontend.
- Generate replacement source classes.
- Modify the original PM-TOOL backend.
- Modify ALS project files.

After creating the manifest, report the exact output path and a concise summary of findings.

**STOP and wait for explicit user approval.**