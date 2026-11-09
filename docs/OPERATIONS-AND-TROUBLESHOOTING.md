# MyMitraa TaskFlow — Operations & Troubleshooting Guide

This guide covers routine operational maintenance, log aggregation, health monitoring, and systematic troubleshooting for MyMitraa TaskFlow in production.

---

## 1. Service Management Commands

Manage the Spring Boot application using systemd:

```bash
# Check service status
sudo systemctl status taskflow-backend

# Start, stop, or restart the service
sudo systemctl start taskflow-backend
sudo systemctl stop taskflow-backend
sudo systemctl restart taskflow-backend

# Reload configuration without restarting
sudo systemctl reload-or-restart taskflow-backend

# Check Nginx status
sudo systemctl status nginx
sudo nginx -t
sudo systemctl reload nginx
```

---

## 2. Log Management & Inspection

All logs are written to `/var/log/taskflow/`:

```bash
# Follow live application stdout logs
tail -f /var/log/taskflow/app.log

# Follow live error logs
tail -f /var/log/taskflow/error.log

# Search for critical exceptions
grep -n -C 5 "ERROR" /var/log/taskflow/app.log

# Inspect Nginx access and error logs
tail -f /var/log/nginx/error.log
tail -f /var/log/nginx/access.log
```

### Logrotate Configuration (`/etc/logrotate.d/taskflow`)
To prevent disk exhaustion:
```
/var/log/taskflow/*.log {
    daily
    missingok
    rotate 14
    compress
    delaycompress
    notifempty
    create 0640 taskflow taskflow
    postrotate
        systemctl kill -s HUP --kill-who=main taskflow-backend.service > /dev/null 2>&1 || true
    endscript
}
```

---

## 3. Health & Liveness Checks

### Automated Uptime Monitoring
Configure an external uptime monitor (e.g. UptimeRobot or Better Uptime) targeting:
```
GET https://task.mitratechgroup.com/api/health
```
- **Interval**: 60 seconds
- **Expected Status**: `200 OK`
- **Expected Payload Content**: `"status":"UP"`

---

## 4. Troubleshooting Playbook

### Issue 1: Backend Fails to Start (`taskflow-backend.service failed`)
- **Symptoms**: `systemctl status taskflow-backend` shows exit code 1.
- **Diagnostic Steps**:
  ```bash
  journalctl -u taskflow-backend -n 50 --no-pager
  tail -n 100 /var/log/taskflow/error.log
  ```
- **Common Causes**:
  - *Missing `JWT_SECRET`*: The production profile requires `JWT_SECRET`. Ensure `/etc/taskflow/taskflow.env` defines `JWT_SECRET`.
  - *MongoDB Connection Refused*: Check if MongoDB is running (`systemctl status mongod`). Verify credentials in `MONGODB_URI`.
  - *Port 8080 Conflict*: Check if another process is using port 8080: `sudo netstat -tlpn | grep :8080`.

### Issue 2: Frontend Shows 502 Bad Gateway
- **Symptoms**: Nginx returns 502 on `/api/*` requests.
- **Diagnostic Steps**:
  ```bash
  curl -i http://127.0.0.1:8080/api/health
  ```
- **Resolution**: If curl fails, Spring Boot is not running on 8080. Start or restart `taskflow-backend`. If curl succeeds, verify Nginx `proxy_pass` URL and timeout settings.

### Issue 3: JWT Tokens Expire Immediately or 401 on Every Request
- **Symptoms**: User logs in, but immediately gets redirected back to login screen.
- **Diagnostic Steps**:
  1. Inspect browser DevTools Console and Network tabs.
  2. Verify that `sessionStorage` has `mitraa_jwt_token`.
  3. Check system clock on VPS: `timedatectl`. A severe clock skew (>5 min) causes JWTs to appear expired on receipt.
  4. Run `sudo systemctl restart systemd-timesyncd` to synchronize NTP.

### Issue 4: Email Delivery Fails (HTTP 500 / 503)
- **Symptoms**: Test email returns "Email service is unconfigured" or "Authentication failed".
- **Diagnostic Steps**:
  1. Verify `/etc/taskflow/taskflow.env` has `ZOHO_SMTP_USER` and `ZOHO_SMTP_PASSWORD`.
  2. Verify Zoho SMTPS connectivity:
     ```bash
     openssl s_client -connect smtppro.zoho.in:465 -crlf
     ```
  3. Ensure you are using a 16-character Zoho App Password, not the account master password.
