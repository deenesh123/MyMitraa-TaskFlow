package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.OptimaLeaveTracker;
import com.mymitraa.taskflow.api_repository.OptimaLeaveTrackerRepository;
import com.mymitraa.taskflow.api_util.StringUtil;
import com.mymitraa.taskflow.api_vm.OptimaLeaveTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OptimaLeaveTrackerServiceImpl implements OptimaLeaveTrackerService {

    private static final Logger log = LoggerFactory.getLogger(OptimaLeaveTrackerServiceImpl.class);

    @Autowired
    private OptimaLeaveTrackerRepository optimaLeaveTrackerRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public Object save(OptimaLeaveTracker optimaLeaveTracker, CustomUserDetails customUserDetails) {
        log.info("Saving OptimaLeaveTracker: {}", optimaLeaveTracker.getName());
        try {
            if (optimaLeaveTracker.getStartDate() != null) {
                LocalDate localDate = optimaLeaveTracker.getStartDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                // set status or comment if applicable
            }
            optimaLeaveTracker.setDeleteFlag(false);
            optimaLeaveTracker.setCreatedOn(new Date());
            if (customUserDetails != null) {
                optimaLeaveTracker.setCreatedBy(customUserDetails.getUsername());
            }

            return optimaLeaveTrackerRepository.save(optimaLeaveTracker);
        } catch (Exception ex) {
            log.error("Error saving OptimaLeaveTracker: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error saving OptimaLeaveTracker: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting OptimaLeaveTracker by ID: {}", id);
        return optimaLeaveTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all OptimaLeaveTracker records");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getSearch())) {
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("name").regex(searchVM.getSearch(), "i"),
                        Criteria.where("leave_type").regex(searchVM.getSearch(), "i")
                ));
            }
        }
        query.with(Sort.by(Sort.Direction.DESC, "start_date"));
        return mongoOperations.find(query, OptimaLeaveTracker.class);
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching OptimaLeaveTracker records with pagination");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null && StringUtil.haveValue(searchVM.getSearch())) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(searchVM.getSearch(), "i"),
                    Criteria.where("leave_type").regex(searchVM.getSearch(), "i")
            ));
        }
        query.with(Sort.by(Sort.Direction.DESC, "start_date"));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoOperations.find(query, OptimaLeaveTracker.class);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting OptimaLeaveTracker ID: {}", id);
        OptimaLeaveTracker existing = optimaLeaveTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDeleteFlag(true);
            existing.setModifiedDateTime(new Date());
            if (customUserDetails != null) {
                existing.setModifiedBy(customUserDetails.getUsername());
            }
            optimaLeaveTrackerRepository.save(existing);
            return true;
        }
        return false;
    }

    @Override
    public Object update(String id, OptimaLeaveTrackerVM optimaLeaveTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Updating OptimaLeaveTracker ID: {}", id);
        OptimaLeaveTracker existing = optimaLeaveTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            if (optimaLeaveTrackerVM.getName() != null) existing.setName(optimaLeaveTrackerVM.getName());
            if (optimaLeaveTrackerVM.getRole() != null) existing.setRole(optimaLeaveTrackerVM.getRole());
            if (optimaLeaveTrackerVM.getLeaveType() != null) existing.setLeaveType(optimaLeaveTrackerVM.getLeaveType());
            if (optimaLeaveTrackerVM.getStartDate() != null) existing.setStartDate(optimaLeaveTrackerVM.getStartDate());
            if (optimaLeaveTrackerVM.getEndDate() != null) existing.setEndDate(optimaLeaveTrackerVM.getEndDate());
            if (optimaLeaveTrackerVM.getStatus() != null) existing.setStatus(optimaLeaveTrackerVM.getStatus());
            if (optimaLeaveTrackerVM.getTotalDays() != null) existing.setTotalDays(optimaLeaveTrackerVM.getTotalDays());
            if (optimaLeaveTrackerVM.getComments() != null) existing.setComments(optimaLeaveTrackerVM.getComments());

            existing.setModifiedDateTime(new Date());
            if (customUserDetails != null) {
                existing.setModifiedBy(customUserDetails.getUsername());
            }
            return optimaLeaveTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Generating Excel for OptimaLeaveTracker");
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Leave Tracker");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Employee Name");
            header.createCell(1).setCellValue("Role");
            header.createCell(2).setCellValue("Leave Type");
            header.createCell(3).setCellValue("Start Date");
            header.createCell(4).setCellValue("End Date");
            header.createCell(5).setCellValue("Status");
            header.createCell(6).setCellValue("Comments");

            List<OptimaLeaveTracker> list = optimaLeaveTrackerRepository.findAll().stream()
                    .filter(l -> !Boolean.TRUE.equals(l.getDeleteFlag()))
                    .collect(Collectors.toList());

            int rowIdx = 1;
            for (OptimaLeaveTracker l : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(l.getName() != null ? l.getName() : "");
                row.createCell(1).setCellValue(l.getRole() != null ? l.getRole() : "");
                row.createCell(2).setCellValue(l.getLeaveType() != null ? l.getLeaveType() : "");
                row.createCell(3).setCellValue(l.getStartDate() != null ? l.getStartDate().toString() : "");
                row.createCell(4).setCellValue(l.getEndDate() != null ? l.getEndDate().toString() : "");
                row.createCell(5).setCellValue(l.getStatus() != null ? l.getStatus() : "");
                row.createCell(6).setCellValue(l.getComments() != null ? l.getComments() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            log.error("Error creating leave Excel: {}", ex.getMessage(), ex);
            return new byte[0];
        }
    }

    @Override
    public Object getParamterList(CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("tableName").is("OptimaLeaveTracker"));
        return mongoOperations.findOne(query, AdvanceSearchList.class);
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getSearch())) {
                query.addCriteria(Criteria.where("name").regex(searchVM.getSearch(), "i"));
            }
        }
        query.with(Sort.by(Sort.Direction.DESC, "start_date"));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoOperations.find(query, OptimaLeaveTracker.class);
    }

    @Override
    public Object dashboard(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails) {
        List<OptimaLeaveTracker> list = optimaLeaveTrackerRepository.findAll().stream()
                .filter(l -> !Boolean.TRUE.equals(l.getDeleteFlag()))
                .collect(Collectors.toList());

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalLeaves", list.size());
        summary.put("wfhCount", list.stream().filter(l -> "WFH".equalsIgnoreCase(l.getLeaveType())).count());
        summary.put("vacationCount", list.stream().filter(l -> "Vacation".equalsIgnoreCase(l.getLeaveType()) || "Planned".equalsIgnoreCase(l.getLeaveType())).count());
        summary.put("sickLeaveCount", list.stream().filter(l -> "Sick Leave".equalsIgnoreCase(l.getLeaveType()) || "Unplanned".equalsIgnoreCase(l.getLeaveType())).count());
        summary.put("records", list);

        return summary;
    }
}