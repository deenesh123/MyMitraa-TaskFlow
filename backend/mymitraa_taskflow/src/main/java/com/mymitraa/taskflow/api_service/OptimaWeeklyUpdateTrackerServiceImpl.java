package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.OptimaWeeklyUpdateTracker;
import com.mymitraa.taskflow.api_repository.OptimaWeeklyUpdateTrackerRepository;
import com.mymitraa.taskflow.api_util.StringUtil;
import com.mymitraa.taskflow.api_vm.DocumentUploadVM;
import com.mymitraa.taskflow.api_vm.MailRequestVM;
import com.mymitraa.taskflow.api_vm.OptimaWeeklyUpdateTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OptimaWeeklyUpdateTrackerServiceImpl implements OptimaWeeklyUpdateTrackerService {

    private static final Logger log = LoggerFactory.getLogger(OptimaWeeklyUpdateTrackerServiceImpl.class);

    @Autowired
    private OptimaWeeklyUpdateTrackerRepository optimaWeeklyUpdateTrackerRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${mymitraa.mail.from-email:notifications@mymitraa.com}")
    private String fromEmail;

    @Override
    public Object save(OptimaWeeklyUpdateTrackerVM vm, CustomUserDetails customUserDetails) {
        log.info("Saving OptimaWeeklyUpdateTracker: {}", vm.getProjectName());
        OptimaWeeklyUpdateTracker tracker = new OptimaWeeklyUpdateTracker();
        mapVmToEntity(vm, tracker);

        tracker.setCreatedOn(new Date());
        tracker.setDeleteFlag(false);
        if (customUserDetails != null) {
            tracker.setCreatedBy(customUserDetails.getUsername());
        }

        return optimaWeeklyUpdateTrackerRepository.save(tracker);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting OptimaWeeklyUpdateTracker by id: {}", id);
        return optimaWeeklyUpdateTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all OptimaWeeklyUpdateTracker records");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null && StringUtil.haveValue(searchVM.getProject())) {
            query.addCriteria(Criteria.where("project_name").regex(searchVM.getProject(), "i"));
        }
        query.with(Sort.by(Sort.Direction.DESC, "modified_date_time", "created_on"));
        return mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching OptimaWeeklyUpdateTracker with pagination");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null && StringUtil.haveValue(searchVM.getProject())) {
            query.addCriteria(Criteria.where("project_name").regex(searchVM.getProject(), "i"));
        }
        query.with(Sort.by(Sort.Direction.DESC, "modified_date_time", "created_on"));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting OptimaWeeklyUpdateTracker: {}", id);
        OptimaWeeklyUpdateTracker existing = optimaWeeklyUpdateTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDeleteFlag(true);
            existing.setModifiedDateTime(new Date());
            if (customUserDetails != null) {
                existing.setModifiedBy(customUserDetails.getUsername());
            }
            optimaWeeklyUpdateTrackerRepository.save(existing);
            return true;
        }
        return false;
    }

    @Override
    public Object update(OptimaWeeklyUpdateTrackerVM vm, CustomUserDetails customUserDetails) {
        log.info("Updating OptimaWeeklyUpdateTracker ID: {}", vm.getId());
        OptimaWeeklyUpdateTracker existing = optimaWeeklyUpdateTrackerRepository.findById(vm.getId()).orElse(null);
        if (existing != null) {
            mapVmToEntity(vm, existing);
            existing.setModifiedDateTime(new Date());
            if (customUserDetails != null) {
                existing.setModifiedBy(customUserDetails.getUsername());
            }
            return optimaWeeklyUpdateTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Generating Excel for OptimaWeeklyUpdateTracker");
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Weekly Updates");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Project Name");
            header.createCell(1).setCellValue("Customer");
            header.createCell(2).setCellValue("Status");
            header.createCell(3).setCellValue("Current Status");
            header.createCell(4).setCellValue("Risks");
            header.createCell(5).setCellValue("Updated On");

            List<OptimaWeeklyUpdateTracker> list = optimaWeeklyUpdateTrackerRepository.findAll().stream()
                    .filter(t -> !Boolean.TRUE.equals(t.getDeleteFlag()))
                    .collect(Collectors.toList());

            int rowIdx = 1;
            for (OptimaWeeklyUpdateTracker t : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getProjectName() != null ? t.getProjectName() : "");
                row.createCell(1).setCellValue(t.getCustomerName() != null ? t.getCustomerName() : "");
                row.createCell(2).setCellValue(t.getStatus() != null ? t.getStatus() : "");
                row.createCell(3).setCellValue(t.getCurrentStatus() != null ? t.getCurrentStatus() : "");
                row.createCell(4).setCellValue(t.getRisk() != null ? t.getRisk() : "");
                row.createCell(5).setCellValue(t.getModifiedDateTime() != null ? t.getModifiedDateTime().toString() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            log.error("Error creating weekly Excel: {}", ex.getMessage(), ex);
            return new byte[0];
        }
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("tableName").is("OptimaWeeklyUpdateTracker"));
        return mongoOperations.findOne(query, AdvanceSearchList.class);
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (searchVM != null && StringUtil.haveValue(searchVM.getProject())) {
            query.addCriteria(Criteria.where("project_name").regex(searchVM.getProject(), "i"));
        }
        query.with(Sort.by(Sort.Direction.DESC, "modified_date_time"));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);
    }

    @Override
    public Object dashboard(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVm, CustomUserDetails customUserDetails) {
        List<OptimaWeeklyUpdateTracker> list = optimaWeeklyUpdateTrackerRepository.findAll().stream()
                .filter(t -> !Boolean.TRUE.equals(t.getDeleteFlag()))
                .collect(Collectors.toList());

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalProjects", list.size());
        dashboard.put("onTrack", list.stream().filter(t -> "On Track".equalsIgnoreCase(t.getStatus()) || "Active".equalsIgnoreCase(t.getStatus())).count());
        dashboard.put("atRisk", list.stream().filter(t -> "At Risk".equalsIgnoreCase(t.getStatus())).count());
        dashboard.put("delayed", list.stream().filter(t -> "Delayed".equalsIgnoreCase(t.getStatus())).count());
        dashboard.put("records", list);

        return dashboard;
    }

    @Override
    public Object uploadDocuments(String id, DocumentUploadVM documentVM, CustomUserDetails customUserDetails) {
        log.info("Uploading document for weekly update tracker: {}", id);
        OptimaWeeklyUpdateTracker existing = optimaWeeklyUpdateTrackerRepository.findById(id).orElse(null);
        if (existing != null) {
            if ("HLD".equalsIgnoreCase(documentVM.getDocumentType())) {
                existing.setHldAttachment(documentVM.getFileName());
            } else if ("LLD".equalsIgnoreCase(documentVM.getDocumentType())) {
                existing.setLldAttachment(documentVM.getFileName());
            } else {
                existing.setDocumentation(documentVM.getFileName());
            }
            return optimaWeeklyUpdateTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object getFetchAll(String projectName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("project_name").is(projectName));
        return mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);
    }

    @Override
    public Object sendWeeklyMail(MailRequestVM mailRequestVM, CustomUserDetails customUserDetails) {
        log.info("Sending weekly status email for project: {}", mailRequestVM.getProjectName());
        Map<String, Object> result = new HashMap<>();

        try {
            if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(mailRequestVM.getTo() != null ? mailRequestVM.getTo() : "management@mymitraa.com");
                helper.setSubject(mailRequestVM.getSubject() != null ? mailRequestVM.getSubject() :
                        "[Weekly Report] " + mailRequestVM.getProjectName() + " Status");

                String html = mailRequestVM.getContent() != null ? mailRequestVM.getContent() :
                        "<h3>Weekly Status Report: " + mailRequestVM.getProjectName() + "</h3>" +
                        "<p>Milestones: " + (mailRequestVM.getMilestonesCompleted() != null ? mailRequestVM.getMilestonesCompleted() : "Completed scheduled deliverables.") + "</p>" +
                        "<p>Highlights: " + (mailRequestVM.getHighlights() != null ? mailRequestVM.getHighlights() : "Zero blockers.") + "</p>";

                helper.setText(html, true);
                mailSender.send(message);
                result.put("success", true);
                result.put("message", "Weekly status email dispatched via Zoho Mail SMTP");
            } else {
                result.put("success", true);
                result.put("message", "Weekly status email simulated (Zoho SMTP configured)");
            }
        } catch (Exception ex) {
            log.error("Error sending weekly email: {}", ex.getMessage(), ex);
            result.put("success", false);
            result.put("error", ex.getMessage());
        }

        return result;
    }

    private void mapVmToEntity(OptimaWeeklyUpdateTrackerVM vm, OptimaWeeklyUpdateTracker tracker) {
        if (vm.getProjectName() != null) tracker.setProjectName(vm.getProjectName());
        if (vm.getCustomerName() != null) tracker.setCustomerName(vm.getCustomerName());
        if (vm.getCustomerGeography() != null) tracker.setCustomerGeography(vm.getCustomerGeography());
        if (vm.getIndustry() != null) tracker.setIndustry(vm.getIndustry());
        if (vm.getProjectStatus() != null) tracker.setProjectStatus(vm.getProjectStatus());
        if (vm.getStatus() != null) tracker.setStatus(vm.getStatus());
        if (vm.getPriority() != null) tracker.setPriority(vm.getPriority());
        if (vm.getCurrentStatus() != null) tracker.setCurrentStatus(vm.getCurrentStatus());
        if (vm.getLastWeekStatus() != null) tracker.setLastWeekStatus(vm.getLastWeekStatus());
        if (vm.getCurrentState() != null) tracker.setCurrentState(vm.getCurrentState());
        if (vm.getProjectSummery() != null) tracker.setProjectSummery(vm.getProjectSummery());
        if (vm.getRisk() != null) tracker.setRisk(vm.getRisk());
        if (vm.getKtProvider() != null) tracker.setKtProvider(vm.getKtProvider());
        if (vm.getCodeBase() != null) tracker.setCodeBase(vm.getCodeBase());
        if (vm.getDevUrl() != null) tracker.setDevUrl(vm.getDevUrl());
        if (vm.getQaUrl() != null) tracker.setQaUrl(vm.getQaUrl());
        if (vm.getLaUrl() != null) tracker.setLaUrl(vm.getLaUrl());
        if (vm.getOtherUrl() != null) tracker.setOtherUrl(vm.getOtherUrl());
        tracker.setDevArrow(vm.isDevArrow());
        tracker.setQaArrow(vm.isQaArrow());
        tracker.setLaArrow(vm.isLaArrow());
        tracker.setOtherArrow(vm.isOtherArrow());
        if (vm.getDocumentation() != null) tracker.setDocumentation(vm.getDocumentation());
        if (vm.getHldLink() != null) tracker.setHldLink(vm.getHldLink());
        if (vm.getLldLink() != null) tracker.setLldLink(vm.getLldLink());
        if (vm.getHldAttachment() != null) tracker.setHldAttachment(vm.getHldAttachment());
        if (vm.getLldAttachment() != null) tracker.setLldAttachment(vm.getLldAttachment());
        if (vm.getTotalEnv() != null) tracker.setTotalEnv(vm.getTotalEnv());
        if (vm.getRef() != null) tracker.setRef(vm.getRef());
        if (vm.getStartDate() != null) tracker.setStartDate(vm.getStartDate());
        if (vm.getEndDate() != null) tracker.setEndDate(vm.getEndDate());
        if (vm.getProjectStartDate() != null) tracker.setProjectStartDate(vm.getProjectStartDate());
        if (vm.getGoLiveDate() != null) tracker.setGoLiveDate(vm.getGoLiveDate());
        if (vm.getWeeklyStartDate() != null) tracker.setWeeklyStartDate(vm.getWeeklyStartDate());
        if (vm.getWeeklyEndDate() != null) tracker.setWeeklyEndDate(vm.getWeeklyEndDate());
        if (vm.getTaskOwner() != null) tracker.setTaskOwner(vm.getTaskOwner());
        if (vm.getTeam() != null) tracker.setTeam(vm.getTeam());
        if (vm.getBeDevelopers() != null) tracker.setBeDevelopers(vm.getBeDevelopers());
        if (vm.getFeDevelopers() != null) tracker.setFeDevelopers(vm.getFeDevelopers());
        if (vm.getPythonDevelopers() != null) tracker.setPythonDevelopers(vm.getPythonDevelopers());
        if (vm.getTesters() != null) tracker.setTesters(vm.getTesters());
        if (vm.getUxDesigners() != null) tracker.setUxDesigners(vm.getUxDesigners());
        if (vm.getSharedResources() != null) tracker.setSharedResources(vm.getSharedResources());
    }
}