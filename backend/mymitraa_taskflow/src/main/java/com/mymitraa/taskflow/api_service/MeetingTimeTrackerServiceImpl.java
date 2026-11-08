package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import com.mymitraa.taskflow.api_repository.MeetingTimeTrackerRepository;
import com.mymitraa.taskflow.api_util.StringUtil;
import com.mymitraa.taskflow.api_vm.MeetingTimeTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MeetingTimeTrackerServiceImpl implements MeetingTimeTrackerService {

    private static final Logger log = LoggerFactory.getLogger(MeetingTimeTrackerServiceImpl.class);

    @Autowired
    private MeetingTimeTrackerRepository meetingTimeTrackerRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public Object save(MeetingTimeTracker meetingTimeTracker, CustomUserDetails customUserDetails) {
        log.info("Saving MeetingTimeTracker: {}", meetingTimeTracker.getMeetingId());
        try {
            meetingTimeTracker.setDeleteFlag(false);
            meetingTimeTracker.setCreatedOn(new Date());
            if (customUserDetails != null) {
                meetingTimeTracker.setCreatedBy(customUserDetails.getUsername());
            }
            if (meetingTimeTracker.getDate() == null) {
                meetingTimeTracker.setDate(new Date());
            }
            return meetingTimeTrackerRepository.save(meetingTimeTracker);
        } catch (Exception ex) {
            log.error("Error saving MeetingTimeTracker: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error saving MeetingTimeTracker: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting MeetingTimeTracker by id: {}", id);
        return meetingTimeTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all MeetingTimeTracker records");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getSearch())) {
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("meeting_by").regex(searchVM.getSearch(), "i"),
                        Criteria.where("project").regex(searchVM.getSearch(), "i"),
                        Criteria.where("customer").regex(searchVM.getSearch(), "i")
                ));
            }
            if (StringUtil.haveValue(searchVM.getCustomer())) {
                query.addCriteria(Criteria.where("customer").is(searchVM.getCustomer()));
            }
            if (StringUtil.haveValue(searchVM.getProject())) {
                query.addCriteria(Criteria.where("project").is(searchVM.getProject()));
            }
        }
        query.with(Sort.by(Sort.Direction.DESC, "date"));
        return mongoOperations.find(query, MeetingTimeTracker.class);
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching MeetingTimeTracker records with pagination");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getSearch())) {
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("meeting_by").regex(searchVM.getSearch(), "i"),
                        Criteria.where("project").regex(searchVM.getSearch(), "i"),
                        Criteria.where("customer").regex(searchVM.getSearch(), "i")
                ));
            }
        }
        if (pageable != null) {
            query.with(pageable);
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "date"));
        }
        return mongoOperations.find(query, MeetingTimeTracker.class);
    }

    @Override
    public Object update(String id, MeetingTimeTrackerVM meetingTimeTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Updating MeetingTimeTracker: {}", id);
        MeetingTimeTracker existing = meetingTimeTrackerRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        if (meetingTimeTrackerVM.getTitle() != null) {
            existing.setMeetingId(meetingTimeTrackerVM.getTitle());
        }
        if (meetingTimeTrackerVM.getDescription() != null) {
            existing.setComments(meetingTimeTrackerVM.getDescription());
        }
        existing.setModifiedDateTime(new Date());
        if (customUserDetails != null) {
            existing.setModifiedBy(customUserDetails.getUsername());
        }
        return meetingTimeTrackerRepository.save(existing);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting MeetingTimeTracker: {}", id);
        MeetingTimeTracker existing = meetingTimeTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDeleteFlag(true);
            existing.setDeletedOn(new Date());
            if (customUserDetails != null) {
                existing.setDeletedBy(customUserDetails.getUsername());
            }
            meetingTimeTrackerRepository.save(existing);
            return true;
        }
        return false;
    }

    @Override
    public Object download(MeetingTimeTrackerVM meetingTimeTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Downloading MeetingTimeTracker records");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        return mongoOperations.find(query, MeetingTimeTracker.class);
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("tableName").is("MeetingTimeTracker"));
        return mongoOperations.findOne(query, AdvanceSearchList.class);
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getParameter()) && StringUtil.haveValue(searchVM.getParametervalue())) {
                query.addCriteria(Criteria.where(searchVM.getParameter()).regex(searchVM.getParametervalue(), "i"));
            }
        }
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoOperations.find(query, MeetingTimeTracker.class);
    }

    @Override
    public Object dashboard(MeetingTimeTrackerVM meetingTimeTrackerVm, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        List<MeetingTimeTracker> list = mongoOperations.find(query, MeetingTimeTracker.class);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalMeetings", list.size());
        summary.put("records", list);
        return summary;
    }
}