package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.TimelineEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimelineEntryRepository extends MongoRepository<TimelineEntry, String> {

    List<TimelineEntry> findByRelatedEntityType(String relatedEntityType);
    List<TimelineEntry> findByEventDateBetween(LocalDateTime start, LocalDateTime end);
}