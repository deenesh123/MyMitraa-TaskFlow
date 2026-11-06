package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.LeaveRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {

    LeaveRequest findByLeaveType(String leaveType);
}