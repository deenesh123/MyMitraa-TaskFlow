package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MeetingTimeTrackerRepository extends MongoRepository<MeetingTimeTracker, String> {

    MeetingTimeTracker findByDeleteFlagAndId(Boolean deleteFlag, String id);

    List<MeetingTimeTracker> findByDeleteFlagOrderByModifiedDateTimeDesc(Boolean deleteFlag);

    Page<MeetingTimeTracker> findByDeleteFlagOrderByModifiedDateTimeDesc(Boolean deleteFlag, Pageable pageable);

    List<MeetingTimeTracker> findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
            Boolean deleteFlag, Date startDate, Date endDate);

    Page<MeetingTimeTracker> findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
            Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    MeetingTimeTracker findByMeetingId(String meetingId);

    List<MeetingTimeTracker> findByProject(String project);

    List<MeetingTimeTracker> findByMeetingBy(String meetingBy);

    List<MeetingTimeTracker> findByStatus(String status);
}