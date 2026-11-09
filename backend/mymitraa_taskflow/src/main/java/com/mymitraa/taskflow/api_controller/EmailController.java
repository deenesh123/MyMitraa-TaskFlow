package com.mymitraa.taskflow.api_controller;

import com.mymitraa.taskflow.api_successcode.CustomResponse;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    @Value("${spring.mail.host:smtp.zoho.com}")
    private String smtpHost;

    @Value("${spring.mail.port:465}")
    private int smtpPort;

    @Value("${spring.mail.username:}")
    private String smtpUser;

    @Value("${mymitraa.mail.from-name:MY MiTRAA TaskFlow}")
    private String fromName;

    @Value("${mymitraa.mail.from-email:notifications@mymitraa.com}")
    private String fromEmail;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final List<Map<String, Object>> emailAuditLogs = new CopyOnWriteArrayList<>();

    @GetMapping("/config")
    public ResponseEntity<CustomResponse> getEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", smtpHost);
        config.put("smtpPort", smtpPort);
        config.put("fromName", fromName);
        config.put("fromEmail", fromEmail);
        config.put("smtpUser", smtpUser != null && !smtpUser.isEmpty() ? smtpUser : "");
        config.put("configured", smtpUser != null && !smtpUser.isEmpty());
        // Do NOT expose password or secret tokens
        return ResponseEntity.ok(new CustomResponse(200, "EMAIL_CONFIG_FETCHED", config));
    }

    @PostMapping("/test")
    public ResponseEntity<CustomResponse> testEmail(@RequestBody Map<String, String> payload) {
        String to = payload.get("to");
        if (to == null || to.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse(400, "Recipient email ('to') is required for test email dispatch"));
        }

        if (smtpUser == null || smtpUser.trim().isEmpty() || mailSender == null) {
            Map<String, Object> unconfiguredResult = new HashMap<>();
            unconfiguredResult.put("recipient", to);
            unconfiguredResult.put("status", "UNCONFIGURED");
            unconfiguredResult.put("smtpHost", smtpHost);
            unconfiguredResult.put("smtpPort", smtpPort);
            unconfiguredResult.put("error", "Zoho SMTP credentials are not configured in environment variables (ZOHO_SMTP_USER / ZOHO_SMTP_PASSWORD missing).");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new CustomResponse(503, "ZOHO_SMTP_NOT_CONFIGURED", unconfiguredResult));
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to.trim());
            String htmlContent = "<div style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 650px; margin: 0 auto; color: #191627;\">"
                    + "<h2 style=\"color: #7B3AEC; margin-bottom: 8px;\">Zoho Mail SMTP Integration Verified</h2>"
                    + "<p>This is a live verification email dispatched from <b>MyMitraa TaskFlow</b> via the Zoho SMTP gateway (<b>"
                    + smtpHost + ":" + smtpPort + "</b>).</p>"
                    + "<p style=\"color: #475569; font-size: 13px;\">Delivery Engine: JavaMailSender (SMTPS :465) · Security: TLS 1.3 · Database: MongoDB</p>"
                    + "<div style=\"margin: 16px 0; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;\">"
                    + "<img src=\"cid:dashboardPreview\" alt=\"MyMitraa TaskFlow Dashboard\" style=\"width: 100%; height: auto; display: block;\" />"
                    + "</div>"
                    + "<p style=\"font-size: 12px; color: #64748b;\">Timestamp: " + new Date() + "</p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            // Attach approved dashboard image inline via CID
            try {
                org.springframework.core.io.Resource imgResource = new org.springframework.core.io.ClassPathResource("static/dashboard-preview.jpeg");
                if (!imgResource.exists()) {
                    java.io.File file = new java.io.File("public/assets/dashboard-preview.jpeg");
                    if (file.exists()) {
                        imgResource = new org.springframework.core.io.FileSystemResource(file);
                    }
                }
                if (imgResource != null && imgResource.exists()) {
                    helper.addInline("dashboardPreview", imgResource, "image/jpeg");
                }
            } catch (Exception imgEx) {
                log.warn("Could not attach inline dashboard image: {}", imgEx.getMessage());
            }

            mailSender.send(message);

            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("id", "log-" + System.currentTimeMillis());
            logEntry.put("type", "TEST_EMAIL");
            logEntry.put("recipient", to);
            logEntry.put("status", "SENT");
            logEntry.put("smtpHost", smtpHost);
            logEntry.put("timestamp", new Date());
            emailAuditLogs.add(0, logEntry);

            Map<String, Object> result = new HashMap<>();
            result.put("recipient", to);
            result.put("status", "SENT");
            result.put("smtpHost", smtpHost);
            result.put("smtpPort", smtpPort);
            result.put("timestamp", new Date());

            return ResponseEntity.ok(new CustomResponse(200, "EMAIL_TEST_SUCCESSFUL", result));
        } catch (Exception ex) {
            log.error("Failed to dispatch Zoho SMTP test email to {}: {}", to, ex.getMessage(), ex);

            Map<String, Object> failureEntry = new HashMap<>();
            failureEntry.put("id", "log-" + System.currentTimeMillis());
            failureEntry.put("type", "TEST_EMAIL");
            failureEntry.put("recipient", to);
            failureEntry.put("status", "FAILED");
            failureEntry.put("smtpHost", smtpHost);
            failureEntry.put("error", ex.getMessage());
            failureEntry.put("timestamp", new Date());
            emailAuditLogs.add(0, failureEntry);

            Map<String, Object> errResult = new HashMap<>();
            errResult.put("recipient", to);
            errResult.put("status", "FAILED");
            errResult.put("error", ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse(500, "EMAIL_SEND_FAILED: " + ex.getMessage(), errResult));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<CustomResponse> getEmailLogs() {
        return ResponseEntity.ok(new CustomResponse(200, "EMAIL_LOGS_FETCHED", new ArrayList<>(emailAuditLogs)));
    }
}
