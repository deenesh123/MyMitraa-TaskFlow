package com.mymitraa.taskflow.api_controller;

import com.mymitraa.taskflow.api_domain.AuditLog;
import com.mymitraa.taskflow.api_mv.CustomResponse;
import com.mymitraa.taskflow.api_repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CustomResponse<List<AuditLog>>> getAllLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String outcome) {
        
        List<AuditLog> logs;
        if (module != null && !module.equalsIgnoreCase("All")) {
            logs = auditLogRepository.findByModule(module);
        } else if (outcome != null && !outcome.equalsIgnoreCase("All")) {
            logs = auditLogRepository.findByOutcome(outcome);
        } else {
            logs = auditLogRepository.findAll();
        }

        CustomResponse<List<AuditLog>> response = new CustomResponse<>();
        response.setCode(200);
        response.setMessage("AUDIT_LOGS_FETCHED_SUCCESSFULLY");
        response.setData(logs);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CustomResponse<AuditLog>> createAuditLog(@RequestBody AuditLog log) {
        if (log.getTimestamp() == null || log.getTimestamp().trim().isEmpty()) {
            log.setTimestamp(LocalDateTime.now().format(FORMATTER));
        }
        AuditLog saved = auditLogRepository.save(log);

        CustomResponse<AuditLog> response = new CustomResponse<>();
        response.setCode(200);
        response.setMessage("AUDIT_LOG_RECORDED");
        response.setData(saved);
        return ResponseEntity.ok(response);
    }
}
