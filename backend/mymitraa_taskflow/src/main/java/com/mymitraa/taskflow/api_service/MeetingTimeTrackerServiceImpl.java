package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import com.mymitraa.taskflow.api_repository.MeetingTimeTrackerRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingTimeTrackerServiceImpl implements MeetingTimeTrackerService {

    private static final Logger log = LoggerFactory.getLogger(MeetingTimeTrackerServiceImpl.class);

    @Autowired
    private MeetingTimeTrackerRepository meetingTimeTrackerRepository;

    @Override
    public Object save(MeetingTimeTracker meetingTimeTracker) {
        log.info("Saving MeetingTimeTracker: {}", meetingTimeTracker.getMeetingId());
        return meetingTimeTrackerRepository.save(meetingTimeTracker);
    }

    @Override
    public Object getById(Long id) {
        log.info("Getting MeetingTimeTracker by id: {}", id);
        return meetingTimeTrackerRepository.findById(id.toString()).orElse(null);
    }

    @Override
    public Object fetchAll(Pageable pageable) {
        log.info("Fetching all MeetingTimeTracker");
        return meetingTimeTrackerRepository.findAll(pageable);
    }

    @Override
    public Object getById(Long id) {
        return meetingTimeTrackerRepository.findById(id.toString()).orElse(null);
    }

    @Override
    public Object update(MeetingTimeTracker meetingTimeTracker) {
        log.info("Updating MeetingTimeTracker: {}", meetingTimeTracker.getId());
        return meetingTimeTrackerRepository.save(meetingTimeTracker);
    }

    @Override
    public Object delete(Long id) {
        log.info("Deleting MeetingTimeTracker: {}", id);
        meetingTimeTrackerRepository.deleteById(id.toString());
        return "Deleted successfully";
    }
}