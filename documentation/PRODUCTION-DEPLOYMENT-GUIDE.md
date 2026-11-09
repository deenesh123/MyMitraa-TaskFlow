# MyMitraa TaskFlow — Production Deployment Architecture & Runbook
**Target Domain**: `task.mitratechgroup.com`  
**Authoritative Backend**: Spring Boot 3.3.4 (Java 17)  
**Authoritative Database**: MongoDB 6.0+  
**Frontend**: React 19 SPA (Vite compiled static assets)  

---

## 1. System Architecture Overview

```
                                  HTTPS (Port 443)
                                         │
                                         ▼
                            ┌────────────────────────┐
                            │      Nginx Proxy       │
                            │ task.mitratechgroup.com│
                            └────────────┬───────────┘
                                         │
                ┌────────────────────────┴────────────────────────┐
                │                                                 │
          /api/* requests                                   /* (Static SPA)
                │                                                 │
                ▼                                                 ▼
    ┌───────────────────────┐                         ┌───────────────────────┐
    │  Spring Boot Backend  │                         │   Vite Static Build   │
    │   (Port 8080 / Java)  │                         │     (/var/www/html)   │
    └───────────┬───────────┘                         └───────────────────────┘
                │
                ▼
    ┌───────────────────────┐
    │   MongoDB Database    │
    │   (Port 27017 / BSON) │
    │  'mymitraa_taskflow'  │
    └───────────────────────┘
```

---

## 2. Server Prerequisites (When Provisioning VPS)

- **OS Recommendation**: Ubuntu 22.04 LTS or 24.04 LTS (x86_64 or arm64)
- **Minimum Specs**: 2 vCPU, 4GB RAM, 40GB SSD
- **Packages**:
  - OpenJDK 17 (`sudo apt install openjdk-17-jdk-headless`)
  - MongoDB 7.0+ Community Edition
  - Nginx (`sudo apt install nginx`)
  - Certbot (`sudo apt install certbot python3-certbot-nginx`)

---

## 3. Production MongoDB Configuration

1. Enable authentication in `/etc/mongod.conf`:
   ```yaml
   security:
     authorization: enabled
   ```
2. Create dedicated application database and credentials:
   ```javascript
   use admin;
   db.createUser({
     user: "adminUser",
     pwd: "StrongAdminPasswordHere",
     roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
   });

   use mymitraa_taskflow;
   db.createUser({
     user: "mymitraa_user",
     pwd: "StrongAppPasswordHere",
     roles: [ { role: "readWrite", db: "mymitraa_taskflow" } ]
   });
   ```

---

## 4. Spring Boot Backend Systemd Service

Create `/etc/systemd/system/mymitraa-taskflow.service`:

```ini
[Unit]
Description=MyMitraa TaskFlow Authoritative Spring Boot Backend
After=network.target mongod.service

[Service]
User=mymitraa
Group=mymitraa
WorkingDirectory=/opt/mymitraa-taskflow/backend
ExecStart=/usr/bin/java -Xms512m -Xmx1536m -jar mymitraa-taskflow.jar
Restart=always
RestartSec=10

# Environment Configuration
Environment="SERVER_PORT=8080"
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="MONGODB_URI=mongodb://mymitraa_user:StrongAppPasswordHere@127.0.0.1:27017/mymitraa_taskflow?authSource=mymitraa_taskflow"
Environment="JWT_SECRET=YOUR_64_BYTE_CRYPTOGRAPHIC_JWT_SECRET_KEY_HERE"
Environment="JWT_EXPIRATION=86400000"
Environment="CORS_ALLOWED_ORIGINS=https://task.mitratechgroup.com"
Environment="ZOHO_SMTP_HOST=smtp.zoho.com"
Environment="ZOHO_SMTP_PORT=465"
Environment="ZOHO_SMTP_USER=notifications@mitratechgroup.com"
Environment="ZOHO_SMTP_PASSWORD=YOUR_ZOHO_APP_SPECIFIC_PASSWORD"
Environment="ZOHO_FROM_EMAIL=notifications@mitratechgroup.com"
Environment="ZOHO_FROM_NAME=MY MiTRAA TaskFlow"

[Install]
WantedBy=multi-user.target
```

---

## 5. Production Nginx Configuration for `task.mitratechgroup.com`

Create `/etc/nginx/sites-available/task.mitratechgroup.com`:

```nginx
server {
    listen 80;
    server_name task.mitratechgroup.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name task.mitratechgroup.com;

    # SSL Certificates managed via Certbot
    ssl_certificate /etc/letsencrypt/live/task.mitratechgroup.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/task.mitratechgroup.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security Headers
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options SAMEORIGIN always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Root for compiled React SPA assets (npm run build -> dist)
    root /var/www/task.mitratechgroup.com/dist;
    index index.html;

    # Static Assets Cache
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API Reverse Proxy to Spring Boot Backend
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # File upload support
        client_max_body_size 50M;

        # Timeouts
        proxy_connect_timeout 15s;
        proxy_read_timeout 60s;
        proxy_send_timeout 60s;
    }

    # SPA Fallback routing
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

---

## 6. DNS Setup Checklist (When DNS Provider is Selected)

| Type | Name | Target / Value | TTL |
|------|------|----------------|-----|
| A | `task` | `YOUR_SERVER_PUBLIC_IP` | 300 |
| TXT | `task` | (Zoho SPF / Domain verification) | 3600 |

Once DNS resolves to the VPS IP, generate SSL certificate:
```bash
sudo certbot --nginx -d task.mitratechgroup.com
```

---

## 7. Build and Deployment Commands

```bash
# 1. Build Frontend Static Bundle
npm run build
# Output in /dist is copied to /var/www/task.mitratechgroup.com/dist

# 2. Package Backend JAR
cd backend/mymitraa_taskflow
mvn clean package -DskipTests
# Output target/mymitraa-taskflow-3.0.0.jar deployed to /opt/mymitraa-taskflow/backend/mymitraa-taskflow.jar

# 3. Restart Services
sudo systemctl restart mymitraa-taskflow
sudo systemctl reload nginx
```
