package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.TimelineEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineEntryRepository extends JpaRepository<TimelineEntry, Long> {

    java.util.List<TimelineEntry> findByRelatedEntityTypeAndRelatedEntityType(String relatedEntityType, Long relatedEntityId);
    java.util.List<TimelineEntry> findByEventDateBetween(LocalDateTime start, LocalDateTime end);
}