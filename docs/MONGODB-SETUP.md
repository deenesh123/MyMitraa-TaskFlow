# MyMitraa TaskFlow — MongoDB Database Setup & Hardening

This document provides complete instructions for provisioning, configuring, and securing MongoDB for MyMitraa TaskFlow.

---

## 1. Database Topology & Naming Standards

MongoDB is the **SOLE** persistence database for MyMitraa TaskFlow. No relational databases (MySQL, PostgreSQL, Oracle, SQLite) or cloud DBs (Firebase, Supabase) are permitted.

| Environment | Database Name | Default Connection Host | Port |
|---|---|---|---|
| **Development** | `mymitraa_taskflow_dev` | `127.0.0.1` (Local Windows or SSH Tunnel) | `27017` / `27025` |
| **Testing** | `mymitraa_taskflow_test` | `127.0.0.1` (Local / CI ephemeral) | Dynamic / `27017` |
| **Production** | `mymitraa_taskflow` | `127.0.0.1` (Hostinger VPS local loopback) | `27017` (Verified port) |

---

## 2. Hostinger VPS Multi-Instance Isolation & Safety

The Hostinger VPS (`201.18.214.36`) hosts several critical production applications. A previous inspection documented the following MongoDB service landscape:

| Port | Bound IP | Service / Application | Protection Policy |
|---|---|---|---|
| `27017` | `127.0.0.1` | Default mongod (admin, config, local, mitraa) | **DO NOT STOP / RESTART** without authorization |
| `27018` | `127.0.0.1` | MiTRAA DART | **DO NOT TOUCH** |
| `27019` | `127.0.0.1` | DART SMTP test | **DO NOT TOUCH** |
| `27020` | `127.0.0.1` | DART production | **DO NOT TOUCH** |
| `27021` | `127.0.0.1` | DART Phase 7 | **DO NOT TOUCH** |

### Critical Safety Rules:
1. **Never kill or restart existing MongoDB services** on ports 27018 through 27021.
2. **Never bind MongoDB to 0.0.0.0** or expose any database port to the public internet.
3. Keep MongoDB bound exclusively to `127.0.0.1`.
4. Create dedicated, least-privilege application users scoped strictly to `mymitraa_taskflow`.

---

## 3. Provisioning the Production Database & User

Connect via SSH to the VPS (`201.18.214.36`) as an authorized administrator:

```bash
# Connect to the verified MongoDB instance on 127.0.0.1:27017
mongosh "mongodb://127.0.0.1:27017" -u admin -p --authenticationDatabase admin
```

Execute the following database initialization script:

```javascript
// Switch to target production database
use mymitraa_taskflow

// 1. Create dedicated least-privilege user
db.createUser({
  user: "taskflow_app_user",
  pwd: "SECURE_STRONG_PASSWORD_HERE",
  roles: [
    { role: "readWrite", db: "mymitraa_taskflow" }
  ]
})

// 2. Initialize required collections
db.createCollection("users")
db.createCollection("roles")
db.createCollection("tasks")
db.createCollection("projects")
db.createCollection("project_status")
db.createCollection("leads")
db.createCollection("bugs")
db.createCollection("documents")
db.createCollection("audit_logs")
db.createCollection("email_recipients")
db.createCollection("feedback_history")
db.createCollection("task_owner_emails")
db.createCollection("leave_requests")
db.createCollection("meetings")
db.createCollection("meeting_time_tracker")
db.createCollection("optima_leave_tracker")
db.createCollection("optima_weekly_update_tracker")
db.createCollection("timeline_entries")
db.createCollection("dropdown_managements")

// 3. Create performance and uniqueness indexes
db.users.createIndex({ "username": 1 }, { unique: true })
db.users.createIndex({ "email": 1 }, { unique: true, sparse: true })
db.tasks.createIndex({ "projectId": 1, "status": 1 })
db.tasks.createIndex({ "assignedTo": 1 })
db.audit_logs.createIndex({ "createdAt": -1 })
db.audit_logs.createIndex({ "action": 1 })
db.email_recipients.createIndex({ "recipientEmail": 1 })
db.bugs.createIndex({ "taskId": 1 })
```

---

## 4. Connection String Specifications

### Production URI Structure
In `/etc/taskflow/taskflow.env`:
```bash
MONGODB_URI=mongodb://taskflow_app_user:SECURE_STRONG_PASSWORD_HERE@127.0.0.1:27017/mymitraa_taskflow?authSource=mymitraa_taskflow
MONGODB_DATABASE=mymitraa_taskflow
```

### Development URI Structure (Local Windows)
In `backend/mymitraa_taskflow/src/main/resources/application-dev.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://127.0.0.1:27017/mymitraa_taskflow_dev
      database: mymitraa_taskflow_dev
```

---

## 5. Security & Verification Checks

Run this verification in `mongosh`:
```javascript
// Test authentication as taskflow_app_user
db.auth("taskflow_app_user", "SECURE_STRONG_PASSWORD_HERE")

// Verify read/write access to mymitraa_taskflow
db.audit_logs.insertOne({ action: "VERIFY_SETUP", timestamp: new Date() })
db.audit_logs.find({ action: "VERIFY_SETUP" })

// Verify that taskflow_app_user CANNOT access other VPS databases (e.g. DART)
// The following should return an authorization error:
use dart_production
db.users.find() // Result: Error: command find requires authentication/authorization
```
