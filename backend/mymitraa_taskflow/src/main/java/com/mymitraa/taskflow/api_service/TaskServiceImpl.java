package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.Task;
import com.mymitraa.taskflow.api_repository.TaskRepository;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired(required = false)
    private TaskOwnerEmailService taskOwnerEmailService;

    @Override
    public Object save(Task obj, CustomUserDetails customUserDetails) {
        log.info("Saving task: {}", obj.getTitle());
        obj.setDeleteFlag(false);
        if (obj.getCreatedOn() == null) {
            obj.setCreatedOn(new Date());
        }
        if (customUserDetails != null) {
            obj.setCreatedBy(customUserDetails.getUsername());
        }
        return taskRepository.save(obj);
    }

    @Override
    public Object fetchAll(String username, Pageable pageable) {
        log.info("Fetching tasks for user: {}", username);
        if (pageable != null) {
            return taskRepository.findAll(pageable);
        }
        return taskRepository.findAll();
    }

    @Override
    public Object getById(String id) {
        log.info("Fetching task by id: {}", id);
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Object update(Task obj, CustomUserDetails customUserDetails) {
        log.info("Updating task: {}", obj.getId());
        obj.setModifiedDateTime(new Date());
        if (customUserDetails != null) {
            obj.setModifiedBy(customUserDetails.getUsername());
        }
        return taskRepository.save(obj);
    }

    @Override
    public Object delete(String id) {
        log.info("Deleting task: {}", id);
        Task t = taskRepository.findById(id).orElse(null);
        if (t != null) {
            t.setDeleteFlag(true);
            t.setDeletedOn(new Date());
            taskRepository.save(t);
            return true;
        }
        return false;
    }

    @Override
    public Object getTaskOwnerEmail(String taskId) {
        log.info("Retrieving task owner email details for task: {}", taskId);
        Task t = taskRepository.findById(taskId).orElse(null);
        if (t != null && t.getOwner() != null) {
            return "Task owner email notification queued for: " + t.getOwner();
        }
        return "Task owner email processed";
    }
}
