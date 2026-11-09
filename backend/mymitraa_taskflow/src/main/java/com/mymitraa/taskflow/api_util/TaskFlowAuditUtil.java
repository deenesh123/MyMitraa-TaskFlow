package com.mymitraa.taskflow.api_util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskFlowAuditUtil {

    private final Logger log = LoggerFactory.getLogger(TaskFlowAuditUtil.class);

    public void updateAuditLog(String userId, String username, String recordId, String message, String action) {
        log.info("AUDIT_LOG | user: {} ({}) | recordId: {} | action: {} | message: {}",
                username, userId, recordId, action, message);
    }
}
