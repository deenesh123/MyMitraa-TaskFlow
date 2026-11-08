package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProjectStatusRepository extends MongoRepository<ProjectStatus, String> {

    List<ProjectStatus> findByStatus(String status);
    List<ProjectStatus> findByOwner(String owner);
}