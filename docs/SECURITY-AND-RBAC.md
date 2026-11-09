# MyMitraa TaskFlow — Security Architecture & RBAC Reference

This document outlines the security policies, Role-Based Access Control (RBAC) matrices, password hashing, and attack mitigation controls implemented in MyMitraa TaskFlow.

---

## 1. Security Architecture Summary

- **Filter Chain**: Spring Security 6 configured via `WebSecurityConfiguration.java`.
- **Session Policy**: `SessionCreationPolicy.STATELESS` — zero HTTP session state stored on the server.
- **Authentication**: JWT validation filter (`JwtAuthenticationFilter`) on every non-public request.
- **Password Storage**: `BCryptPasswordEncoder` with strength factor 12.
- **CSRF**: Disabled for stateless REST API with Bearer token authentication, eliminating CSRF vulnerabilities.
- **CORS**: Strict origin whitelist configured via `CORS_ALLOWED_ORIGINS` (defaults to `https://task.mitratechgroup.com` in production).

---

## 2. Role-Based Access Control (RBAC) Hierarchy

The system defines 4 primary authorization roles:

```
+-----------------------------------------------------------+
|                      SUPER_ADMIN                          |
|  - System-wide configuration                              |
|  - Role creation & security policy tuning                 |
+-----------------------------+-----------------------------+
                              |
+-----------------------------v-----------------------------+
|                         ADMIN                             |
|  - User management (create, update, deactivate)           |
|  - Audit log inspection (/api/audit)                      |
|  - SMTP testing & configuration (/api/email)              |
|  - Project creation, archiving, deletion                  |
+-----------------------------+-----------------------------+
                              |
+-----------------------------v-----------------------------+
|                        MANAGER                            |
|  - Task assignment and status transitions                 |
|  - Leave request approvals (/api/leaveRequest)            |
|  - Meeting scheduling & team oversight                    |
+-----------------------------+-----------------------------+
                              |
+-----------------------------v-----------------------------+
|                         USER                              |
|  - View assigned tasks and projects                       |
|  - Submit leave requests                                  |
|  - File bug reports and log activity                      |
+-----------------------------------------------------------+
```

---

## 3. Endpoint Authorization Matrix

| Endpoint Route Pattern | HTTP Methods | Required Role | Unauthenticated Response |
|---|---|---|---|
| `/api/auth/login`, `/api/auth/register` | `POST` | Public (Permit All) | N/A |
| `/api/health` | `GET` | Public (Permit All) | N/A |
| `/api/auth/current`, `/api/auth/logout` | `GET`, `POST` | Authenticated (Any Role) | 401 Unauthorized |
| `/api/tasks/**` | `GET` | Authenticated | 401 Unauthorized |
| `/api/tasks/**` | `POST`, `PUT` | `USER`, `MANAGER`, `ADMIN` | 403 Forbidden |
| `/api/tasks/**` | `DELETE` | `MANAGER`, `ADMIN` | 403 Forbidden |
| `/api/projects/**` | `GET` | Authenticated | 401 Unauthorized |
| `/api/projects/**` | `POST`, `PUT`, `DELETE` | `ADMIN` | 403 Forbidden |
| `/api/users/**` | `GET` | Authenticated | 401 Unauthorized |
| `/api/users/**` | `POST`, `PUT`, `DELETE` | `ADMIN` | 403 Forbidden |
| `/api/audit/**` | `GET` | `ADMIN`, `SUPER_ADMIN` | 403 Forbidden |
| `/api/email/test` | `POST` | `ADMIN` | 403 Forbidden |
| `/api/leaveRequest/{id}/status` | `PUT` | `MANAGER`, `ADMIN` | 403 Forbidden |
| `/api/document/upload` | `POST` | Authenticated | 401 Unauthorized |
| `/api/document/download/**` | `GET` | Authenticated | 401 Unauthorized |

---

## 4. Hardening & Attack Mitigations

### 4.1 Path Traversal Prevention
File uploads in `DocumentController.java` sanitize filenames before writing to disk:
- Rejects paths containing `..`, `/`, `\`, or null bytes.
- Resolves normalized paths strictly within `FILE_UPLOAD_DIR`.
- Random UUID prefixed to filenames prevents file overwrites and collision attacks.

### 4.2 Credential Protection
- Passwords are never logged in application logs.
- Sensitive environment variables (`JWT_SECRET`, `ZOHO_SMTP_PASSWORD`, `MONGODB_URI`) are masked in logs and excluded from all API responses.
- The SMTP configuration API (`GET /api/email/config`) returns `hasPassword: true` without exposing the actual password string.

### 4.3 Brute-Force & Rate Limiting Strategy
In production, Nginx enforces rate limits on the authentication endpoints:
```nginx
limit_req_zone $binary_remote_addr zone=auth_limit:10m rate=5r/m;

location /api/auth/login {
    limit_req zone=auth_limit burst=5 nodelay;
    proxy_pass http://127.0.0.1:8080;
}
```

### 4.4 Automated Regression Tests
Automated Spring Security tests (`RoleSecurityTest.java`, `AuthenticationTest.java`, `RegistrationSecurityTest.java`) verify:
- Unauthenticated requests to protected endpoints return HTTP 401.
- Regular `USER` access to `/api/audit` returns HTTP 403.
- Normal users cannot escalate privileges or approve their own leave.
