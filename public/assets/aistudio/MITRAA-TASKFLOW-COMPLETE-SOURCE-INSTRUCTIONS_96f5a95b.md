# MyMitraa TaskFlow — Complete Source Discovery, Verification and Development Instructions

## 1. Project identity

**Application name:** MyMitraa TaskFlow  
**Java package target:** `com.mymitraa.taskflow`  
**MongoDB database target:** `mymitraa_taskflow`  
**Frontend:** React + TypeScript + Tailwind CSS  
**Backend:** Existing PM-TOOL Spring Boot application, selectively adapted after approval.

The goal is to build MyMitraa TaskFlow using the specifically approved source files and documentation.

Do not rebuild or copy the entire original PM-TOOL backend.

---

## 2. Exact Windows paths

Use these paths. Do not guess locations.

### Original backend — READ ONLY

`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

This is the original PM-TOOL backend. You may inspect it and locate approved files, but must never modify or delete anything inside it.

### Downloads folder

`C:\Users\LENOVO\Downloads`

Search this folder and its subfolders for the latest MyMitraa documentation.

### Claude Code Lab workspace

`C:\Users\LENOVO\Claude-Code-Lab\workspace`

All MyMitraa project work must remain inside this workspace.

### MyMitraa project destination

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow`

### Documentation destination

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\documentation`

### Approved backend working destination

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\backend\mymitraa_taskflow`

Do not populate this backend destination until the source inventory is complete and the user explicitly approves the next stage.

---

## 3. First task: inspect Downloads

Before doing anything else, inspect:

`C:\Users\LENOVO\Downloads`

Search recursively for the following documents:

1. `MiTRAA_TaskFlow_API_Reference_v3.docx`
2. `MiTRAA_TaskFlow_API_Reference_v3.pdf`
3. `MiTRAA_TaskFlow_Frontend_BRD_v3.docx`
4. `MiTRAA_TaskFlow_Frontend_BRD_v3.pdf`
5. `implementation_plan.md`

The actual filenames may have minor variations. Report the exact filename, full path, file size and last-modified date.

### Documentation rules

- Prefer the latest v3 documentation.
- If both DOCX and PDF versions of v3 exist, report both and identify the latest versions.
- Do not silently use v2 when v3 is unavailable.
- If the latest v3 API Reference or Frontend BRD is missing, list it in the missing-source report and request it from the user.
- Do not invent content from a missing document.
- Do not assume that a file exists merely because it was mentioned in an earlier conversation.

### Screenshot PDFs are not required for this initial task

The following PDFs and design image are not mandatory for the initial backend inventory:

- `dropdown.pdf`
- `meeting.pdf`
- `leave.pdf`
- `status.pdf`
- `doc.pdf`
- `calendar.pdf`
- `task3.pdf`
- `task4.pdf`
- `task_management.pdf`
- `task_management_2.pdf`
- `image.jpg`

Do not stop the backend inventory because these files are missing.

If they are present, report their paths. Do not invent screenshots or claim to have inspected missing files. They may be used in a later frontend design-reference stage after the user supplies or approves them.

---

## 4. Strictly approved backend controllers

Locate only the following seven controller source files in the original backend.

1. `FeedbackHistoryController.java`
2. `GlobalFileUploadsController.java`
3. `OptimaLeaveTrackerController.java`
4. `OptimaWeeklyUpdateTrackerController.java`
5. `TaskOwnerEmailController.java`
6. `MeetingTimeTrackerController.java`
7. `DropdownManagementController.java`

For each controller, record:

- Exact full Windows path.
- Package declaration.
- Import statements.
- Class name.
- Public REST endpoints and HTTP methods.
- Request mappings.
- Request parameters and request-body types.
- Response types.
- Referenced domain classes.
- Referenced service classes.
- Referenced repository classes.
- Referenced VM/MV classes.
- Security annotations or permission checks.
- Other direct dependencies.

Do not assume that every controller has all these elements. Record only what is actually present in its source.

---

## 5. Strictly approved backend domain classes

Locate only these seven domain source files:

1. `FeedbackHistory.java`
2. `GlobalFileUploads.java`
3. `OptimaLeaveTracker.java`
4. `OptimaWeeklyUpdateTracker.java`
5. `MeetingTimeTracker.java`
6. `WorkLogEntry.java`
7. `BaseDomain.java`

For each file, record:

- Exact full Windows path.
- Package declaration.
- Fields and their actual Java types.
- MongoDB collection or document annotations.
- ID and inheritance relationships.
- Validation annotations.
- Embedded or referenced objects.
- Imports and direct dependencies.
- Relationships to approved controllers.

Do not create fields based on assumptions about the product.

---

## 6. Missing dependencies — investigate and report

Check whether the following files and dependencies actually exist in the original backend.

### Domain and model dependencies

- DropdownManagement domain class.
- TaskOwnerEmail class.
- SearchVM.
- AdvanceSearchList.
- Any directly referenced VM or MV classes.

### Service dependencies

- Service interfaces.
- Service implementation classes.
- Classes under `service`.
- Classes under `service/impl`.

### Repository dependencies

- Repository interfaces.
- MongoDB repository classes.
- Classes under `repository`.

### Security and configuration dependencies

- Security configuration.
- Authentication and authorization classes.
- JWT-related configuration.
- Application configuration.
- MongoDB configuration.
- Required framework configuration.

### Important rules

These dependencies have not automatically been approved for copying or implementation.

For every dependency:

1. Check whether it exists.
2. Record its exact full path.
3. Identify which approved controller or domain class references it.
4. Explain why it is required.
5. Mark it as `FOUND`, `MISSING`, or `REQUIRES USER APPROVAL`.

Do not invent a missing class.

Do not create a replacement class with guessed fields or methods.

If a required dependency is missing, add it to `missing-source-files-request.md` and ask the user to provide or approve the exact source file.

Do not implement the affected functionality until the required source has been provided or explicitly approved.

---

## 7. Approved source folders

The original project may contain the following folders:

- `controller`
- `domain`
- `mv`
- `vm`
- `service`
- `repository`
- `service/impl`

Only the seven approved controllers and seven approved domain classes listed above are currently approved source files.

Files under `mv`, `vm`, `service`, `repository`, and `service/impl` are dependencies to investigate, not automatically approved files.

Do not add guessed classes to these folders.

Do not copy unrelated classes just because they exist in the original backend.

---

## 8. Branding and technical migration rules

The original application contains Kellton/Optima naming and configuration.

The target application is MyMitraa TaskFlow.

Target values:

- Application name: `MyMitraa TaskFlow`
- Java package: `com.mymitraa.taskflow`
- MongoDB database: `mymitraa_taskflow`

However, do not perform a blind global replacement.

First classify each occurrence of old naming into:

1. User-facing branding.
2. Java package and import references.
3. Database and collection configuration.
4. API route names.
5. Third-party dependencies.
6. External integration references.
7. Historical comments or documentation.

Explain which references can safely change and which may break compatibility.

Do not change any source code, package names, database configuration or API routes during the initial discovery task.

---

## 9. Security and credentials

The original backend may contain database credentials, email credentials, API keys, JWT secrets or other sensitive configuration.

Rules:

- Never print actual passwords, API keys, tokens or JWT secrets in reports.
- Never copy real credentials into the new project.
- Do not reuse original secrets.
- If exposed credentials are found, report only the file path and configuration key name, not the secret value.
- Recommend rotating any previously exposed credentials.
- Use environment variables or a secure configuration approach in a later approved implementation stage.
- Never connect to or modify the original production database.
- Do not execute destructive database operations.

---

## 10. Frontend project direction

The frontend will be developed as a new React + TypeScript + Tailwind CSS application.

The frontend must be based on the actual v3 Frontend BRD and approved screenshots when available.

Do not invent screens, buttons, API routes, fields, filters, permissions or workflows.

For each documented screen, identify:

- Screen name.
- Purpose.
- Required UI elements.
- User actions.
- Data required.
- Related API endpoints.
- Missing API dependencies.
- Loading, error and empty states, only where requirements support them.

Do not start frontend implementation during this discovery stage.

---

## 11. Original source protection

This is mandatory.

- Original PM-TOOL folder is READ ONLY.
- Do not rename the original folder.
- Do not delete original files.
- Do not modify the original source code.
- Do not copy the entire backend.
- Do not modify the user's global Claude Code configuration.
- Do not modify unrelated projects.
- Do not modify ALS project files or history.
- Do not overwrite unrelated memory files.

All generated reports must be saved in the MyMitraa project documentation folder.

If the destination folders do not exist, create only the required MyMitraa workspace folders.

---

## 12. Required reports

Create the following Markdown reports:

### Report 1: `approved-source-inventory.md`

Include:

- Original backend path.
- Downloads path.
- Latest v3 documentation found.
- Seven approved controllers and their exact paths.
- Seven approved domain classes and their exact paths.
- Screenshot and design files found, if any.
- Missing approved files.
- File verification status.

### Report 2: `controller-domain-service-mapping.md`

Create a mapping of:

- Approved controller.
- Referenced domain classes.
- Referenced service classes.
- Referenced repositories.
- Referenced VM/MV classes.
- Security/configuration dependencies.
- Missing dependencies.

Use only source-verified relationships.

### Report 3: `api-reference-mapping.md`

Compare the actual controller endpoints against the latest v3 API Reference.

For each endpoint, record:

- Endpoint and HTTP method.
- Controller source.
- Request parameters/body.
- Response type.
- API documentation match.
- Missing or conflicting information.

Clearly distinguish actual source code from documentation.

### Report 4: `missing-source-files-request.md`

List every missing or unapproved dependency.

For each item, provide:

- Filename/class name.
- Expected source folder, if verified.
- Referencing controller or class.
- Why it is needed.
- Exact file requested from the user.
- Whether development must stop until it is supplied.

### Report 5: `frontend-screen-reference-mapping.md`

Use the latest v3 Frontend BRD if available.

Map only documented screens and functions to their corresponding API endpoints.

If the BRD is missing, state that the screen mapping is blocked. Do not invent a replacement specification.

### Report 6: `mymitraa-implementation-roadmap.md`

Prepare a phased roadmap based only on verified requirements and available sources.

Separate:

- Confirmed requirements.
- Source-verified functionality.
- Missing dependencies.
- Questions requiring user decisions.
- Work that cannot start yet.

Do not begin implementation.

---

## 13. Memory update

Update:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\memory\work_state.md`

Record the MyMitraa TaskFlow discovery status, exact paths, verified files, missing files and next steps.

Do not delete or overwrite unrelated project history.

Preserve existing ALS project information and any other project records.

If the memory file does not exist, check the workspace structure before creating a new one. Do not overwrite an existing memory index or unrelated notes.

---

## 14. Execution order

Follow this order strictly:

**Step 1:** Verify the original backend directory exists.

**Step 2:** Search Downloads recursively for the latest v3 API Reference, Frontend BRD and implementation plan.

**Step 3:** Locate the seven approved controller files.

**Step 4:** Locate the seven approved domain files.

**Step 5:** Inspect their actual source contents.

**Step 6:** Trace their direct dependencies and check whether the missing dependency classes exist.

**Step 7:** Compare actual controller endpoints with the v3 API Reference, if available.

**Step 8:** Map documented frontend screens to the API Reference, if available.

**Step 9:** Generate the six required reports.

**Step 10:** Update the MyMitraa work-state record without disturbing other projects.

**Step 11:** Present a concise summary of findings and wait for user approval.

---

## 15. Mandatory STOP conditions

Do not proceed beyond discovery and reporting.

Stop and request user approval before:

- Copying any Java source files.
- Creating the backend working copy.
- Renaming Java packages.
- Renaming classes.
- Changing application configuration.
- Changing MongoDB database settings.
- Creating new Java classes.
- Creating service or repository implementations.
- Creating authentication or security implementations.
- Building frontend screens.
- Installing dependencies.
- Connecting to any database.
- Changing API contracts.

If a required file or document is missing, report it instead of guessing.

If an action requires access that the current environment does not provide, report the limitation. Do not claim the action succeeded.

---

## 16. Final response format

After completing the discovery task, report:

1. Whether the original backend path exists.
2. Whether the latest v3 API Reference was found.
3. Whether the latest v3 Frontend BRD was found.
4. How many of the seven approved controllers were found.
5. How many of the seven approved domain classes were found.
6. Which dependencies are missing.
7. Which reports were created and their exact paths.
8. Whether any files were copied or modified.
9. What files or decisions are needed from the user next.

**Final instruction: This is a source discovery and verification task only. Do not start implementation. Preserve the original PM-TOOL backend and all unrelated projects.**