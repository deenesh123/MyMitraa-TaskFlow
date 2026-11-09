# MyMitraa TaskFlow — Architecture Documentation

## 1. System Overview

MyMitraa TaskFlow (`com.mymitraa.taskflow`) is an enterprise task, project, lead, meeting, leave, and issue management platform. It features a modern Single-Page Application (SPA) frontend and a secure Spring Boot 3.3.4 backend backed strictly by MongoDB document persistence.

```
+-----------------------------------------------------------------------------------+
|                                 CLIENT TIER                                       |
|  React 19 + TypeScript + Vite + Tailwind CSS v4                                   |
|  - Auth State in sessionStorage (mitraa_jwt_token)                                |
|  - API Client with Single Bearer prefix & HTTP 401 Interceptor                    |
|  - Real-time Dashboard, Leads, Tasks, Projects, Leave, Bugs, Email Management     |
+-----------------------------------------+-----------------------------------------+
                                          |
                                          | HTTPS / REST (JSON)
                                          v
+-----------------------------------------------------------------------------------+
|                        PRODUCTION GATEWAY & REVERSE PROXY                         |
|  Hostinger VPS (Ubuntu 24.04) — 201.18.214.36                                     |
|  Public Domain: https://task.mitratechgroup.com                                   |
|  Nginx 1.24+:                                                                     |
|    /          --> Static Pre-built Vite Bundle (/var/www/taskflow/dist)           |
|    /api/      --> Reverse Proxy to 127.0.0.1:8080 (Spring Boot)                   |
+-----------------------------------------+-----------------------------------------+
                                          |
                                          | Localhost HTTP / loopback only
                                          v
+-----------------------------------------------------------------------------------+
|                               APPLICATION TIER                                    |
|  Spring Boot 3.3.4 (Java 21 / 17 runtime)                                         |
|  - Spring Security with Stateless JWT Filter                                      |
|  - 20 REST Controllers mapped under /api/**                                       |
|  - TaskFlowAuditUtil & AuditLog Logging Service                                   |
|  - Zoho Mail Integration via JavaMailSender (SMTPS :465)                          |
|  - Local Dev Server: Express + Vite middleware (server.ts, strictly for dev)      |
+-----------------------------------------+-----------------------------------------+
                                          |
                                          | Internal Loopback (127.0.0.1:27017)
                                          v
+-----------------------------------------------------------------------------------+
|                                PERSISTENCE TIER                                   |
|  MongoDB (Document Store ONLY — No SQL / JPA / Relational DBs)                     |
|  - Development Database: mymitraa_taskflow_dev                                    |
|  - Production Database:  mymitraa_taskflow                                        |
|  - Isolated VPS MongoDB instance, loopback-bound, authenticated user               |
+-----------------------------------------------------------------------------------+
```

---

## 2. Component Architecture

### 2.1 Frontend Tier
- **Framework**: React 19 SPA bundled with Vite 6.
- **Language**: TypeScript 5.7 (strict typing, zero `any` leaks).
- **Styling**: Tailwind CSS v4 with modern CSS variables.
- **State & Session**:
  - Authenticated JWT stored in `sessionStorage` under `mitraa_jwt_token`.
  - Current user session verified at application startup via `GET /api/auth/current`.
  - HTTP 401 responses trigger automatic session termination, storage cleanup, and redirection to `/auth`.
  - Zero static credentials, zero placeholder tokens, zero simulated mock fallbacks.

### 2.2 Backend Tier
- **Framework**: Spring Boot 3.3.4.
- **Java Platform**: Java 21 LTS (compiles on Java 17+).
- **Security**:
  - Spring Security 6 filter chain with `SessionCreationPolicy.STATELESS`.
  - `JwtAuthenticationFilter` verifying `Authorization: Bearer <token>` header.
  - BCrypt password hashing for credentials.
  - Method-level and path-level RBAC (`ADMIN`, `USER`, `MANAGER`, `SUPER_ADMIN`).
- **Data Persistence**:
  - Spring Data MongoDB (`MongoRepository`).
  - No SQL, JPA, Hibernate, or relational engines.
  - Strict profile segregation (`dev`, `prod`, `test`).

### 2.3 Email & External Services Tier
- **SMTP Provider**: Zoho Mail (`smtppro.zoho.in` / `smtp.zoho.com`).
- **Transport**: SMTPS over TLS/SSL on port 465.
- **Execution**: `JavaMailSender` with `MimeMessageHelper`.
- **Truthful Status**: Real audit entries written to MongoDB `audit_logs` collection. Failures reported honestly to callers (no fake simulation).

---

## 3. Data Flow

### 3.1 Authentication Lifecycle
1. User enters username and password in `AuthPage.tsx`.
2. Client sends `POST /api/auth/login` with `{ username, password }`.
3. Spring Boot validates credentials against MongoDB `users` collection via `AuthenticationManager`.
4. Spring Boot generates HMAC-SHA256 JWT signed with `JWT_SECRET`, expiring in exactly 86,400,000 ms (24 hours).
5. Frontend extracts token from `metaInfo.token` and persists it in `sessionStorage`.
6. Subsequent requests automatically attach `Authorization: Bearer <token>` (exactly one Bearer prefix).
7. On logout, client calls `POST /api/auth/logout`, purges `sessionStorage`, and redirects to `/auth`.

### 3.2 Standard CRUD API Request Flow
1. Client makes authenticated fetch to `/api/{resource}`.
2. Nginx matches `/api/` prefix, strips or preserves path as configured, and proxies to `127.0.0.1:8080`.
3. `JwtAuthenticationFilter` intercepts request, extracts claims, and populates `SecurityContextHolder`.
4. Controller receives typed DTO, invokes service layer.
5. Service validates business constraints and queries MongoDB via `MongoRepository`.
6. Audit log entry is generated using `TaskFlowAuditUtil` for mutations.
7. Standardized `CustomResponse<T>` returned with status, message, and payload.

---

## 4. Key Architectural Guarantees
- **Strict Database Boundary**: MongoDB is the sole persistence technology. No SQL/ORM dependencies exist.
- **Fail-Safe Mock Isolation**: In production, API errors surface truthful error states (e.g. 503 Service Unavailable). In-memory mock data is never served.
- **Credential Protection**: Passwords, tokens, and SMTP credentials never appear in client bundles, log files, or API responses.
- **VPS Co-existence**: Production MongoDB and Spring Boot run on loopback addresses only, completely isolated from other applications hosted on the VPS (such as DART).
