package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    Task findByTitle(String title);
    Task findByAssignee(String assignee);
}