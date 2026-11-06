package com.mymitraa.taskflow.api_service;

import java.util.Date;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.mymitraa.taskflow.api_domain.FilesDownloadedCountDomain;
import com.mymitraa.taskflow.api_vm.GlobalFileUploadVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import com.mymitraa.taskflow.security.CustomUserDetails;

public interface GlobalFileUploadsService {

    Object uploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails);

    Object getUploadedFiles(SearchVM dateRangeVM, Pageable pageable);

    Object countDownloadedFiles(CustomUserDetails customUserDetails, FilesDownloadedCountDomain filesDownloadedCount);

    Object numericalAnalysisForFtp(CustomUserDetails customUserDetails);

    Object getImportFileNames(CustomUserDetails customUserDetails);

    Object updateApprovedStatus(String globalFileUploadsId, CustomUserDetails customUserDetails);

    Object chronProcessed(CustomUserDetails customUserDetails);

    Object chronProcessedWithDate(CustomUserDetails customUserDetails, Date operationDateTime);

    Object validateAllFiles(CustomUserDetails customUserDetails, Date operationDateTime);

    Object removeAboveData(Date operationDateTime, CustomUserDetails customUserDetails);

    Object removeDataBaseData(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object uploadTempleteAndManualFle(MultipartFile multipartFile, String category, String startDate, CustomUserDetails customUserDetails);

    Object getLatestDate(CustomUserDetails customUserDetails);

    Object automaticProcess(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object automaticUploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails);

    Object chronProcessedWithDateWithOutVfm(CustomUserDetails customUserDetails, Date operationDateTime);

    Object chronProcessedForExtraTable(CustomUserDetails customUserDetails);

    Object deleteByFileUploadId(String globalFileUploadsId, CustomUserDetails customUserDetails);

    Object updateTempleteFile(CustomUserDetails customUserDetails, String id, GlobalFileUploadVM globalFileUploadVM);

    Object automaticProcessWithoutImport(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object getLatestApprovedDate(CustomUserDetails customUserDetails);

    Object createExcel(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object updatePlantImport12Data(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object removeAboveDataOtherthanImportData(CustomUserDetails customUserDetails, Date operationDateTime);

    Object updateTableData(CustomUserDetails customUserDetails);

    Object toAvoidDuplicatesImportFiles(SearchVM searchVM, CustomUserDetails customUserDetails);

    Object getGlobalImportDropdownLists(CustomUserDetails customUserDetails, Date operationDateTime);

    Object MechanicalIntegrityFileUpload(MultipartFile multipartFile, String category, Date inspectionDate, String description, CustomUserDetails customUserDetails);

    Object getMechanicalIntegrityUploadedFiles(SearchVM dateRangeVM, Pageable pageable);
}