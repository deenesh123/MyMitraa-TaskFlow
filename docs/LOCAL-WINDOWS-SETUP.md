# MyMitraa TaskFlow — Local Windows Development Setup Guide

This guide details the step-by-step procedure for setting up and running MyMitraa TaskFlow on a local Windows 10 Pro workstation using VS Code, Java 21, Node.js 22, and MongoDB.

---

## 1. Prerequisites & Required Tools

Ensure the following tools are installed on your Windows machine:
1. **Operating System**: Windows 10 Pro or Windows 11 (64-bit).
2. **Java Development Kit**: Eclipse Temurin or Oracle JDK 21 LTS (or JDK 17 minimum).
   - Verify: `java -version`
   - Set environment variable: `JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.x.x`
   - Add `%JAVA_HOME%\bin` to your system `PATH`.
3. **Apache Maven**: Version 3.9+ (or use the provided Maven wrapper `mvnw.cmd`).
   - Verify: `mvn -version`
4. **Node.js**: Node.js 22 LTS with npm 10+.
   - Verify: `node -v` and `npm -v`
5. **Git**: Git for Windows 2.40+.
   - Verify: `git --version`
6. **MongoDB Community Server**: Version 7.0+ (Local installation) OR SSH Client for VPS Tunneling.
   - Verify: `mongod --version` and `mongosh --version`

---

## 2. Repository Cloning & Branch Verification

From Windows PowerShell or Command Prompt:

```powershell
# Navigate to your preferred projects directory
cd C:\Users\LENOVO\Downloads

# Clone the repository (if not already present)
git clone https://github.com/deenesh123/MyMitraa-TaskFlow.git MyMitraa-TaskFlow-AISTUDIO-TEST
cd MyMitraa-TaskFlow-AISTUDIO-TEST

# Verify current branch is tracking production-readiness
git status
git branch -vv
```

Ensure the output confirms:
```
On branch production-readiness
Your branch is up to date with 'origin/production-readiness'.
nothing to commit, working tree clean
```

---

## 3. Database Setup: Development Options

The application requires the development database named **`mymitraa_taskflow_dev`**. Choose either Option A (Local MongoDB) or Option B (SSH Tunnel to VPS).

### Option A: Local Windows MongoDB (Recommended)
1. Start MongoDB Windows Service:
   ```powershell
   net start MongoDB
   ```
2. Verify connection using `mongosh`:
   ```powershell
   mongosh "mongodb://127.0.0.1:27017"
   ```
3. Initialize the dev database:
   ```javascript
   use mymitraa_taskflow_dev
   db.createCollection("users")
   ```

### Option B: Remote VPS via Secure SSH Tunnel
If connecting to a remote MongoDB instance running on VPS (201.18.214.36):
- **Never open MongoDB ports to 0.0.0.0 on the VPS**.
- Forward a local port through SSH:
  ```powershell
  ssh -N -L 27025:127.0.0.1:<VPS_MONGO_PORT> deploy@201.18.214.36
  ```
- Configure local connection URI to `mongodb://127.0.0.1:27025/mymitraa_taskflow_dev`.

---

## 4. Frontend Setup & Verification

1. Install npm dependencies:
   ```powershell
   npm install
   ```
2. Run automated tests to verify code integrity:
   ```powershell
   npm test
   ```
   *Expected: All 40+ tests pass with 0 failures.*
3. Run TypeScript type checks:
   ```powershell
   npm run lint
   ```
4. Run production bundle test:
   ```powershell
   npm run build
   ```

---

## 5. Backend Setup & Verification

1. Navigate to backend directory:
   ```powershell
   cd backend\mymitraa_taskflow
   ```
2. Run backend test suite with the `test` profile:
   ```powershell
   mvn test -Dspring.profiles.active=test
   ```
   *Expected: All unit and security tests pass.*
3. Package the executable JAR:
   ```powershell
   mvn clean package -DskipTests
   ```
   *Generated artifact: `target/mymitraa-taskflow-0.0.1-SNAPSHOT.jar`.*

---

## 6. Running the Full Stack Locally

### Terminal 1: Run Backend (Spring Boot)
```powershell
cd C:\Users\LENOVO\Downloads\MyMitraa-TaskFlow-AISTUDIO-TEST\backend\mymitraa_taskflow
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Backend will start on port `8080`. Verify health:
```powershell
curl http://127.0.0.1:8080/api/health
# Response: {"status":"UP","app":"MyMitraa TaskFlow"}
```

### Terminal 2: Run Frontend Dev Server
```powershell
cd C:\Users\LENOVO\Downloads\MyMitraa-TaskFlow-AISTUDIO-TEST
npm run dev
```
Dev server starts on port `3000` (via `server.ts` Express + Vite middleware, proxying `/api` requests to `8080`).

Access in browser: `http://localhost:3000`

---

## 7. Troubleshooting Local Issues

| Issue | Root Cause | Solution |
|---|---|---|
| `Cannot connect to MongoDB on 27017` | Local MongoDB service stopped | Run `net start MongoDB` or verify service status in `services.msc`. |
| `Port 8080 already in use` | Zombie Java process | Run `netstat -ano \| findstr :8080` and terminate PID with `taskkill /PID <PID> /F`. |
| `Port 3000 already in use` | Zombie Node process | Run `netstat -ano \| findstr :3000` and terminate PID with `taskkill /PID <PID> /F`. |
| `JWT Expired / 401 Unauthorized` | Clock skew or expired token | Sign in with valid credentials; token automatically lasts 24 hours. |
| `bun.lock detected` | Non-standard lockfile | Remove `bun.lock` if generated; use only `package-lock.json`. |
