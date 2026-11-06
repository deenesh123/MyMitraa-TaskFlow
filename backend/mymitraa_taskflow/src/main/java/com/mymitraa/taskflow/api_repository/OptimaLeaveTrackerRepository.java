package com.mymitraa.taskflow.api_repository;

import java.util.Date;
import java.util.List;
import com.mymitraa.taskflow.api_domain.OptimaLeaveTracker;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OptimaLeaveTrackerRepository extends MongoRepository<OptimaLeaveTracker, String> {

    OptimaLeaveTracker findByLeaveType(String leaveType);

    OptimaLeaveTracker findByStatus(String status);

    List<OptimaLeaveTracker> findByEmployeeName(String employeeName);

    List<OptimaLeaveTracker> findByEmployeeNameContaining(String employeeName);

    List<OptimaLeaveTracker> findByStartDateBetween(Date start, Date end);

    List<OptimaLeaveTracker> findByTotalDaysGreaterThan(Double days);

    List<OptimaLeaveTracker> findByApprovedBy(String approvedBy);

    List<OptimaLeaveTracker> findByCommentsContaining(String comments);
}