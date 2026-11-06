package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectStatusRepository extends MongoRepository<ProjectStatus, String> {

    ProjectStatus findByStatus(String status);
}