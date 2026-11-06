package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.Task;
import com.mymitraa.taskflow.api_repository.TaskRepository;
import com.mymitraa.taskflow.api_vm.TaskVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import com.kelltontech.optima.api_successcode.CustomResponse;
import com.kelltontech.optima.api_successcode.SuccessCode;

public interface TaskService {

    Object save(Task obj, CustomUserDetails customUserDetails);

    Object fetchAll(String username, Pageable pageable);

    Object getById(String id);

    Object update(Task obj, CustomUserDetails customUserDetails);

    Object delete(String id);

    Object getTaskOwnerEmail(String taskId);
}