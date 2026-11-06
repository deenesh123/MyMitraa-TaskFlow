package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.OptimaWeeklyUpdateTracker;
import com.mymitraa.taskflow.api_repository.OptimaWeeklyUpdateTrackerRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import com.mymitraa.taskflow.api_vm.OptimaWeeklyUpdateTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OptimaWeeklyUpdateTrackerServiceImpl implements OptimaWeeklyUpdateTrackerService {

    private static final Logger log = LoggerFactory.getLogger(OptimaWeeklyUpdateTrackerServiceImpl.class);

    @Autowired
    private OptimaWeeklyUpdateTrackerRepository optimaWeeklyUpdateTrackerRepository;

    @Override
    public Object save(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Saving OptimaWeeklyUpdateTracker: {}", optimaWeeklyUpdateTrackerVM.getProjectName());
        OptimaWeeklyUpdateTracker tracker = new OptimaWeeklyUpdateTracker();
        tracker.setProjectName(optimaWeeklyUpdateTrackerVM.getProjectName());
        tracker.setUpdateTitle(optimaWeeklyUpdateTrackerVM.getUpdateTitle());
        tracker.setUpdateDescription(optimaWeeklyUpdateTrackerVM.getUpdateDescription());
        tracker.setUpdateDate(new java.util.Date());
        tracker.setStatus("SAVED");
        tracker.setWeekNumber(optimaWeeklyUpdateTrackerVM.getWeekNumber());
        tracker.setYear(optimaWeeklyUpdateTrackerVM.getYear());
        return optimaWeeklyUpdateTrackerRepository.save(tracker);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting OptimaWeeklyUpdateTracker by id: {}", id);
        return optimaWeeklyUpdateTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all OptimaWeeklyUpdateTracker with search: {}", searchVM.getSearch());
        String search = searchVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return optimaWeeklyUpdateTrackerRepository.findByProjectNameContaining(search);
        }
        return optimaWeeklyUpdateTrackerRepository.findAll();
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching OptimaWeeklyUpdateTracker with pagination and search: {}", searchVM.getSearch());
        String search = searchVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return optimaWeeklyUpdateTrackerRepository.findByProjectNameContaining(search);
        }
        return optimaWeeklyUpdateTrackerRepository.findAll(pageable);
    }

    @Override
    public Object update(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Updating OptimaWeeklyUpdateTracker: {}", optimaWeeklyUpdateTrackerVM.getId());
        OptimaWeeklyUpdateTracker existing = optimaWeeklyUpdateTrackerRepository.findById(optimaWeeklyUpdateTrackerVM.getId()).orElse(null);
        if (existing != null) {
            existing.setProjectName(optimaWeeklyUpdateTrackerVM.getProjectName());
            existing.setUpdateTitle(optimaWeeklyUpdateTrackerVM.getUpdateTitle());
            existing.setUpdateDescription(optimaWeeklyUpdateTrackerVM.getUpdateDescription());
            existing.setStatus("UPDATED");
            return optimaWeeklyUpdateTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Downloading OptimaWeeklyUpdateTracker: {}", optimaWeeklyUpdateTrackerVm.getId());
        return optimaWeeklyUpdateTrackerRepository.findById(optimaWeeklyUpdateTrackerVm.getId()).orElse(null);
    }

    @Override
    public Object getParamterList(CustomUserDetails customUserDetails) {
        log.info("Getting parameter list for OptimaWeeklyUpdateTracker");
        return java.util.Collections.emptyList();
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        log.info("Advance search for OptimaWeeklyUpdateTracker");
        return optimaWeeklyUpdateTrackerRepository.findAll(pageable);
    }

    @Override
    public Object dashboard(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Getting dashboard for OptimaWeeklyUpdateTracker");
        return optimaWeeklyUpdateTrackerRepository.findAll();
    }
}