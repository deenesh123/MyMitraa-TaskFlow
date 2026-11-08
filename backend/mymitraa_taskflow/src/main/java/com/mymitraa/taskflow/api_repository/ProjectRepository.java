package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Optional<Project> findByProjectId(String projectId);
    List<Project> findByStatus(String status);
    List<Project> findByProjectManager(String projectManager);
}
