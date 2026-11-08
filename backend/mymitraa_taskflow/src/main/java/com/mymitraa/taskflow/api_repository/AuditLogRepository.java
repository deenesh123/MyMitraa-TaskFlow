package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByModule(String module);
    List<AuditLog> findByUserId(String userId);
    List<AuditLog> findByOutcome(String outcome);
}
