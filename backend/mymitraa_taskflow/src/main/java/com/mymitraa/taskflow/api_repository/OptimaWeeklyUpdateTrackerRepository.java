package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.OptimaWeeklyUpdateTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OptimaWeeklyUpdateTrackerRepository extends MongoRepository<OptimaWeeklyUpdateTracker, String> {

    OptimaWeeklyUpdateTracker findByDeleteFlagAndId(Boolean deleteFlag, String id);

    List<OptimaWeeklyUpdateTracker> findByDeleteFlagOrderByModifiedDateTimeDesc(Boolean deleteFlag);

    Page<OptimaWeeklyUpdateTracker> findByDeleteFlagOrderByModifiedDateTimeDesc(Boolean deleteFlag, Pageable pageable);

    List<OptimaWeeklyUpdateTracker> findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
            Boolean deleteFlag, Date startDate, Date endDate);

    Page<OptimaWeeklyUpdateTracker> findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
            Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    OptimaWeeklyUpdateTracker findByProjectName(String projectName);

    List<OptimaWeeklyUpdateTracker> findByProjectNameAndDeleteFlag(String projectName, Boolean deleteFlag);

    List<OptimaWeeklyUpdateTracker> findByStatus(String status);
}