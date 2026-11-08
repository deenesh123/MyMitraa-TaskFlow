package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.TimelineEntry;
import com.mymitraa.taskflow.api_repository.TimelineEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TimelineEntryServiceImpl implements TimelineEntryService {

    private static final Logger log = LoggerFactory.getLogger(TimelineEntryServiceImpl.class);

    @Autowired
    private TimelineEntryRepository timelineEntryRepository;

    @Override
    public Object save(TimelineEntry obj) {
        log.info("Saving timeline entry: {}", obj.getTitle());
        obj.setDeleteFlag(false);
        if (obj.getCreatedOn() == null) {
            obj.setCreatedOn(new Date());
        }
        if (obj.getEventDate() == null) {
            obj.setEventDate(new Date());
        }
        return timelineEntryRepository.save(obj);
    }

    @Override
    public Object fetchAll(Pageable pageable) {
        log.info("Fetching timeline entries");
        if (pageable != null) {
            return timelineEntryRepository.findAll(pageable);
        }
        return timelineEntryRepository.findAll();
    }

    @Override
    public Object getById(String id) {
        log.info("Fetching timeline entry by id: {}", id);
        return timelineEntryRepository.findById(id).orElse(null);
    }

    @Override
    public Object update(TimelineEntry obj) {
        log.info("Updating timeline entry: {}", obj.getId());
        obj.setModifiedDateTime(new Date());
        return timelineEntryRepository.save(obj);
    }

    @Override
    public Object delete(String id) {
        log.info("Deleting timeline entry: {}", id);
        TimelineEntry entry = timelineEntryRepository.findById(id).orElse(null);
        if (entry != null) {
            entry.setDeleteFlag(true);
            entry.setDeletedOn(new Date());
            timelineEntryRepository.save(entry);
            return true;
        }
        return false;
    }

    @Override
    public Object fetchByRelatedEntity(String entityType, String entityId) {
        log.info("Fetching timeline entries by entity: {} / {}", entityType, entityId);
        return timelineEntryRepository.findByRelatedEntityTypeAndRelatedEntityId(entityType, entityId);
    }

    @Override
    public Object fetchByDateRange(Date start, Date end) {
        log.info("Fetching timeline entries by date range");
        return timelineEntryRepository.findByEventDateBetween(start, end);
    }
}
