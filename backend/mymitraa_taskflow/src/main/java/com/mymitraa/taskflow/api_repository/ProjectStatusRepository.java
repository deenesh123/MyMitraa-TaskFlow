package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {

    ProjectStatus findByStatus(String status);
}