package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import com.mymitraa.taskflow.api_repository.MeetingTimeTrackerRepository;
import com.mymitraa.taskflow.api_vl.MeetingTimeTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MeetingTimeTrackerServiceImpl implements MeetingTimeTrackerService {

    private static final Logger log = LoggerFactory.getLogger(MeetingTimeTrackerServiceImpl.class);

    @Autowired
    private MeetingTimeTrackerRepository meetingTimeTrackerRepository;

    @Override
    public Object save(MeetingTimeTracker meetingTimeTracker, CustomUserDetails customUserDetails) {
        log.info("Saving MeetingTimeTracker: {}", meetingTimeTracker.getMeetingId());
        return meetingTimeTrackerRepository.save(meetingTimeTracker);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting MeetingTimeTracker by id: {}", id);
        return meetingTimeTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all MeetingTimeTracker records");
        return meetingTimeTrackerRepository.findAll();
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching MeetingTimeTracker records with pagination");
        return meetingTimeTrackerRepository.findAll(pageable);
    }

    @Override
    public Object update(String id, MeetingTimeTrackerVM meetingTimeTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Updating MeetingTimeTracker: {}", id);
        MeetingTimeTracker existing = meetingTimeTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setMeetingId(meetingTimeTrackerVM.getMeetingId());
            existing.setAttendee(meetingTimeTrackerVM.getAttendee());
            return meetingTimeTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(MeetingTimeTrackerVM meetingTimeTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Downloading MeetingTimeTracker data: {}", meetingTimeTrackerVm.getId());
        return meetingTimeTrackerRepository.findById(meetingTimeTrackerVm.getId()).orElse(null);
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        log.info("Getting parameter list for MeetingTimeTracker");
        return new AdvanceSearchList();
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        log.info("Advance search for MeetingTimeTracker");
        return meetingTimeTrackerRepository.findAll(pageable);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting MeetingTimeTracker: {}", id);
        meetingTimeTrackerRepository.deleteById(id);
        return id;
    }

    @Override
    public Object dashboard(MeetingTimeTrackerVM meetingTimeTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Getting dashboard for MeetingTimeTracker");
        return meetingTimeTrackerRepository.findAll();
    }
}