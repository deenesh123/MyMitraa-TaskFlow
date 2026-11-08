package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.LeaveRequest;
import com.mymitraa.taskflow.api_repository.LeaveRequestRepository;
import com.mymitraa.taskflow.api_vm.LeaveRequestVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final Logger log = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public Object save(LeaveRequest obj, CustomUserDetails customUserDetails) {
        log.info("Saving LeaveRequest: {}", obj.getLeaveType());
        obj.setDeleteFlag(false);
        if (obj.getCreatedOn() == null) {
            obj.setCreatedOn(new Date());
        }
        if (customUserDetails != null) {
            obj.setCreatedBy(customUserDetails.getUsername());
            if (obj.getRequestedBy() == null) {
                obj.setRequestedBy(customUserDetails.getUsername());
            }
        }
        return leaveRequestRepository.save(obj);
    }

    @Override
    public Object fetchAll(String username, Pageable pageable) {
        log.info("Fetching LeaveRequests for: {}", username);
        if (pageable != null) {
            return leaveRequestRepository.findAll(pageable);
        }
        return leaveRequestRepository.findAll();
    }

    @Override
    public Object getById(String id) {
        log.info("Fetching LeaveRequest by ID: {}", id);
        return leaveRequestRepository.findById(id).orElse(null);
    }

    @Override
    public Object update(LeaveRequest obj, CustomUserDetails customUserDetails) {
        log.info("Updating LeaveRequest: {}", obj.getId());
        obj.setModifiedDateTime(new Date());
        if (customUserDetails != null) {
            obj.setModifiedBy(customUserDetails.getUsername());
        }
        return leaveRequestRepository.save(obj);
    }

    @Override
    public Object delete(String id) {
        log.info("Deleting LeaveRequest: {}", id);
        LeaveRequest req = leaveRequestRepository.findById(id).orElse(null);
        if (req != null) {
            req.setDeleteFlag(true);
            req.setDeletedOn(new Date());
            leaveRequestRepository.save(req);
            return true;
        }
        return false;
    }

    @Override
    public Object download(LeaveRequestVM obj) {
        log.info("Downloading LeaveRequests");
        return leaveRequestRepository.findAll();
    }
}
