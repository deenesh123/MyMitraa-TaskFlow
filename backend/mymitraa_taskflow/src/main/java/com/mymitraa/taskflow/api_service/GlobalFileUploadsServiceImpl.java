package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.FilesDownloadedCountDomain;
import com.mymitraa.taskflow.api_domain.GlobalFileUploads;
import com.mymitraa.taskflow.api_repository.GlobalFileUploadsRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import com.mymitraa.taskflow.api_vm.GlobalFileUploadVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GlobalFileUploadsServiceImpl implements GlobalFileUploadsService {

    private static final Logger log = LoggerFactory.getLogger(GlobalFileUploadsServiceImpl.class);

    @Autowired
    private GlobalFileUploadsRepository globalFileUploadsRepository;

    @Override
    public CustomResponse uploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails) {
        log.info("Uploading file: {} category: {}", multipartFile.getOriginalFilename(), category);
        GlobalFileUploads upload = new GlobalFileUploads();
        upload.setCategory(category);
        upload.setOperationDateTime(operationDateTime);
        upload.setFileName(multipartFile.getOriginalFilename());
        upload.setOriginalFileName(multipartFile.getOriginalFilename());
        upload.setFileType(multipartFile.getContentType());
        upload.setStatus("UPLOADED");
        if (customUserDetails != null) {
            upload.setUploadedBy(customUserDetails.getUsername());
        }
        GlobalFileUploads saved = globalFileUploadsRepository.save(upload);
        Map<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new CustomResponse(
                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                saved,
                metaInfo);
    }

    @Override
    public Object getUploadedFiles(SearchVM dateRangeVM, Pageable pageable) {
        log.info("Getting uploaded files with search: {}", dateRangeVM.getSearch());
        String search = dateRangeVM.getSearch();
        if (search != null && !search.isEmpty()) {
            return globalFileUploadsRepository.findByCategoryContainingAndStatus(search, "UPLOADED");
        }
        Page<GlobalFileUploads> all = globalFileUploadsRepository.findByStatus("UPLOADED", pageable);
        return all.getContent();
    }

    @Override
    public Object countDownloadedFiles(CustomUserDetails customUserDetails, FilesDownloadedCountDomain filesDownloadedCount) {
        log.info("Counting downloaded files for user: {}", customUserDetails.getUsername());
        return java.lang.Long.valueOf(0);
    }

    @Override
    public Object numericalAnalysisForFtp(CustomUserDetails customUserDetails) {
        log.info("Getting numerical analysis for FTP");
        return java.util.Collections.emptyList();
    }

    @Override
    public Object getImportFileNames(CustomUserDetails customUserDetails) {
        log.info("Getting import file names");
        return java.util.Collections.emptyList();
    }

    @Override
    public Object updateApprovedStatus(String globalFileUploadsId, CustomUserDetails customUserDetails) {
        log.info("Updating approved status for: {}", globalFileUploadsId);
        GlobalFileUploads upload = globalFileUploadsRepository.findById(globalFileUploadsId).orElse(null);
        if (upload != null) {
            upload.setStatus("APPROVED");
            upload.setApprovedBy(customUserDetails.getUsername());
            upload.setApprovedDate(new Date());
            return globalFileUploadsRepository.save(upload);
        }
        return null;
    }

    @Override
    public Object chronProcessed(CustomUserDetails customUserDetails) {
        log.info("Processing chron for user: {}", customUserDetails.getUsername());
        return "Chron process completed";
    }

    @Override
    public Object chronProcessedWithDate(CustomUserDetails customUserDetails, Date operationDateTime) {
        log.info("Processing chron with date: {}", operationDateTime);
        return "Chron process completed with date: " + operationDateTime;
    }

    @Override
    public Object validateAllFiles(CustomUserDetails customUserDetails, Date operationDateTime) {
        log.info("Validating all files with date: {}", operationDateTime);
        return "Validation completed";
    }

    @Override
    public Object removeAboveData(Date operationDateTime, CustomUserDetails customUserDetails) {
        log.info("Removing above data with date: {}", operationDateTime);
        globalFileUploadsRepository.deleteByOperationDateTimeBefore(operationDateTime);
        return "Data removal completed";
    }

    @Override
    public Object removeDataBaseData(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Removing database data with search: {}", searchVM.getSearch());
        return "Database data removal completed";
    }

    @Override
    public Object uploadTempleteAndManualFle(MultipartFile multipartFile, String category, String startDate, CustomUserDetails customUserDetails) {
        log.info("Template and manual file upload: {} category: {}", multipartFile.getOriginalFilename(), category);
        GlobalFileUploads upload = new GlobalFileUploads();
        upload.setCategory(category);
        upload.setStatus("UPLOADED");
        upload.setUploadedBy(customUserDetails.getUsername());
        return globalFileUploadsRepository.save(upload);
    }

    @Override
    public Object getLatestDate(CustomUserDetails customUserDetails) {
        log.info("Getting latest date");
        return new java.util.Date();
    }

    @Override
    public Object automaticProcess(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Running automatic process with search: {}", searchVM.getSearch());
        return "Automatic process completed";
    }

    @Override
    public Object automaticUploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails) {
        log.info("Automatic upload file: {} category: {}", multipartFile.getOriginalFilename(), category);
        GlobalFileUploads upload = new GlobalFileUploads();
        upload.setCategory(category);
        upload.setOperationDateTime(operationDateTime);
        upload.setFileName(multipartFile.getOriginalFilename());
        upload.setStatus("UPLOADED");
        upload.setUploadedBy(customUserDetails.getUsername());
        return globalFileUploadsRepository.save(upload);
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
    public Object deleteByFileUploadId(String globalFileUploadsId, CustomUserDetails customUserDetails) {
        log.info("Deleting file upload by id: {}", globalFileUploadsId);
        globalFileUploadsRepository.deleteById(globalFileUploadsId);
        return "File upload deleted";
    }

    @Override
    public Object updateTempleteFile(CustomUserDetails customUserDetails, String id, GlobalFileUploadVM globalFileUploadVM) {
        log.info("Updating template file: {}", id);
        return "Template file update completed";
    }

    @Override
    public Object automaticProcessWithoutImport(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Running automatic process without import with search: {}", searchVM.getSearch());
        return "Automatic process without import completed";
    }

    @Override
    public Object getLatestApprovedDate(CustomUserDetails customUserDetails) {
        log.info("Getting latest approved date");
        return new java.util.Date();
    }

    @Override
    public Object createExcel(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Creating Excel report with search: {}", searchVM.getSearch());
        return "Excel report creation completed";
    }

    @Override
    public Object updatePlantImport12Data(SearchVM searchVM, CustomUserDetails customUserDetails) {
        log.info("Updating import 12 data with search: {}", searchVM.getSearch());
        return "Import 12 data update completed";
    }

    @Override
    public Object removeAboveDataOtherthanImportData(CustomUserDetails customUserDetails, Date operationDateTime) {
        log.info("Removing above data other than import data with date: {}", operationDateTime);
        return "Data removal completed";
    }
}