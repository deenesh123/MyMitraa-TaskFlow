# MyMitraa TaskFlow — Backend Verification Guide

## 1. Environment & Readiness Status

- **Project Root**: `/backend/mymitraa_taskflow`
- **Target Java Version**: Java 21 LTS
- **Spring Boot Version**: 3.3.4
- **Build System**: Apache Maven 3.9+
- **Executable Output Artifact**: `target/mymitraa-taskflow-3.0.0.jar`
- **Target Database**: MongoDB 7.0+ (`mymitraa_taskflow`)
- **Email Service**: Zoho Mail SMTP (`smtp.zoho.com:465` SSL / `smtp.zoho.com:587` TLS)
- **Container Environment Note**: The current AI Studio Build environment runs on a lightweight Node.js 22-slim runtime without a local JDK 21 or `mongod` binary installed (`java: not found`, `mvn: not found`). Therefore, Java compilation cannot be executed directly within this specific container shell. The codebase has been fully refactored, modernized, and structured for execution on any host or container equipped with JDK 21 and Maven 3.9+.

---

## 2. Verification Classification Matrix

To maintain strict truth-in-reporting, each component is classified under one of five verified categories:

| Component / Layer | Classification | Details |
|---|---|---|
| **React Frontend SPA** | **[VERIFIED - LIVE EXECUTED]** | Compiled with Vite 6.2.0; verified clean TypeScript typecheck (`tsc --noEmit`); loads with HTTP 200 on port 3000. |
| **Express Preview Server** | **[VERIFIED - LIVE EXECUTED]** | Running on port 3000; provides simulated database, dual REST routes, and health endpoint (`/api/health`). |
| **Java 21 Spring Boot Codebase** | **[CONFIRMED - SOURCE AUDITED]** | All 12 controllers, services, repositories, DTOs, and configs audited; missing imports and signatures resolved. |
| **Spring Data MongoDB Mappings** | **[CONFIRMED - SOURCE AUDITED]** | Zero JPA annotations; 100% `@Document(collection = "...")` and `@Id String id`; verified database name `mymitraa_taskflow`. |
| **Spring Security 6 & JWT** | **[CONFIRMED - SOURCE AUDITED]** | `JwtAuthenticationFilter`, `WebSecurityConfiguration`, `BCryptPasswordEncoder`, and dynamic role authorities audited. |
| **Zoho Mail Integration** | **[CONFIRMED - SOURCE AUDITED]** | `ZohoMailConfig` uses `JavaMailSender` with server-side environment variable bindings; Express preview includes simulated SMTP. |
| **Live Java Maven Build & Test** | **[EXTERNAL BLOCKED]** | Blocked inside sandbox due to missing JDK 21 / Maven in Linux Node 22-slim environment. |
| **Live MongoDB 7.0 Connection** | **[EXTERNAL BLOCKED]** | Blocked inside sandbox due to missing `mongod` service in Linux Node 22-slim environment. |

---

## 3. Java 21 & Maven Build Verification Commands

To compile, test, and package the Spring Boot backend on a host with JDK 21:

```bash
# Navigate to the backend directory
cd backend/mymitraa_taskflow

# 1. Clean and compile source code
mvn clean compile

# 2. Run unit and integration test suites
mvn test

# 3. Package into executable standalone JAR
mvn clean package -DskipTests=false

# 4. Verify JAR artifact generation
ls -lh target/mymitraa-taskflow-3.0.0.jar
```

---

## 4. MongoDB 7.0+ Configuration & Connectivity

The application connects to MongoDB using the URI specified in `application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/mymitraa_taskflow}
      database: ${MONGODB_DATABASE:mymitraa_taskflow}
      auto-index-creation: true
```

### Starting MongoDB locally via Docker:
```bash
docker run -d \
  --name mymitraa-mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_DATABASE=mymitraa_taskflow \
  mongo:7.0
```

### Verifying connection via mongosh:
```bash
mongosh "mongodb://localhost:27017/mymitraa_taskflow" --eval "db.getCollectionNames()"
```

---

## 5. Running the Spring Boot Application in Production

```bash
# Run with active profile and environment variables
export SPRING_PROFILES_ACTIVE="prod"
export SERVER_PORT="8080"
export MONGODB_URI="mongodb://localhost:27017/mymitraa_taskflow"
export MONGODB_DATABASE="mymitraa_taskflow"

# Zoho SMTP Credentials (Server-Side Only - Never exposed to frontend)
export ZOHO_SMTP_HOST="smtp.zoho.com"
export ZOHO_SMTP_PORT="465"
export ZOHO_SMTP_USER="notifications@mymitraa.com"
export ZOHO_SMTP_PASSWORD="your-zoho-app-password"
export ZOHO_SMTP_FROM_EMAIL="notifications@mymitraa.com"
export ZOHO_SMTP_FROM_NAME="MyMitraa TaskFlow"

# JWT Secret (minimum 256 bits)
export JWT_SECRET="mymitraa-taskflow-jwt-super-secret-key-production-2026-secure"
export JWT_EXPIRATION="86400000"

# Execute JAR
java -jar target/mymitraa-taskflow-3.0.0.jar
```

---

## 6. End-to-End API Verification Checklist

Once the Java Spring Boot service starts on port 8080:

### 1. Actuator & Health Check
```bash
curl -s http://localhost:8080/actuator/health | jq .
```
Expected Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MongoDB",
        "version": "7.0.x"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### 2. User Authentication (JWT Issue)
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.token')
echo "Token: $TOKEN"
```

### 3. Fetch Dropdown Management
```bash
curl -s -X GET http://localhost:8080/api/dropdowns/get_all \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### 4. Create Task Owner Email Notification
```bash
curl -s -X POST http://localhost:8080/api/task_owner_email/send \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketNumber": "MTF-1001",
    "subject": "Review MongoDB Schema Updates",
    "taskOwner": "Lead Engineer",
    "ownerEmail": "engineer@mymitraa.com",
    "priority": "High"
  }' | jq .
```

### 5. Paginate Feedback History
```bash
curl -s -X PUT http://localhost:8080/api/feedback_history/get_all_with_pagination \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pageNumber": 0, "pageSize": 10}' | jq .
```

### 6. Verify Global File Upload Metadata
```bash
curl -s -X PUT http://localhost:8080/api/global_uploads/get_uploaded_files \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pageNumber": 0, "pageSize": 10}' | jq .
```

---

## 7. Connecting Frontend to Production Java Backend

In production environments or staging deployments:

1. Set the frontend environment variable:
   ```env
   VITE_API_BASE_URL=http://your-backend-host:8080
   VITE_API_MODE=production
   ```
2. Build the production React assets:
   ```bash
   npm run build
   ```
3. When `VITE_API_MODE=production`, `src/api/client.ts` will strictly dispatch all requests to the Java backend at `VITE_API_BASE_URL` and throw immediate network errors upon failure, ensuring zero silent mock fallbacks.
