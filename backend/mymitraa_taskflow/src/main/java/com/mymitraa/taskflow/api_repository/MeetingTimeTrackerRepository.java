package com.mymitraa.taskflow.api_repository;

import java.util.Date;
import java.util.List;
import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeetingTimeTrackerRepository extends MongoRepository<MeetingTimeTracker, String> {

    MeetingTimeTracker findByMeetingId(String meetingId);

    MeetingTimeTracker findByAttendee(String attendee);

    List<MeetingTimeTracker> findByStatus(String status);

    List<MeetingTimeTracker> findByCheckInTimeAfter(Date date);

    List<MeetingTimeTracker> findByDurationMinutesGreaterThan(Double minutes);
}