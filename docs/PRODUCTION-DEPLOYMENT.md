# MyMitraa TaskFlow — Production Deployment Guide

This guide details the end-to-end production deployment process for MyMitraa TaskFlow onto the Hostinger Ubuntu 24.04 VPS (`201.18.214.36`) under domain `https://task.mitratechgroup.com`.

---

## 1. Production Architecture Topology

```
Internet Users (HTTPS:443)
       |
       v
Hostinger VPS (201.18.214.36)
  +--------------------------------------------------------------------------+
  | Nginx 1.24+                                                              |
  | Server Name: task.mitratechgroup.com                                     |
  | SSL: Let's Encrypt / Certbot                                             |
  |                                                                          |
  |   location / {                                                           |
  |       root /var/www/taskflow/dist;                                       |
  |       try_files $uri $uri/ /index.html;                                  |
  |   }                                                                      |
  |                                                                          |
  |   location /api/ {                                                       |
  |       proxy_pass http://127.0.0.1:8080;                                  |
  |       proxy_set_header Host $host;                                       |
  |       proxy_set_header X-Real-IP $remote_addr;                           |
  |       proxy_set_header X-Forwarded-Proto $scheme;                        |
  |   }                                                                      |
  +--------------------------------------------------------------------------+
       |                                       |
       | Static Files                          | Loopback Proxy
       v                                       v
  /var/www/taskflow/dist                Spring Boot 3.3.4 (PID / Systemd)
                                        Listening on 127.0.0.1:8080
                                               |
                                               v
                                        MongoDB 7.0 (127.0.0.1:27017)
                                        Database: mymitraa_taskflow
```

---

## 2. Directory & User Provisioning on VPS

Create dedicated system user and service directories:

```bash
# 1. Create taskflow system user (no login shell)
sudo useradd -r -s /bin/false -d /var/data/taskflow taskflow

# 2. Create runtime directories
sudo mkdir -p /var/www/taskflow/dist
sudo mkdir -p /var/data/mymitraa_uploads
sudo mkdir -p /var/log/taskflow
sudo mkdir -p /etc/taskflow
sudo mkdir -p /opt/taskflow

# 3. Assign ownership and permissions
sudo chown -R taskflow:taskflow /var/data/mymitraa_uploads /var/log/taskflow /opt/taskflow /etc/taskflow
sudo chmod 750 /var/data/mymitraa_uploads /opt/taskflow
sudo chmod 700 /etc/taskflow
sudo chown -R www-data:www-data /var/www/taskflow
```

---

## 3. Frontend Build & Deployment

Build locally on Windows or within CI/CD:

```powershell
# From project root:
npm install
npm run build
```

Upload the generated `dist/` directory to the VPS:
```bash
rsync -avz --delete dist/ deploy@201.18.214.36:/var/www/taskflow/dist/
sudo chown -R www-data:www-data /var/www/taskflow/dist
```

---

## 4. Backend Packaging & Deployment

Build executable JAR in `backend/mymitraa_taskflow`:

```powershell
mvn clean package -DskipTests
```

Deploy the artifact `mymitraa-taskflow-0.0.1-SNAPSHOT.jar` to `/opt/taskflow/app.jar` on the VPS:
```bash
scp target/mymitraa-taskflow-0.0.1-SNAPSHOT.jar deploy@201.18.214.36:/opt/taskflow/app.jar
sudo chown taskflow:taskflow /opt/taskflow/app.jar
sudo chmod 500 /opt/taskflow/app.jar
```

---

## 5. Systemd Service Definition (`/etc/systemd/system/taskflow-backend.service`)

Create the systemd unit file:

```ini
[Unit]
Description=MyMitraa TaskFlow Spring Boot Application
After=network.target mongodb.service

[Service]
Type=simple
User=taskflow
Group=taskflow
EnvironmentFile=/etc/taskflow/taskflow.env
WorkingDirectory=/opt/taskflow
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/taskflow/app.jar
Restart=always
RestartSec=10
StandardOutput=append:/var/log/taskflow/app.log
StandardError=append:/var/log/taskflow/error.log

# Security Sandbox
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/var/data/mymitraa_uploads /var/log/taskflow /tmp
PrivateTmp=true
NoNewPrivileges=true

[Install]
WantedBy=multi-user.target
```

Enable and start the service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable taskflow-backend
sudo systemctl start taskflow-backend
sudo systemctl status taskflow-backend
```

---

## 6. Nginx Virtual Host Configuration (`/etc/nginx/sites-available/taskflow`)

Create the Nginx site file:

```nginx
server {
    listen 80;
    server_name task.mitratechgroup.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name task.mitratechgroup.com;

    ssl_certificate /etc/letsencrypt/live/task.mitratechgroup.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/task.mitratechgroup.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # Client Static Assets
    root /var/www/taskflow/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API Proxy to Spring Boot
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
        client_max_body_size 50M;
    }
}
```

Enable site and test configuration:
```bash
sudo ln -s /etc/nginx/sites-available/taskflow /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## 7. Post-Deployment Verification

1. Verify backend health check:
   ```bash
   curl -i https://task.mitratechgroup.com/api/health
   # Expected: HTTP 200 {"status":"UP","app":"MyMitraa TaskFlow"}
   ```
2. Verify frontend loads over HTTPS in browser.
3. Verify login flow generates valid token and loads dashboard.
4. Verify MongoDB writes:
   ```bash
   mongosh "mongodb://taskflow_app_user:PASSWORD@127.0.0.1:27017/mymitraa_taskflow" --eval "db.audit_logs.find().sort({timestamp:-1}).limit(5)"
   ```
