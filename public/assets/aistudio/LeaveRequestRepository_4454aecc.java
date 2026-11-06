package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    LeaveRequest findByLeaveType(String leaveType);
}