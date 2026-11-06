package com.mymitraa.taskflow.api_repository;

import java.util.Date;
import java.util.List;
import com.mymitraa.taskflow.api_domain.OptimaWeeklyUpdateTracker;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OptimaWeeklyUpdateTrackerRepository extends MongoRepository<OptimaWeeklyUpdateTracker, String> {

    OptimaWeeklyUpdateTracker findByProjectName(String projectName);

    List<OptimaWeeklyUpdateTracker> findByProjectNameContaining(String projectName);

    OptimaWeeklyUpdateTracker findByStatus(String status);

    List<OptimaWeeklyUpdateTracker> findByUpdatedBy(String updatedBy);

    List<OptimaWeeklyUpdateTracker> findByWeekNumberAndYear(Integer weekNumber, Integer year);

    List<OptimaWeeklyUpdateTracker> findByUpdateDateBetween(Date start, Date end);

    List<OptimaWeeklyUpdateTracker> findByStatusAndProjectName(String status, String projectName);

    List<OptimaWeeklyUpdateTracker> findByUpdatedDateAfter(Date date);
}