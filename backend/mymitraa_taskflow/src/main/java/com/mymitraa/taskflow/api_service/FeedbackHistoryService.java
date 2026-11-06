package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.FeedbackHistory;
import com.mymitraa.taskflow.api_domain.WorkLogEntry;
import com.mymitraa.taskflow.api_vm.AssignTaskVM;
import com.mymitraa.taskflow.api_vm.EffortRecordVM;
import com.mymitraa.taskflow.api_vm.EpicVM;
import com.mymitraa.taskflow.api_vm.FeedbackHistoryVM;
import com.mymitraa.taskflow.api_vm.GlobalFileUploadVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface FeedbackHistoryService {

    FeedbackHistory saveFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    FeedbackHistory editFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    Object getAllFeedback(SearchVM searchVM, Pageable pageable);

    Object getFeedbackById(String feedbackId);

    Object deleteFeedbackById(String feedbackId);

    Object getFeedbackNumericalCount();

    Object getParamterList(CustomUserDetails customUserDetails);

    Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails);

    Object subTaskAdvanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails);

    Object getSearchableSubtaskFields(CustomUserDetails customUserDetails);

    Object deleteFile(String ticketNumber);

    Object assignTask(AssignTaskVM assignTaskVM, CustomUserDetails customUserDetails);

    Object taskClosed(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    Object addSubTask(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    Object updateSubTask(EpicVM epicVM, String feedbackHistoryId, String epicId, CustomUserDetails customUserDetails);

    Object deleteSubTask(String feedbackHistoryId, String epicId, CustomUserDetails customUserDetails);

    Object getUploadedFiles(SearchVM dateRangeVM, Pageable pageable);

    Object getAllTicketNumbers(CustomUserDetails customUserDetails);

    Object getDailyTaskTracker(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails);

    Object dashboard(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails);

    Object uploadFile(MultipartFile file, CustomUserDetails customUserDetails);

    Object uploadFileByTicketNumber(String ticketNumber, MultipartFile file, CustomUserDetails customUserDetails);

    Object getRecord(SearchVM searchVM, Pageable pageable);

    Object saveWorklogHours(String feedbackHistoryId, String epicId, List<WorkLogEntry> workLogEntry, CustomUserDetails customUserDetails);

    Object addWorklogHours(String feedbackHistoryId, String epicId, List<WorkLogEntry> workLogEntries, CustomUserDetails customUserDetails);

    Object chronProcessed(CustomUserDetails customUserDetails);

    Object chronProcessedWithDate(CustomUserDetails customUserDetails, String operationDateTime);

    Object chronProcessedWithDateWithOutVfm(CustomUserDetails customUserDetails, String operationDateTime);

    Object chronProcessedForExtraTable(CustomUserDetails customUserDetails);

    Object toAvoidDuplicatesImportFiles(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object getGlobalImportDropdownLists(CustomUserDetails customUserDetails, Date operationDateTime);

    Object MechanicalIntegrityFileUpload(MultipartFile multipartFile, String category, Date inspectionDate, String description, CustomUserDetails customUserDetails);

    Object getMechanicalIntegrityUploadedFiles(CustomUserDetails customUserDetails, SearchVM dateRangeVM, Pageable pageable);

    Object updateTempleteFile(CustomUserDetails customUserDetails, String id, GlobalFileUploadVM globalFileUploadVM);

    Object automaticProcess(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object automaticUploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails);

    Object automaticProcessWithoutImport(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object getLatestApprovedDate(CustomUserDetails customUserDetails);

    Object createExcel(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object updatePlantImport12Data(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object removeAboveDataOtherthanImportData(CustomUserDetails customUserDetails, Date operationDateTime);

    Object removeDataBaseData(SearchVM searchVM, CustomUserDetails customUserDetails);
}