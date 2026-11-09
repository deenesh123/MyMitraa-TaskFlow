# MyMitraa TaskFlow — MongoDB Backup & Disaster Recovery Guide

This guide establishes the backup policies, automated cron backup scripts, offsite transfer procedures, and step-by-step restoration protocols for the `mymitraa_taskflow` database.

---

## 1. Backup Policy & Retention Schedule

| Backup Type | Frequency | Retention Window | Destination |
|---|---|---|---|
| **Hourly Snapshot** | Every 1 hour | 24 hours | Local VPS (`/var/backups/taskflow/hourly`) |
| **Daily Full** | Once daily at 02:00 UTC | 30 days | Local VPS (`/var/backups/taskflow/daily`) + Offsite |
| **Weekly Archive** | Sundays at 03:00 UTC | 12 weeks | Cold storage / Offsite bucket |

---

## 2. Automated Backup Script (`/usr/local/bin/backup-taskflow-mongo.sh`)

Install this script on the Hostinger VPS with root permissions:

```bash
#!/usr/bin/env bash
set -eo pipefail

# Configuration
BACKUP_DIR="/var/backups/taskflow/daily"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
DB_NAME="mymitraa_taskflow"
ARCHIVE_NAME="${DB_NAME}_backup_${TIMESTAMP}.tar.gz"
DUMP_DIR="/tmp/mongodump_${TIMESTAMP}"
RETENTION_DAYS=30

# Source credentials from secured environment file
source /etc/taskflow/taskflow.env

mkdir -p "${BACKUP_DIR}" "${DUMP_DIR}"

echo "[INFO] Starting mongodump for ${DB_NAME} at $(date)"

# Perform consistent point-in-time backup
mongodump \
  --uri="${MONGODB_URI}" \
  --out="${DUMP_DIR}" \
  --quiet

# Archive and compress
tar -czf "${BACKUP_DIR}/${ARCHIVE_NAME}" -C "${DUMP_DIR}/${DB_NAME}" .

# Remove temporary dump directory
rm -rf "${DUMP_DIR}"

echo "[INFO] Backup successfully written to ${BACKUP_DIR}/${ARCHIVE_NAME}"

# Purge backups older than retention window
find "${BACKUP_DIR}" -name "${DB_NAME}_backup_*.tar.gz" -type f -mtime +${RETENTION_DAYS} -delete
echo "[INFO] Cleaned archives older than ${RETENTION_DAYS} days"
```

Set executable permissions:
```bash
chmod 700 /usr/local/bin/backup-taskflow-mongo.sh
chown root:root /usr/local/bin/backup-taskflow-mongo.sh
```

### Configure System Cron
Add to root crontab (`crontab -e`):
```bash
0 2 * * * /usr/local/bin/backup-taskflow-mongo.sh >> /var/log/taskflow-backup.log 2>&1
```

---

## 3. Manual On-Demand Backup Commands

To create an instant pre-deployment backup before code updates or database migrations:

```bash
# 1. Export entire database to a compressed archive
mongodump \
  --uri="mongodb://taskflow_app_user:PASSWORD@127.0.0.1:27017/mymitraa_taskflow?authSource=mymitraa_taskflow" \
  --archive="/var/backups/taskflow/pre_deploy_$(date +%Y%m%d).archive" \
  --gzip

# 2. Verify archive size and integrity
ls -lh /var/backups/taskflow/pre_deploy_*.archive
```

---

## 4. Restoration Protocol

### 4.1 Restoring to Development Database (`mymitraa_taskflow_dev`)
To test or inspect production data locally without affecting production:

```bash
# Extract backup archive
mkdir -p /tmp/restore_taskflow
tar -xzf mymitraa_taskflow_backup_YYYYMMDD.tar.gz -C /tmp/restore_taskflow

# Restore into dev database on Windows or local host
mongorestore \
  --host="127.0.0.1:27017" \
  --db="mymitraa_taskflow_dev" \
  --drop \
  /tmp/restore_taskflow
```

### 4.2 Full Production Disaster Recovery (`mymitraa_taskflow`)
In the event of accidental data corruption or hardware recovery:

1. **Stop Spring Boot Service** to prevent writes:
   ```bash
   systemctl stop taskflow-backend
   ```
2. **Execute Full Restore**:
   ```bash
   mongorestore \
     --uri="mongodb://taskflow_app_user:PASSWORD@127.0.0.1:27017/mymitraa_taskflow?authSource=mymitraa_taskflow" \
     --drop \
     --archive="/var/backups/taskflow/daily/mymitraa_taskflow_backup_YYYYMMDD_HHMMSS.archive" \
     --gzip
   ```
3. **Verify Restored Collections and Document Counts**:
   ```bash
   mongosh "${MONGODB_URI}" --eval "db.tasks.countDocuments(); db.users.countDocuments(); db.audit_logs.countDocuments();"
   ```
4. **Restart Spring Boot Service**:
   ```bash
   systemctl start taskflow-backend
   systemctl status taskflow-backend
   ```
5. **Verify API Health**:
   ```bash
   curl -s http://127.0.0.1:8080/api/health
   ```
