package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.LeaveRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {

    List<LeaveRequest> findByLeaveType(String leaveType);
    List<LeaveRequest> findByRequestedBy(String requestedBy);
}