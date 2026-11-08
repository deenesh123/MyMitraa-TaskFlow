package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Bug;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends MongoRepository<Bug, String> {
    List<Bug> findBySeverity(String severity);
    List<Bug> findByStatus(String status);
    List<Bug> findByProject(String project);
    List<Bug> findByAssignedTo(String assignedTo);
}
