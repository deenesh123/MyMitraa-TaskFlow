package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.ProjectStatus;
import com.mymitraa.taskflow.api_repository.ProjectStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProjectStatusServiceImpl implements ProjectStatusService {

    private static final Logger log = LoggerFactory.getLogger(ProjectStatusServiceImpl.class);

    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Override
    public Object save(ProjectStatus obj) {
        log.info("Saving ProjectStatus: {}", obj.getTitle());
        obj.setDeleteFlag(false);
        if (obj.getCreatedOn() == null) {
            obj.setCreatedOn(new Date());
        }
        return projectStatusRepository.save(obj);
    }

    @Override
    public Object fetchAll(Pageable pageable) {
        log.info("Fetching ProjectStatuses");
        if (pageable != null) {
            return projectStatusRepository.findAll(pageable);
        }
        return projectStatusRepository.findAll();
    }

    @Override
    public Object getById(String id) {
        log.info("Fetching ProjectStatus by ID: {}", id);
        return projectStatusRepository.findById(id).orElse(null);
    }

    @Override
    public Object update(ProjectStatus obj) {
        log.info("Updating ProjectStatus: {}", obj.getId());
        obj.setModifiedDateTime(new Date());
        return projectStatusRepository.save(obj);
    }

    @Override
    public Object delete(String id) {
        log.info("Deleting ProjectStatus: {}", id);
        ProjectStatus ps = projectStatusRepository.findById(id).orElse(null);
        if (ps != null) {
            ps.setDeleteFlag(true);
            ps.setDeletedOn(new Date());
            projectStatusRepository.save(ps);
            return true;
        }
        return false;
    }
}
