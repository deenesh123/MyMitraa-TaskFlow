# MyMitraa TaskFlow — Zoho Mail SMTP Security & Configuration Audit

## 1. Security Architecture & Boundary Verification

The Zoho Mail integration is designed with strict server-side boundary enforcement:

```
[ React Client Browser ]
         │ (HTTP REST: /api/email/*)
         │ [ZERO SMTP CREDENTIALS IN BROWSER / LOCALSTORAGE]
         ▼
[ Backend Server (Express dev / Spring Boot prod) ]
         │
         ├── Credentials injected via ENV: ZOHO_SMTP_USER, ZOHO_SMTP_PASSWORD
         ├── Passwords stripped before returning status/config to client
         ├── Sanitized audit logging (passwords never written to logs)
         │
         ▼ (Encrypted TLS / SSL on Port 465 or 587)
[ Zoho Mail SMTP Gateway (smtp.zoho.com) ]
```

---

## 2. Credential Protection Verification

1. **Client-Side Isolation**:
   - The React frontend does NOT store or access SMTP credentials.
   - The settings endpoint `/api/email/config` returns:
     ```json
     {
       "host": "smtp.zoho.com",
       "port": 465,
       "secure": true,
       "user": "notifications@mymitraa.com",
       "from": "\"MyMitraa TaskFlow\" <notifications@mymitraa.com>",
       "isConfigured": true,
       "hasPassword": true
     }
     ```
     The plain-text password is **never returned** to the client browser.

2. **Source Code Cleanliness**:
   - All source code and repositories were audited for hardcoded credentials.
   - Any previously hardcoded test recipient email addresses were eliminated; the test email panel in `EmailZohoManagement.tsx` requires an explicit user-entered email address before sending.
   - `.env.example` provides sanitized template variables without real secrets:
     ```bash
     ZOHO_SMTP_HOST=smtp.zoho.com
     ZOHO_SMTP_PORT=465
     ZOHO_SMTP_USER=
     ZOHO_SMTP_PASSWORD=
     ZOHO_FROM_NAME=MyMitraa TaskFlow
     ZOHO_FROM_EMAIL=notifications@mymitraa.com
     ```

---

## 3. Port Configuration & Transport Protocols

Zoho Mail supports two secure connection standards:

| Port | Protocol | Encryption | Settings | Recommendation |
|---|---|---|---|---|
| **465** | SMTPS | Implicit SSL/TLS | `secure: true`, SSL enabled | **Recommended for production** (standard for transactional email) |
| **587** | SMTP | Explicit STARTTLS | `secure: false`, STARTTLS enabled | Alternative for networks where port 465 is blocked |

Both the Node.js Express service (`server/emailService.ts`) and the Java Spring Boot service (`ZohoMailConfig.java` / `application.yml`) dynamically select the appropriate protocol based on the configured port.

---

## 4. Audit Logging & Abuse Prevention

1. **Delivery Activity Logging**:
   - Every email dispatch attempt (Task Assignment, Weekly Status, Leave Update, or Test Email) records an audit entry containing:
     - `id`: Unique timestamped log ID
     - `timestamp`: ISO-8601 UTC timestamp
     - `to`: Target recipient email address
     - `subject`: Email subject
     - `category`: Category (`TASK_OWNER`, `WEEKLY_REPORT`, `LEAVE_STATUS`, `TEST`, `SYSTEM`)
     - `status`: `SENT`, `SIMULATED`, or `FAILED`
     - `details`: Transport metadata (gateway host, port, messageId)
     - `error`: Error message if delivery failed (credentials stripped)

2. **Rate Limiting & Validation**:
   - Test email sending requires a valid recipient address (`validator.isEmail()`).
   - Empty recipients reject with HTTP 400 Bad Request before attempting SMTP socket connections.

---

## 5. Production Best Practices for Zoho Mail

1. **Zoho Application-Specific Passwords**:
   - For accounts with Two-Factor Authentication (2FA/MFA) enabled, standard account passwords will not work with SMTP.
   - Generate an **Application-Specific Password** in the Zoho Accounts portal (`Security` → `App Passwords` → `Generate New Password`).
2. **DNS Authentication (SPF, DKIM, DMARC)**:
   - Ensure your organization domain has valid Zoho SPF records (`v=spf1 include:zoho.com ~all`).
   - Configure Zoho DKIM keys to prevent emails from being marked as spam.
3. **Environment Variable Injection**:
   - Always inject `ZOHO_SMTP_PASSWORD` via runtime secrets manager (e.g. Kubernetes Secrets, AWS Secrets Manager, or Google Secret Manager). Never commit `.env` files with live credentials to Git.
