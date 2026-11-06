package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.OptimaLeaveTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OptimaLeaveTrackerRepository extends MongoRepository<OptimaLeaveTracker, String> {

    OptimaLeaveTracker findByDeleteFlagAndId(Boolean deleteFlag, String id);

    List<OptimaLeaveTracker> findByDeleteFlagOrderByModifiedDateTimeDesc(Boolean deleteFlag);

    Page<OptimaLeaveTracker> findByDeleteFlagOrderByModifiedDateTimeDesc(Boolean deleteFlag, Pageable pageable);

    List<OptimaLeaveTracker> findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
            Boolean deleteFlag, Date startDate, Date endDate);

    Page<OptimaLeaveTracker> findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
            Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    List<OptimaLeaveTracker> findByName(String name);

    List<OptimaLeaveTracker> findByLeaveType(String leaveType);

    List<OptimaLeaveTracker> findByStartDateBetween(Date start, Date end);
}