# MyMitraa TaskFlow — Backend Verification Guide

## 1. Environment & Readiness Status

- **Project Root**: `/backend/mymitraa_taskflow`
- **Target Java Version**: Java 21 LTS
- **Spring Boot Version**: 3.3.4
- **Build System**: Apache Maven 3.9+
- **Target Database**: MongoDB 7.0+ (`mymitraa_taskflow`)
- **Container Environment Note**: The current AI Studio Build environment runs on a lightweight Node.js 22 runtime without a local JDK 21 or `mongod` binary installed. Therefore, Java compilation cannot be executed directly within this specific container shell. The codebase has been fully refactored, modernized, and structured for external compilation on any machine with JDK 21 and Maven 3.9+.

---

## 2. Java 21 & Maven Build Verification Commands

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
ls -lh target/taskflow-3.0.0.jar
```

---

## 3. MongoDB 7.0+ Configuration & Connectivity

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

## 4. Running the Spring Boot Application

```bash
# Run with active profile and environment variables
export ZOHO_SMTP_HOST="smtp.zoho.com"
export ZOHO_SMTP_PORT="465"
export ZOHO_SMTP_USER="notifications@mymitraa.com"
export ZOHO_SMTP_PASSWORD="your-zoho-app-password"
export MONGODB_URI="mongodb://localhost:27017/mymitraa_taskflow"

java -jar target/taskflow-3.0.0.jar
```

---

## 5. End-to-End API Verification Checklist

Once the Java Spring Boot service starts on port 8080:

### 1. Actuator & Health Check
```bash
curl -s http://localhost:8080/actuator/health | jq .
```
Expected Response:
```json
{"status": "UP"}
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
    "ticket_number": "MTF-1001",
    "subject": "Review MongoDB Schema Updates",
    "task_owner": "Lead Engineer",
    "owner_email": "engineer@mymitraa.com",
    "priority": "High"
  }' | jq .
```
