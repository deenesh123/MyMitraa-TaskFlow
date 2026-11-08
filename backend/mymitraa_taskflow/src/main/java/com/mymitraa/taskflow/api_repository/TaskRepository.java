package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByTitle(String title);
    List<Task> findByAssignee(String assignee);
    List<Task> findByOwner(String owner);
}