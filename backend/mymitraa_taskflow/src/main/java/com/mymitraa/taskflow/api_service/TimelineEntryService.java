package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.TimelineEntry;
import com.mymitraa.taskflow.api_repository.TimelineEntryRepository;
import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;

import java.util.Date;

public interface TimelineEntryService {

    Object save(TimelineEntry obj);

    Object fetchAll(Pageable pageable);

    Object getById(String id);

    Object update(TimelineEntry obj);

    Object delete(String id);

    Object fetchByRelatedEntity(String entityType, String entityId);

    Object fetchByDateRange(Date start, Date end);
}