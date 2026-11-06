# MyMitraa TaskFlow — Backend Migration Execution

## Objective

Migrate the existing PM-TOOL backend into the isolated MyMitraa TaskFlow backend, adapting the code to the new application structure.

This is an authorized migration phase.

### Source
`C:\Users\LENOVO\Downloads\PM-TOOL\PM-TOOL\pep_optima_well_service`

### Destination
`C:\Users\LENOVO\Claude-Code-Lab\workspace\projects\mymitraa-taskflow\backend\mymitraa_taskflow`

### Target identity

- Application: MyMitraa TaskFlow
- Java package: `com.mymitraa.taskflow`
- MongoDB database: `mymitraa_taskflow`
- Development environment: localhost only

## 1. Source protection

- Treat the original PM-TOOL backend as strictly read-only.
- Never modify, rename, move or delete original source files.
- Never copy old production credentials.
- Do not modify unrelated projects or global configurations.
- Never weaken Claude Code sandbox restrictions.
- If the approved transfer method is blocked, stop and report the exact restriction.

## 2. Inventory before copying

Inspect the source project and existing migration documentation.

Verify:
- All seven approved controllers.
- All seven approved domain classes.
- Their actual service interfaces and implementations.
- Repository dependencies.
- DTOs, VMs and request/response models.
- Security and authentication classes.
- Database configuration.
- Maven build files and dependencies.
- Application configuration and resources.
- Exception handling and validation.
- File upload and storage components.
- Email components.
- Logging and infrastructure.
- Required tests.

Generate a distinct file-level manifest with source paths, destination paths, purpose, dependencies and whether adaptation is required.

Resolve inconsistent endpoint and component counts in the existing reports by inspecting the actual source. Do not rely on previous summary numbers.

## 3. New backend structure

Use the target package:

`com.mymitraa.taskflow`

Organize code into logical packages where compatible with the existing architecture:

- config
- security
- authentication
- controller
- domain
- repository
- service
- service.impl
- dto
- vm
- exception
- validation
- util
- storage
- email

Use appropriate subpackages where needed. Do not create empty placeholder classes merely to fill this structure.

Update package declarations and imports consistently. Update Maven group/artifact identity and application branding where appropriate.

Preserve required business behavior and API contracts unless an intentional, documented adaptation is necessary.

## 4. Local development configuration

Configure localhost development only.

- MongoDB host: `localhost`
- MongoDB database: `mymitraa_taskflow`
- Never connect to the original production database.
- Do not reuse original database credentials or connection strings.
- Use environment variables or local ignored configuration for secrets.
- Provide an `.env.example` or equivalent template containing placeholders.
- Do not commit real secrets.

Create temporary development accounts:

Admin:
- Username: `admin`
- Password: generate a strong random development password.

User:
- Username: `demo.user`
- Password: generate a different strong random development password.

Requirements:
- Hash passwords with an appropriate password encoder.
- Never store plaintext passwords in the database.
- Assign separate ADMIN and USER roles.
- Enforce role-based authorization on protected endpoints.
- Generate a strong random local JWT signing secret.
- Do not hardcode the JWT secret in committed source.
- Store local passwords and JWT secrets outside version control.
- Document how to regenerate them.
- Clearly label the accounts as development-only.

Email:
- Disable external SMTP delivery by default.
- Use a local mock or development logging adapter where practical.
- Do not reuse old SMTP credentials.

## 5. Security and configuration migration

Inspect the old security implementation before adapting it.

Preserve necessary authentication and authorization behavior while updating it for MyMitraa.

Verify:
- Password hashing.
- Authentication.
- JWT creation and validation.
- Role checks.
- Endpoint authorization.
- CORS configuration.
- Session/stateless behavior as appropriate.
- Input validation.
- Safe error responses.
- Secret management.
- Local development profile separation.

Do not introduce production configuration or production secrets.

## 6. Database and persistence

Prepare local MongoDB configuration and required repository support.

Do not:
- Connect to production.
- Import production data.
- Run destructive migrations.
- Delete or alter existing databases.
- Automatically create or modify a database outside the local development environment.

Document the local MongoDB prerequisites and startup instructions.

## 7. Build and dependency compatibility

Inspect the original Java version, Spring Boot version, JHipster version and Maven dependencies first.

Choose a compatible migration strategy based on verified source code.

Do not perform an arbitrary framework upgrade.

Preserve required dependencies, update package references and resolve compilation errors.

Document any dependencies that require a deliberate version upgrade.

## 8. Migration execution

Use a controlled, manifest-driven copy and adaptation process.

- Copy only approved and verified files.
- Preserve the original source.
- Never overwrite unrelated destination files.
- Do not use broad recursive copy operations without a reviewed manifest.
- Verify source and destination file counts and hashes where files are copied unchanged.
- Record intentional modifications separately.
- Do not silently omit required dependencies.

If sandbox permissions block a file operation, stop and report the exact operation and restriction. Do not weaken sandbox security.

## 9. Testing

After migration, run appropriate local build and tests.

Verify:
- Maven compilation.
- Package consistency.
- Dependency resolution.
- Authentication and authorization.
- Admin and User login.
- JWT validation.
- Controller mappings.
- Repository wiring.
- Configuration loading.
- Validation and exception handling.
- File upload behavior where supported.
- Email mock behavior.

Do not connect to production services or databases.

Do not claim a test passed unless it actually ran successfully.

## 10. Required documentation

Create or update:

1. `documentation/backend-migration-report.md`
2. `documentation/backend-file-manifest.md`
3. `documentation/backend-architecture.md`
4. `documentation/local-development-setup.md`
5. `documentation/security-configuration.md`
6. `documentation/migration-issues.md`

Document:
- Files migrated.
- Files adapted.
- Files excluded and reasons.
- Package changes.
- API compatibility.
- Security implementation.
- Local development credentials setup procedure.
- Required local services.
- Build and test results.
- Outstanding issues.

## 11. Completion conditions

The migration is complete only when:
- The new package structure is consistent.
- Required backend functionality is migrated.
- Security and local configuration are implemented.
- No original production credentials are present.
- No production database connection is configured.
- The build and reported tests have actual results.
- The original source remains unchanged.

Stop if any required source dependency is missing or any operation would violate source protection.

Proceed in small, verifiable stages. Report the completed stage and its evidence before continuing to the next stage.