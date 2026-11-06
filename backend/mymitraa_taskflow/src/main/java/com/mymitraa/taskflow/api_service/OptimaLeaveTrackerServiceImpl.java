package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.OptimaLeaveTracker;
import com.mymitraa.taskflow.api_vm.OptimaLeaveTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.api_repository.OptimaLeaveTrackerRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OptimaLeaveTrackerServiceImpl implements OptimaLeaveTrackerService {

    private static final Logger log = LoggerFactory.getLogger(OptimaLeaveTrackerServiceImpl.class);

    @Autowired
    private OptimaLeaveTrackerRepository optimaLeaveTrackerRepository;

    @Override
    public Object save(OptimaLeaveTracker optimaLeaveTracker, CustomUserDetails customUserDetails) {
        log.info("Saving OptimaLeaveTracker: {}", optimaLeaveTracker.getEmployeeName());
        return optimaLeaveTrackerRepository.save(optimaLeaveTracker);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting OptimaLeaveTracker by id: {}", id);
        return optimaLeaveTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all OptimaLeaveTracker with search");
        if (searchVM != null && searchVM.getSearch() != null && !searchVM.getSearch().isEmpty()) {
            return optimaLeaveTrackerRepository.findByEmployeeNameContaining(searchVM.getSearch());
        }
        return optimaLeaveTrackerRepository.findAll();
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching OptimaLeaveTracker with pagination");
        if (searchVM != null && searchVM.getSearch() != null && !searchVM.getSearch().isEmpty()) {
            return optimaLeaveTrackerRepository.findByEmployeeNameContaining(searchVM.getSearch());
        }
        return optimaLeaveTrackerRepository.findAll(pageable);
    }

    @Override
    public Object update(String id, OptimaLeaveTrackerVM optimaLeaveTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Updating OptimaLeaveTracker: {}", id);
        OptimaLeaveTracker existing = optimaLeaveTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setLeaveType(optimaLeaveTrackerVM.getLeaveType());
            existing.setStartDate(optimaLeaveTrackerVM.getStartDate());
            existing.setEndDate(optimaLeaveTrackerVM.getEndDate());
            existing.setStatus(optimaLeaveTrackerVM.getStatus());
            existing.setTotalDays(optimaLeaveTrackerVM.getTotalDays());
            return optimaLeaveTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Downloading OptimaLeaveTracker: {}", optimaLeaveTrackerVm.getId());
        return optimaLeaveTrackerRepository.findById(optimaLeaveTrackerVm.getId()).orElse(null);
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        log.info("Getting parameter list for OptimaLeaveTracker");
        return new AdvanceSearchList();
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        log.info("Advance search for OptimaLeaveTracker");
        return optimaLeaveTrackerRepository.findAll(pageable);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting OptimaLeaveTracker: {}", id);
        optimaLeaveTrackerRepository.deleteById(id);
        return id;
    }

    @Override
    public Object dashboard(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Getting dashboard for OptimaLeaveTracker");
        return optimaLeaveTrackerRepository.findAll();
    }
}