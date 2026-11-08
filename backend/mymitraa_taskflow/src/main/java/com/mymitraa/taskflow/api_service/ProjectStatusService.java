package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.ProjectStatus;
import org.springframework.data.domain.Pageable;

public interface ProjectStatusService {

    Object save(ProjectStatus obj);

    Object fetchAll(Pageable pageable);

    Object getById(String id);

    Object update(ProjectStatus obj);

    Object delete(String id);
}