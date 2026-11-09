# MyMitraa TaskFlow — Rollback & Recovery Guide

This document defines the emergency rollback protocols for application releases, database state restoration, and disaster recovery procedures.

---

## 1. Rollback Triggers & Thresholds

Initiate a rollback immediately if any of the following conditions occur after a deployment:
- Health check `GET /api/health` fails for more than 2 minutes.
- HTTP 500 error rate exceeds 2% across `/api/**` traffic.
- Critical authentication or task mutation operations fail.
- Database connectivity or document corruption errors appear in logs.

---

## 2. Fast Application Rollback (< 2 Minutes)

Each release keeps the previous stable build cached on the VPS:
- Frontend: `/var/www/taskflow/dist_previous`
- Backend: `/opt/taskflow/app_previous.jar`

### Rollback Steps:

```bash
# 1. SSH into VPS
ssh deploy@201.18.214.36

# 2. Roll back backend JAR
sudo systemctl stop taskflow-backend
sudo cp /opt/taskflow/app_previous.jar /opt/taskflow/app.jar
sudo chown taskflow:taskflow /opt/taskflow/app.jar
sudo systemctl start taskflow-backend

# 3. Roll back frontend static bundle
sudo rm -rf /var/www/taskflow/dist
sudo cp -r /var/www/taskflow/dist_previous /var/www/taskflow/dist
sudo chown -R www-data:www-data /var/www/taskflow/dist
sudo systemctl reload nginx

# 4. Verify restored service
curl -s https://task.mitratechgroup.com/api/health
```

---

## 3. Database Rollback & Recovery

If a failed deployment executed schema changes or corrupted MongoDB documents:

```bash
# 1. Stop backend service to prevent data mutation
sudo systemctl stop taskflow-backend

# 2. Identify pre-deployment backup archive
LATEST_BACKUP=$(ls -t /var/backups/taskflow/pre_deploy_*.archive | head -n 1)
echo "Restoring from: ${LATEST_BACKUP}"

# 3. Execute mongorestore with --drop
mongorestore \
  --uri="mongodb://taskflow_app_user:PASSWORD@127.0.0.1:27017/mymitraa_taskflow?authSource=mymitraa_taskflow" \
  --drop \
  --archive="${LATEST_BACKUP}" \
  --gzip

# 4. Restart backend service
sudo systemctl start taskflow-backend
sudo systemctl status taskflow-backend
```

---

## 4. Git Branch Emergency Recovery

If unwanted commits or bad merges occurred on the working branch `production-readiness`:

1. Identify the last verified stable commit hash (e.g. `46a3b81`).
2. Locally check out the branch and inspect:
   ```powershell
   git status
   git log --oneline -n 10
   ```
3. Revert erroneous commits cleanly without force-pushing:
   ```powershell
   git revert <bad_commit_hash>
   ```
4. Run full local test suite:
   ```powershell
   npm test
   mvn test -Dspring.profiles.active=test
   ```
5. Obtain explicit administrator authorization before pushing any recovery commit to `origin/production-readiness`.

---

## 5. Incident Post-Mortem Template

Following any production rollback, document:
1. **Incident Summary**: Date, time, impacted endpoints, duration of outage.
2. **Root Cause Analysis**: Code regression, environment configuration mismatch, or network issue.
3. **Corrective Actions Taken**: Rollback timing, database restoration details.
4. **Preventive Measures**: Additional unit/integration tests added to prevent recurrence.
