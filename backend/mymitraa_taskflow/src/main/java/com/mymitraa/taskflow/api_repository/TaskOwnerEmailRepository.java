package com.mymitraa.taskflow.api_repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mymitraa.taskflow.api_domain.TaskOwnerEmail;

import java.util.List;

/**
 * MongoDB repository for TaskOwnerEmail in mymitraa_taskflow database.
 */
@Repository
public interface TaskOwnerEmailRepository extends MongoRepository<TaskOwnerEmail, String> {

    List<TaskOwnerEmail> findByTaskId(String taskId);

    List<TaskOwnerEmail> findByOwnerEmail(String ownerEmail);
}
