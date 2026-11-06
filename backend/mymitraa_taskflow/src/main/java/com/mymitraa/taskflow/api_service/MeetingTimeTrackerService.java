package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import com.mymitraa.taskflow.api_vl.MeetingTimeTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;

public interface MeetingTimeTrackerService {

    Object save(MeetingTimeTracker meetingTimeTracker, CustomUserDetails customUserDetails);

    Object getById(String id, CustomUserDetails customUserDetails);

    Object fetchAll(String username, SearchVM searchVM);

    Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable);

    Object update(String id, MeetingTimeTrackerVM meetingTimeTrackerVM, CustomUserDetails customUserDetails);

    Object download(MeetingTimeTrackerVM meetingTimeTrackerVm, CustomUserDetails customUserDetails);

    AdvanceSearchList getParamterList(CustomUserDetails customUserDetails);

    Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails);

    Object delete(String id, CustomUserDetails customUserDetails);

    Object dashboard(MeetingTimeTrackerVM meetingTimeTrackerVm, CustomUserDetails customUserDetails);
}