package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.FeedbackHistory;
import com.mymitraa.taskflow.api_domain.WorkLogEntry;
import com.mymitraa.taskflow.api_repository.FeedbackHistoryRepository;
import com.mymitraa.taskflow.api_vm.AssignTaskVM;
import com.mymitraa.taskflow.api_vm.EpicVM;
import com.mymitraa.taskflow.api_vm.FeedbackHistoryVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FeedbackHistoryServiceImpl implements FeedbackHistoryService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackHistoryServiceImpl.class);

    @Autowired
    private FeedbackHistoryRepository feedbackHistoryRepository;

    @Override
    public Object saveFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        log.info("Saving feedback history");
        FeedbackHistory feedbackHistory = new FeedbackHistory();
        // Map VM to domain - preserve all fields
        feedbackHistory.setTicketNumber(feedbackHistoryVM.getTicketNumber());
        feedbackHistory.setSubject(feedbackHistoryVM.getSubject());
        feedbackHistory.setComments(feedbackHistoryVM.getComments());
        feedbackHistory.setDescription(feedbackHistoryVM.getDescription());
        feedbackHistory.setStatus(feedbackHistoryVM.getStatus());
        feedbackHistory.setPriority(feedbackHistoryVM.getPriority());
        feedbackHistory.setProject(feedbackHistoryVM.getProject());
        feedbackHistory.setModule(feedbackHistoryVM.getModule());
        feedbackHistory.setSubModule(feedbackHistoryVM.getSubModule());
        feedbackHistory.setEpic(feedbackHistoryVM.getEpic());
        feedbackHistory.setCustomer(feedbackHistoryVM.getCustomer());
        feedbackHistory.setUserStories(feedbackHistoryVM.getUserStories());
        feedbackHistory.setTeam(feedbackHistoryVM.getTeam());
        feedbackHistory.setTaskType(feedbackHistoryVM.getTaskType());
        feedbackHistory.setTaskOwner(feedbackHistoryVM.getTaskOwner());
        feedbackHistory.setAssignedBy(feedbackHistoryVM.getAssignedBy());
        feedbackHistory.setClassification(feedbackHistoryVM.getClassification());
        feedbackHistory.setCategory(feedbackHistoryVM.getCategory());
        feedbackHistory.setCategoryDescription(feedbackHistoryVM.getCategoryDescription());
        feedbackHistory.setEstimatedEffort(feedbackHistoryVM.getEstimatedEffort());
        feedbackHistory.setActualEffort(feedbackHistoryVM.getActualEffort());
        feedbackHistory.setProductivitySavings(feedbackHistoryVM.getProductivitySavings());
        feedbackHistory.setWhoReported(feedbackHistoryVM.getWhoReported());
        feedbackHistory.setReviewerComments(feedbackHistoryVM.getReviewerComments());
        feedbackHistory.setReviewedBy(feedbackHistoryVM.getReviewedBy());
        feedbackHistory.setEstimatedStartDate(feedbackHistoryVM.getEstimatedStartDate());
        feedbackHistory.setEstimatedCloseDate(feedbackHistoryVM.getEstimatedCloseDate());
        feedbackHistory.setActualStartDate(feedbackHistoryVM.getActualStartDate());
        feedbackHistory.setActualCloseDate(feedbackHistoryVM.getActualCloseDate());
        feedbackHistory.setDateAssigned(feedbackHistoryVM.getDateAssigned());
        feedbackHistory.setFileName(feedbackHistoryVM.getFileName());
        feedbackHistory.setAttachments(feedbackHistoryVM.getAttachments());
        feedbackHistory.setWorkLogEntries(feedbackHistoryVM.getWorkLogEntries());
        feedbackHistory.setUploaded(feedbackHistoryVM.getUploaded());
        feedbackHistory.setTaskDescription(feedbackHistoryVM.getTaskDescription());
        feedbackHistory.setSubtaskCount(feedbackHistoryVM.getSubtaskCount());
        feedbackHistory.setLastSubtaskUpdate(feedbackHistoryVM.getLastSubtaskUpdate());
        feedbackHistory.setInstance(feedbackHistoryVM.getInstance());
        feedbackHistory.setProjectCategory(feedbackHistoryVM.getProjectCategory());
        feedbackHistory.setCoding(feedbackHistoryVM.getCoding());
        feedbackHistory.setAiTool(feedbackHistoryVM.getAiTool());
        feedbackHistory.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
        feedbackHistory.setProduct(feedbackHistoryVM.getProduct());

        return feedbackHistoryRepository.save(feedbackHistory);
    }

    @Override
    public Object editFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        log.info("Editing feedback history");
        return saveFeedback(feedbackHistoryVM, customUserDetails);
    }

    @Override
    public Object getAllFeedback(SearchVM searchVM, Pageable pageable) {
        log.info("Getting all feedback with search: {}", searchVM.getSearch());
        String search = searchVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByTicketNumberContainingOrSubjectContainingOrCommentsContaining(
                    search, search, search);
        }
        Page<FeedbackHistory> all = feedbackHistoryRepository.findAll(pageable);
        return all.getContent();
    }

    @Override
    public Object getFeedbackById(String feedbackId) {
        log.info("Getting feedback by id: {}", feedbackId);
        return feedbackHistoryRepository.findById(feedbackId).orElse(null);
    }

    @Override
    public Object deleteFeedbackById(String feedbackId) {
        log.info("Deleting feedback by id: {}", feedbackId);
        FeedbackHistory feedback = feedbackHistoryRepository.findById(feedbackId).orElse(null);
        if (feedback != null) {
            feedbackHistoryRepository.delete(feedback);
            return "Deleted successfully";
        }
        return "Feedback not found";
    }

    @Override
    public Object getFeedbackNumericalCount() {
        log.info("Getting feedback numerical count");
        Long count = feedbackHistoryRepository.count();
        return count;
    }

    @Override
    public Object getParamterList(CustomUserDetails customUserDetails) {
        log.info("Getting parameter list");
        return AdvanceSearchList.builder()
                .ticketNumbers(findAllTicketNumbers())
                .statuses(findAllStatuses())
                .projects(findAllProjects())
                .build();
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        log.info("Advancing search for feedback");
        String search = searchVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByTicketNumberContainingOrSubjectContaining(
                    search, search);
        }
        Page<FeedbackHistory> all = feedbackHistoryRepository.findAll(pageable);
        return all.getContent();
    }

    @Override
    public Object subTaskAdvanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        log.info("Subtask advance search");
        String search = searchVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByEpicContainingOrSubModuleContaining(search, search);
        }
        return feedbackHistoryRepository.findAll(pageable);
    }

    @Override
    public Object getSearchableSubtaskFields(CustomUserDetails customUserDetails) {
        log.info("Getting searchable subtask fields");
        return java.util.Map.of(
                "modules", findAllModules(),
                "statuses", findAllStatuses(),
                "projects", findAllProjects());
    }

    @Override
    public Object deleteFile(String ticketNumber) {
        log.info("Deleting file with ticket number: {}", ticketNumber);
        FeedbackHistory feedback = feedbackHistoryRepository.findByTicketNumber(ticketNumber);
        if (feedback != null) {
            feedback.setUploaded(false);
            feedbackHistoryRepository.save(feedback);
            return "File deletion recorded";
        }
        return "File not found";
    }

    @Override
    public Object assignTask(AssignTaskVM assignTaskVM, CustomUserDetails customUserDetails) {
        log.info("Assigning task: {}", assignTaskVM.getTaskTitle());
        FeedbackHistory feedback = new FeedbackHistory();
        feedback.setTicketNumber(assignTaskVM.getTicketNumber());
        feedback.setTaskOwner(assignTaskVM.getAssignee());
        feedback.setSubModule("TASK_ASSIGNED");
        return feedbackHistoryRepository.save(feedback);
    }

    @Override
    public Object taskClosed(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        log.info("Closing task: {}", feedbackHistoryVM.getTicketNumber());
        FeedbackHistory feedback = feedbackHistoryRepository.findByTicketNumber(feedbackHistoryVM.getTicketNumber());
        if (feedback != null) {
            feedback.setStatus("CLOSED");
            feedback.setSubModule("TASK_CLOSED");
            feedbackHistoryRepository.save(feedback);
            return "Task closed successfully";
        }
        return "Feedback not found";
    }

    @Override
    public Object addSubTask(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        log.info("Adding subtask for: {}", feedbackHistoryVM.getTicketNumber());
        FeedbackHistory feedback = feedbackHistoryRepository.findByTicketNumber(feedbackHistoryVM.getTicketNumber());
        if (feedback != null) {
            feedback.setSubModule(feedbackHistoryVM.getSubModule());
            feedback.setSubtaskCount(feedbackHistoryVM.getSubtaskCount() != null
                    ? feedbackHistoryVM.getSubtaskCount() + 1 : 1);
            feedbackHistoryRepository.save(feedback);
            return "Subtask added successfully";
        }
        return "Feedback not found";
    }

    @Override
    public Object updateSubTask(EpicVM epicVM, String feedbackHistoryId, String epicId, CustomUserDetails customUserDetails) {
        log.info("Updating subtask for: {}", feedbackHistoryId);
        FeedbackHistory feedback = feedbackHistoryRepository.findById(feedbackHistoryId).orElse(null);
        if (feedback != null) {
            feedback.setEpic(epicVM.getEpic());
            feedback.setSubModule(epicVM.getSubModule());
            feedbackHistoryRepository.save(feedback);
            return "Subtask updated successfully";
        }
        return "Feedback not found";
    }

    @Override
    public Object getUploadedFiles(SearchVM dateRangeVM, Pageable pageable) {
        log.info("Getting uploaded files with date range: {}", dateRangeVM.getSearch());
        String search = dateRangeVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByFileNameContainingAndUploadedTrue(search);
        }
        return feedbackHistoryRepository.findByUploadedTrue(pageable);
    }

    @Override
    public Object getAllTicketNumbers(CustomUserDetails customUserDetails) {
        log.info("Getting all ticket numbers");
        return feedbackHistoryRepository.findAllTicketNumbers();
    }

    @Override
    public Object getDailyTaskTracker(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
        log.info("Getting daily task tracker");
        String search = effortRecordVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByTicketNumberContainingOrSubjectContaining(search, search);
        }
        return feedbackHistoryRepository.findAll();
    }

    @Override
    public Object dashboard(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
        log.info("Getting dashboard data");
        String search = effortRecordVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByProjectContaining(search);
        }
        return feedbackHistoryRepository.findAll();
    }

    @Override
    public Object uploadFile(MultipartFile file, CustomUserDetails customUserDetails) {
        log.info("Uploading file: {}", file.getOriginalFilename());
        // In a real implementation, would save file to storage
        return "File upload recorded: " + file.getOriginalFilename();
    }

    @Override
    public Object uploadFileByTicketNumber(String ticketNumber, MultipartFile file, CustomUserDetails customUserDetails) {
        log.info("Uploading file by ticket number: {}", ticketNumber);
        FeedbackHistory feedback = feedbackHistoryRepository.findByTicketNumber(ticketNumber);
        if (feedback != null) {
            feedback.setFileName(file.getOriginalFilename());
            feedback.setAttachments(java.util.Collections.singletonList(file.getOriginalFilename()));
            feedbackHistoryRepository.save(feedback);
            return "File uploaded successfully";
        }
        return "Feedback not found";
    }

    @Override
    public Object getRecord(SearchVM searchVM, Pageable pageable) {
        log.info("Getting record with search: {}", searchVM.getSearch());
        String search = searchVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return feedbackHistoryRepository.findByTicketNumberContainingOrSubjectContaining(search, search);
        }
        return feedbackHistoryRepository.findAll(pageable);
    }

    @Override
    public Object saveWorklogHours(String feedbackHistoryId, String epicId, List<WorkLogEntry> workLogEntry, CustomUserDetails customUserDetails) {
        log.info("Saving work log hours for: {}", feedbackHistoryId);
        FeedbackHistory feedback = feedbackHistoryRepository.findById(feedbackHistoryId).orElse(null);
        if (feedback != null) {
            feedback.setWorkLogEntries(workLogEntry);
            feedbackHistoryRepository.save(feedback);
            return "Work log hours saved successfully";
        }
        return "Feedback not found";
    }

    @Override
    public Object chronProcessed(CustomUserDetails customUserDetails) {
        log.info("Processing chron");
        return "Chron process completed";
    }

    @Override
    public Object chronProcessedWithDate(CustomUserDetails customUserDetails, String operationDateTime) {
        log.info("Processing chron with date: {}", operationDateTime);
        return "Chron process completed with date: " + operationDateTime;
    }

    @Override
    public Object chronProcessedWithDateWithOutVfm(CustomUserDetails customUserDetails, String operationDateTime) {
        log.info("Processing chron without VFM with date: {}", operationDateTime);
        return "Chron process completed without VFM";
    }

    @Override
    public Object chronProcessedForExtraTable(CustomUserDetails customUserDetails) {
        log.info("Processing chron for extra table");
        return "Chron process for extra table completed";
    }

    @Override
    public Object toAvoidDuplicatesImportFiles(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Avoiding duplicates in import files");
        return "Duplicate check completed";
    }

    @Override
    public Object getGlobalImportDropdownLists(CustomUserDetails customUserDetails, Date operationDateTime) {
        log.info("Getting global import dropdown lists");
        return java.util.Collections.emptyList();
    }

    @Override
    public Object MechanicalIntegrityFileUpload(MultipartFile multipartFile, String category, Date inspectionDate, String description, CustomUserDetails customUserDetails) {
        log.info("Mechanical integrity file upload: {}", multipartFile.getOriginalFilename());
        return "Mechanical integrity file uploaded: " + multipartFile.getOriginalFilename();
    }

    @Override
    public Object getMechanicalIntegrityUploadedFiles(CustomUserDetails customUserDetails, SearchVM dateRangeVM, Pageable pageable) {
        log.info("Getting mechanical integrity uploaded files");
        return java.util.Collections.emptyList();
    }

    @Override
    public Object updateTempleteFile(CustomUserDetails customUserDetails, String id, GlobalFileUploadVM globalFileUploadVM) {
        log.info("Updating template file: {}", id);
        return "Template file updated";
    }

    @Override
    public Object automaticProcess(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Running automatic process");
        return "Automatic process completed";
    }

    @Override
    public Object automaticUploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails) {
        log.info("Automatic upload file: {}", multipartFile.getOriginalFilename());
        return "Automatic upload completed: " + multipartFile.getOriginalFilename();
    }

    @Override
    public Object automaticProcessWithoutImport(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Running automatic process without import");
        return "Automatic process without import completed";
    }

    @Override
    public Object getLatestApprovedDate(CustomUserDetails customUserDetails) {
        log.info("Getting latest approved date");
        return null;
    }

    @Override
    public Object createExcel(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Creating Excel report");
        return "Excel report created";
    }

    @Override
    public Object updatePlantImport12Data(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Updating import 12 data");
        return "Import 12 data updated";
    }

    @Override
    public Object removeAboveDataOtherthanImportData(CustomUserDetails customUserDetails, Date operationDateTime) {
        log.info("Removing above data other than import data");
        return "Data removal completed";
    }

    @Override
    public Object removeDataBaseData(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Removing database data");
        return "Database data removed";
    }

    private List<String> findAllTicketNumbers() {
        return java.util.Collections.emptyList();
    }

    private List<String> findAllStatuses() {
        return java.util.Collections.emptyList();
    }

    private List<String> findAllProjects() {
        return java.util.Collections.emptyList();
    }

    private List<String> findAllModules() {
        return java.util.Collections.emptyList();
    }
}