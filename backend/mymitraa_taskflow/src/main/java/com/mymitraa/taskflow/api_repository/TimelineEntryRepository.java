package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.TimelineEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Date;
import java.util.List;

public interface TimelineEntryRepository extends MongoRepository<TimelineEntry, String> {

    List<TimelineEntry> findByRelatedEntityTypeAndRelatedEntityId(String relatedEntityType, String relatedEntityId);
    List<TimelineEntry> findByEventDateBetween(Date start, Date end);
}