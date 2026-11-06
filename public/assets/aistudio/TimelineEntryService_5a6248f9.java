package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.TimelineEntry;
import com.mymitraa.taskflow.api_repository.TimelineEntryRepository;
import org.springframework.data.domain.Pageable;
import com.kelltontech.optima.api_successcode.CustomResponse;
import com.kelltontech.optima.api_successcode.SuccessCode;

public interface TimelineEntryService {

    Object save(TimelineEntry obj);

    Object fetchAll(Pageable pageable);

    Object getById(Long id);

    Object update(TimelineEntry obj);

    Object delete(Long id);

    Object fetchByRelatedEntity(String entityType, Long entityId);

    Object fetchByDateRange(LocalDateTime start, LocalDateTime end);
}