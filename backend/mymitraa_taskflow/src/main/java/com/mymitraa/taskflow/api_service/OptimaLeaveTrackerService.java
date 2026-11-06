package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.OptimaLeaveTracker;
import com.mymitraa.taskflow.api_vm.OptimaLeaveTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;

public interface OptimaLeaveTrackerService {

    Object save(OptimaLeaveTracker optimaLeaveTracker, CustomUserDetails customUserDetails);

    Object getById(String id, CustomUserDetails customUserDetails);

    Object fetchAll(String username, SearchVM searchVM);

    Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable);

    Object update(String id, OptimaLeaveTrackerVM optimaLeaveTrackerVM, CustomUserDetails customUserDetails);

    Object download(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails);

    AdvanceSearchList getParamterList(CustomUserDetails customUserDetails);

    Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails);

    Object delete(String id, CustomUserDetails customUserDetails);

    Object dashboard(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails);
}