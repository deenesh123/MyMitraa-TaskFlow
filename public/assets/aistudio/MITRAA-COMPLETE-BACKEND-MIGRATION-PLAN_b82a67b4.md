# MyMitraa TaskFlow — Complete Backend Migration Planning

We are changing the implementation strategy.

The goal is to migrate a complete, functional backend from the existing PM-TOOL source into an isolated MyMitraa TaskFlow backend.

The 14 previously approved files are the initial business-logic scope. They are NOT the complete list of files required for a working backend.

## 1. Source and destination

Original source — READ ONLY:

`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

Isolated destination:

`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\backend\mymitraa_taskflow`

Target Java package:

`com.mymitraa.taskflow`

Application name:

`MyMitraa TaskFlow`

Target database:

`mymitraa_taskflow`

## 2. Required backend components

Inspect and plan the migration of all required components:

1. Seven approved controllers.
2. Seven approved domain classes.
3. Required service interfaces.
4. Required service implementations.
5. Required repositories.
6. Required DTOs, VMs and MVs.
7. Required security configuration.
8. Authentication and authorization.
9. User and role management dependencies.
10. Spring Boot configuration.
11. Maven pom.xml and dependencies.
12. MongoDB configuration.
13. Email and SMTP integration.
14. File upload and storage configuration.
15. Exception handling.
16. Validation.
17. Utility classes.
18. Required application resources.
19. Required logging configuration.
20. Other directly required infrastructure.

Do not assume that every existing PM-TOOL module must be migrated. Determine what is necessary for the approved functionality.

## 3. Security migration

Inspect the original authentication and authorization implementation.

Identify:

- Authentication mechanism.
- Authorization and role checks.
- Spring Security configuration.
- User and role domain classes.
- Password encoding.
- JWT or session configuration.
- Token validation and expiration.
- CORS and CSRF settings.
- Protected and public routes.
- Authentication-related filters and handlers.

Preserve required security behavior, but adapt it safely for MyMitraa.

Never copy old production secrets or credentials.

Do not disable authentication or authorization to make the application compile.

Do not invent an authentication implementation without first documenting the original behavior.

## 4. Configuration migration

Inspect:

- application configuration files.
- Maven configuration.
- Spring profiles.
- MongoDB settings.
- SMTP settings.
- File storage settings.
- Logging settings.
- Server configuration.
- Environment variables.
- Required external integrations.

Create a migration plan using environment variables for sensitive values.

The target database must be separate:

`mymitraa_taskflow`

Do not connect to or modify the original production database.

## 5. Package migration

Identify every occurrence of:

`com.kelltontech.optima`

Plan migration to:

`com.mymitraa.taskflow`

Inspect Java imports, component scanning, configuration references, resource paths, Maven settings and other package-dependent references.

Do not perform the rename yet.

## 6. Compatibility

The original project reportedly uses Java 8 and Spring Boot 1.5.9.

Inspect the actual pom.xml and source compatibility.

Identify deprecated APIs, incompatible dependencies and security concerns.

Do not upgrade the framework automatically.

Recommend a compatible migration strategy and explain risks before changing versions.

## 7. Complete dependency manifest

Create a file:

`projects/mymitraa-taskflow/documentation/complete-backend-migration-manifest.md`

For each required file, include:

- Exact source path.
- Proposed destination path.
- File category.
- Purpose.
- Which approved controller or feature requires it.
- Whether it is essential.
- Whether it contains environment-specific configuration.
- Whether it needs adaptation.
- Migration status.

Classify each file:

- REQUIRED — BUSINESS LOGIC
- REQUIRED — SECURITY
- REQUIRED — CONFIGURATION
- REQUIRED — DATABASE
- REQUIRED — BUILD
- REQUIRED — INFRASTRUCTURE
- OPTIONAL
- NOT REQUIRED
- REQUIRES USER DECISION

Include exact totals by category.

## 8. Migration boundaries

The objective is a functional MyMitraa backend for the approved modules, not a blind clone of all 250+ original Java files.

Include additional files when they are required for security, configuration, compilation or runtime functionality.

Explain why every additional file is necessary.

Keep unrelated legacy features out unless required by an approved feature.

## 9. Current execution restriction

This task is planning and inventory only.

Do not:

- Copy files.
- Modify original source files.
- Rename packages.
- Change configuration.
- Modify database settings.
- Start the backend.
- Modify frontend files.
- Change global Claude settings.
- Expose credentials.

The original PM-TOOL source remains READ ONLY.

## 10. Final deliverable

Produce:

`complete-backend-migration-manifest.md`

Also provide:

- Security migration report.
- Configuration migration report.
- Maven compatibility report.
- Package migration impact report.
- Missing dependency list.
- Recommended migration sequence.

Finish with a clear assessment of whether the complete backend scope is sufficiently understood to authorize a controlled copy.

STOP and wait for explicit user approval before copying or modifying any source files.