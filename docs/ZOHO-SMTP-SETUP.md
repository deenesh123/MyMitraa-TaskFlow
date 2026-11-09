# MyMitraa TaskFlow — Zoho SMTP & Email Configuration Guide

This guide details the Zoho Mail configuration, SMTPS protocol settings, application password creation, backend mail sender injection, and test email verification.

---

## 1. Zoho Mail SMTPS Specifications

MyMitraa TaskFlow uses Zoho Mail's secure SMTPS protocol on port 465 (SSL/TLS).

| Parameter | Zoho Workplace (India) | Zoho Global (US / EU) |
|---|---|---|
| **SMTP Server** | `smtppro.zoho.in` | `smtp.zoho.com` |
| **Port** | `465` (SSL) or `587` (TLS) | `465` (SSL) or `587` (TLS) |
| **Authentication** | Required (Username + App Password) | Required (Username + App Password) |
| **Encryption Type** | SSL / TLS | SSL / TLS |
| **Protocol** | `smtps` | `smtps` |

---

## 2. Generating a Zoho Application-Specific Password

Zoho accounts with Two-Factor Authentication (2FA) require an **Application-Specific Password** (16 characters):

1. Log into Zoho Mail: https://mailadmin.zoho.in (or https://accounts.zoho.com).
2. Navigate to **Security** -> **App Passwords**.
3. Click **Generate New Password**.
4. Set App Name: `MyMitraa-TaskFlow-Production`.
5. Copy the generated 16-character password (e.g. `xxxx yyyy zzzz wwww`).
6. Place this password in `/etc/taskflow/taskflow.env` under `ZOHO_SMTP_PASSWORD`.
7. **Never commit this password to Git, logs, or client-side bundles.**

---

## 3. Spring Boot Backend Mail Sender Implementation

### 3.1 Bean Configuration (`application-prod.yml`)
```yaml
spring:
  mail:
    host: ${ZOHO_SMTP_HOST:smtppro.zoho.in}
    port: ${ZOHO_SMTP_PORT:465}
    username: ${ZOHO_SMTP_USER}
    password: ${ZOHO_SMTP_PASSWORD}
    protocol: smtps
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: false
            required: false
          ssl:
            enable: true
            trust: "*"
          socketFactory:
            port: 465
            class: javax.net.ssl.SSLSocketFactory
            fallback: false
```

### 3.2 Real Email Dispatch in `EmailController.java`
The backend injects `JavaMailSender` into `EmailController.java`. When `POST /api/email/test` is called:
1. Validates that the caller has `ADMIN` role.
2. Checks if `mailSender` is configured. If unconfigured, returns `HTTP 503 Service Unavailable`.
3. Validates the recipient email address with regex.
4. Constructs a `MimeMessageHelper` with `fromEmail`, `fromName`, recipient, subject, and HTML body.
5. Sends message via `mailSender.send(mimeMessage)`.
6. Records an audit entry in MongoDB `audit_logs` collection:
   ```json
   {
     "action": "EMAIL_SENT",
     "target": "recipient@example.com",
     "details": "Test email sent successfully via Zoho SMTP"
   }
   ```
7. If an `Exception` or `MailException` occurs, catches it, logs the sanitized error (without password), writes an audit failure entry, and returns `HTTP 500` with the truthful error message.
8. **No fake simulation**: The system never returns success if the message was not delivered to the SMTP relay.

---

## 4. Testing & Verification

### 4.1 CLI Verification via cURL
Test the endpoint using an authenticated admin token:

```bash
# 1. Obtain admin token
TOKEN=$(curl -s -X POST http://127.0.0.1:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"YourAdminPassword"}' | jq -r '.metaInfo.token')

# 2. Trigger test email
curl -i -X POST http://127.0.0.1:8080/api/email/test \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"recipientEmail":"admin@mitratechgroup.com","subject":"Test Email","body":"Test content"}'
```

### 4.2 Web UI Verification
1. Navigate to **Email Management** (`/email`) in the application.
2. Verify that SMTP host (`smtppro.zoho.in`), port (`465`), and sender email are displayed.
3. Password field remains masked as `••••••••••••••••`.
4. Enter your email in the **Send Test Email** form and click **Send Test Email**.
5. Check inbox for message from `MyMitraa TaskFlow`.
6. Check **Email Audit Logs** table to confirm the delivery record is stored in MongoDB.
