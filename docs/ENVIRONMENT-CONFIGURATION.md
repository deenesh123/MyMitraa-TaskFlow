# MyMitraa TaskFlow — Environment Configuration Guide

This document lists all environment variables required by MyMitraa TaskFlow across local development (`dev`), automated testing (`test`), and production (`prod`).

---

## 1. Environment Variable Reference Table

| Variable Name | Required in Prod? | Default (Dev) | Description |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Yes | `dev` | Active Spring profile (`dev`, `prod`, `test`). |
| `SERVER_PORT` | No | `8080` | Spring Boot HTTP listening port (internal only). |
| `MONGODB_URI` | Yes | `mongodb://127.0.0.1:27017/mymitraa_taskflow_dev` | MongoDB connection URI with credentials and database name. |
| `MONGODB_DATABASE` | Yes | `mymitraa_taskflow_dev` (dev) / `mymitraa_taskflow` (prod) | Name of the isolated MongoDB database. |
| `JWT_SECRET` | **MANDATORY** in prod | *Dev default* | Base64 or 256-bit secret string for HMAC-SHA256 JWT signing. |
| `JWT_EXPIRATION` | Yes | `86400000` | Token expiration time in **milliseconds** (86,400,000 ms = 24 hours). |
| `FILE_UPLOAD_DIR` | Yes | `./uploads/mymitraa_uploads` (dev) / `/var/data/mymitraa_uploads` (prod) | Root storage directory for user uploads. |
| `ZOHO_SMTP_HOST` | Yes | `smtppro.zoho.in` | Zoho Mail SMTP hostname (`smtppro.zoho.in` or `smtp.zoho.com`). |
| `ZOHO_SMTP_PORT` | Yes | `465` | SMTP port (465 for SSL/SMTPS, 587 for STARTTLS). |
| `ZOHO_SMTP_USER` | Yes | *None* | Authenticated Zoho account email address. |
| `ZOHO_SMTP_PASSWORD` | Yes | *None* | Zoho Application-Specific Password (16 characters, never account master password). |
| `ZOHO_FROM_NAME` | Yes | `MyMitraa TaskFlow` | Display name in outgoing email headers. |
| `ZOHO_FROM_EMAIL` | Yes | `notifications@mitratechgroup.com` | From email address matching Zoho verified sending domain. |
| `CORS_ALLOWED_ORIGINS` | Yes | `http://localhost:3000,http://127.0.0.1:3000` | Comma-separated list of permitted browser origins. |

---

## 2. Profile Separation Details

### 2.1 Development Profile (`dev`)
Configured in `backend/mymitraa_taskflow/src/main/resources/application-dev.yml`:
- Database target: `mymitraa_taskflow_dev`
- URI: `mongodb://${MONGODB_HOST:127.0.0.1}:${MONGODB_PORT:27017}/${MONGODB_DATABASE:mymitraa_taskflow_dev}`
- File uploads: `./uploads/mymitraa_uploads`
- Default JWT expiration: `86400000` (24h)
- Permitted origins: `http://localhost:3000`, `http://127.0.0.1:3000`

### 2.2 Production Profile (`prod`)
Configured in `backend/mymitraa_taskflow/src/main/resources/application-prod.yml`:
- Database target: `mymitraa_taskflow` (MANDATORY, never dev)
- URI: `${MONGODB_URI}` (Mandatory environment variable; fails startup if absent)
- File uploads: `${FILE_UPLOAD_DIR:/var/data/mymitraa_uploads}`
- JWT Secret: `${JWT_SECRET}` (Mandatory; application fails fast if missing)
- JWT Expiration: `${JWT_EXPIRATION:86400000}`
- Permitted origins: `https://task.mitratechgroup.com`
- Exposed ports: Bound strictly to `127.0.0.1:8080`, reverse-proxied by Nginx.

### 2.3 Test Profile (`test`)
Configured in `backend/mymitraa_taskflow/src/test/resources/application-test.yml`:
- Database target: In-memory mock or dedicated isolated test database (`mymitraa_taskflow_test`).
- Fake SMTP or mock mail sender injected during test execution.

---

## 3. Production Environment File Template (`/etc/taskflow/taskflow.env`)

Store this file with strict Linux permissions (`chmod 600 /etc/taskflow/taskflow.env`, owned by `taskflow:taskflow`):

```bash
# ==============================================================================
# MYMITRAA TASKFLOW — PRODUCTION ENVIRONMENT CONFIGURATION
# Location: /etc/taskflow/taskflow.env (DO NOT COMMIT REAL PASSWORDS TO GIT)
# ==============================================================================

# Application Profile
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Production MongoDB (Local loopback on VPS 201.18.214.36)
# Note: Uses dedicated authenticated user on approved port
MONGODB_URI=mongodb://taskflow_app_user:<SECURE_DB_PASSWORD>@127.0.0.1:27017/mymitraa_taskflow?authSource=mymitraa_taskflow
MONGODB_DATABASE=mymitraa_taskflow

# Security & JWT (Must be at least 256 bits / 32 characters)
JWT_SECRET=<GENERATE_SECURE_64_CHAR_HEX_OR_BASE64_SECRET>
JWT_EXPIRATION=86400000

# File Storage
FILE_UPLOAD_DIR=/var/data/mymitraa_uploads

# Zoho Mail SMTPS Configuration
ZOHO_SMTP_HOST=smtppro.zoho.in
ZOHO_SMTP_PORT=465
ZOHO_SMTP_USER=taskflow@mitratechgroup.com
ZOHO_SMTP_PASSWORD=<ZOHO_16_CHAR_APP_SPECIFIC_PASSWORD>
ZOHO_FROM_NAME=MyMitraa TaskFlow
ZOHO_FROM_EMAIL=taskflow@mitratechgroup.com

# CORS Allowed Origins
CORS_ALLOWED_ORIGINS=https://task.mitratechgroup.com
```

---

## 4. Verification Check

Before starting services in production:
1. Ensure `JWT_SECRET` is at least 32 characters long.
2. Confirm `JWT_EXPIRATION=86400000` is in milliseconds.
3. Validate that `MONGODB_DATABASE=mymitraa_taskflow` (not `mymitraa_taskflow_dev`).
4. Ensure no production secrets exist in `.env.example` or frontend build assets.
