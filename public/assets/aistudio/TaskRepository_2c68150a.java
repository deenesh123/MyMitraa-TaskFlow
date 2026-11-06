package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByTitle(String title);
    Task findByAssignee(String assignee);
}