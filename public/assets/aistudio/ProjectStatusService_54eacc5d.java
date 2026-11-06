package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.ProjectStatus;
import com.mymitraa.taskflow.api_repository.ProjectStatusRepository;
import org.springframework.data.domain.Pageable;
import com.kelltontech.optima.api_successcode.CustomResponse;
import com.kelltontech.optima.api_successcode.SuccessCode;

public interface ProjectStatusService {

    Object save(ProjectStatus obj);

    Object fetchAll(Pageable pageable);

    Object getById(Long id);

    Object update(ProjectStatus obj);

    Object delete(Long id);
}