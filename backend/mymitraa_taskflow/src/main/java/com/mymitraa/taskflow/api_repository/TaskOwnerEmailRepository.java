package com.mymitraa.taskflow.api_repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.mymitraa.taskflow.api_domain.TaskOwnerEmail;

/**
 * Spring Data MongoDB repository for TaskOwnerEmail entity.
 */
@Repository
public interface TaskOwnerEmailRepository extends MongoRepository<TaskOwnerEmail, String> {

    List<TaskOwnerEmail> findByTaskId(String taskId);

    List<TaskOwnerEmail> findByOwnerEmail(String ownerEmail);

    List<TaskOwnerEmail> findByStatus(String status);
}
