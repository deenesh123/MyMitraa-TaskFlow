package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.LeaveRequest;
import com.mymitraa.taskflow.api_repository.LeaveRequestRepository;
import com.mymitraa.taskflow.api_vm.LeaveRequestVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;

public interface LeaveRequestService {

    Object save(LeaveRequest obj, CustomUserDetails customUserDetails);

    Object fetchAll(String username, Pageable pageable);

    Object getById(String id);

    Object update(LeaveRequest obj, CustomUserDetails customUserDetails);

    Object delete(String id);

    Object download(LeaveRequestVM obj);
}