package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import com.mymitraa.taskflow.api_repository.MeetingTimeTrackerRepository;
import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;

public interface MeetingTimeTrackerService {

    Object save(MeetingTimeTracker obj);

    Object fetchAll(Pageable pageable);

    Object getById(Long id);

    Object update(MeetingTimeTracker obj);

    Object delete(Long id);
}