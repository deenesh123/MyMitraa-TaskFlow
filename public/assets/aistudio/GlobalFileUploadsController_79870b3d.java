package com.mymitraa.taskflow.api_controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mymitraa.taskflow.api_domain.FilesDownloadedCountDomain;
import com.mymitraa.taskflow.api_service.GlobalFileUploadsService;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import com.mymitraa.taskflow.api_util.StringUtil;
import com.mymitraa.taskflow.api_vm.GlobalFileUploadVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import com.mymitraa.taskflow.web.rest.vm.SearchVM;

@RestController
@RequestMapping("/api/global_uploads")
public class GlobalFileUploadsController {
    private final Logger log = LoggerFactory.getLogger(GlobalFileUploadsController.class);

    @Autowired
    private GlobalFileUploadsService globalFileUploadsService;

    @PostMapping(value = "/upload_file")
    public ResponseEntity<CustomResponse> addWorkOverSchedule(@RequestBody MultipartFile multipartFile,
            @RequestParam String category, @RequestParam Date operationDateTime,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                globalFileUploadsService.uploadFile(multipartFile, category, operationDateTime, customUserDetails),
                HttpStatus.OK);
    }

    @PutMapping("/get_uploaded_files")
    public ResponseEntity<CustomResponse> getUploadedFiles(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody SearchVM dateRangeVM, Pageable pageable) {
        log.debug("get all remarks details");
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.getUploadedFiles(dateRangeVM, pageable), metaInfo), HttpStatus.OK);
    }

    @PostMapping("/save_downloaded_count")
    public ResponseEntity<CustomResponse> countDownloadedFiles(@RequestHeader("Authorization") String token,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody(required = false) FilesDownloadedCountDomain filesDownloadedCount) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.FTP_FILES_DOWNLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.FTP_FILES_DOWNLOADED_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.FTP_FILES_DOWNLOADED_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.countDownloadedFiles(customUserDetails, filesDownloadedCount), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/get_global_numerical_analysis")
    public ResponseEntity<CustomResponse> numericalAnalysisForAlEngineering(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.numericalAnalysisForFtp(customUserDetails), metaInfo), HttpStatus.OK);
    }

    @GetMapping("/get_import_fileNames")
    public ResponseEntity<CustomResponse> getImportFileNames(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_NAMES_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_NAMES_FETCH_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_NAMES_FETCH_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.getImportFileNames(customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/update_approved_by_id/{globalFileUploadsId}")
    public ResponseEntity<CustomResponse> updateApprovedStatus(@PathVariable String globalFileUploadsId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.updateApprovedStatus(globalFileUploadsId, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/chron_processed")
    public ResponseEntity<CustomResponse> chronProcessed(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.chronProcessed(customUserDetails), metaInfo), HttpStatus.OK);
    }

    @GetMapping("/chron_processed_date")
    public ResponseEntity<CustomResponse> chronProcessedWithDate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Date operationDateTime) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.chronProcessedWithDate(customUserDetails, operationDateTime), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/validate_all_files")
    public ResponseEntity<CustomResponse> validateAllFiles(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Date operationDateTime) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.validateAllFiles(customUserDetails, operationDateTime), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/remove_above_data")
    public ResponseEntity<CustomResponse> removeAboveData(@RequestParam Date operationDateTime,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.DATA_CLEANED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.DATA_CLEANED_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.DATA_CLEANED_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.removeAboveData(operationDateTime, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @PutMapping("/remove_above_data")
    public ResponseEntity<CustomResponse> removeDataBaseData(@RequestBody SearchVM searchVM,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.DATA_CLEANED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.DATA_CLEANED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.DATA_CLEANED_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.removeDataBaseData(searchVM, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @PostMapping(value = "/upload_templete_manual_file")
    public ResponseEntity<CustomResponse> uploadTempleteAndManualFle(@RequestBody MultipartFile multipartFile,
            @RequestParam String category, @RequestParam String startDate,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(), globalFileUploadsService
                        .uploadTempleteAndManualFle(multipartFile, category, startDate, customUserDetails),
                        metaInfo), HttpStatus.OK);
    }

    @GetMapping("/get_latestDate")
    public ResponseEntity<CustomResponse> toGetLatestDate(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.getLatestDate(customUserDetails), metaInfo), HttpStatus.OK);
    }

    @PutMapping("/automatic_process")
    public ResponseEntity<CustomResponse> automaticProcess(@RequestBody SearchVM searchVM,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.automaticProcess(searchVM, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @PostMapping(value = "/automatic_upload_file")
    public ResponseEntity<CustomResponse> automaticUploadFile(@RequestBody MultipartFile multipartFile,
            @RequestParam String category, @RequestParam Date operationDateTime,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(globalFileUploadsService.automaticUploadFile(multipartFile, category,
                operationDateTime, customUserDetails), HttpStatus.OK);
    }

    @GetMapping("/chron_processed_date_without_vfm")
    public ResponseEntity<CustomResponse> chronProcessedWithDateWithOutVfm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String operationDateTime) throws ParseException {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        Date operationDateTime1 = StringUtil.convertSimpleStringToDate(operationDateTime);
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(), globalFileUploadsService
                                .chronProcessedWithDateWithOutVfm(customUserDetails, operationDateTime1),
                        metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/chron_processed_for_extra_table")
    public ResponseEntity<CustomResponse> chronProcessedForExtraTable(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.chronProcessedForExtraTable(customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete_by_id/{globalFileUploadsId}")
    public ResponseEntity<CustomResponse> deleteByFileUploadId(@PathVariable String globalFileUploadsId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_REJCETED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_REJCETED_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_REJCETED_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.deleteByFileUploadId(globalFileUploadsId, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @PostMapping("/update_by_id/{id}")
    public ResponseEntity<CustomResponse> updateTempleteFile(@PathVariable String id,
            @Valid GlobalFileUploadVM globalFileUploadVM, @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Authorization") String token) {
        String methodName = "updateReportFile";

        log.debug("updated the data in templete file list by using id and save " + id);
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<CustomResponse>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.updateTempleteFile(customUserDetails, id, globalFileUploadVM),
                        metaInfo), HttpStatus.OK);

    }

    @GetMapping("/update_table")
    public ResponseEntity<CustomResponse> updateTableData(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.updateTableData(customUserDetails), metaInfo), HttpStatus.OK);
    }

    @PutMapping("/create_excel")
    public ResponseEntity<CustomResponse> createExcel(@RequestBody SearchVM searchVM,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.createExcel(searchVM, customUserDetails), metaInfo), HttpStatus.OK);
    }

    @PutMapping("/update_import12")
    public ResponseEntity<CustomResponse> updatePlantImport12Data(@RequestBody SearchVM searchVM,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.updatePlantImport12Data(searchVM, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/remove_above_data_otherthan_import_data")
    public ResponseEntity<CustomResponse> removeAboveDataOtherthanImportData(@RequestParam Date operationDateTime,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.DATA_CLEANED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.DATA_CLEANED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.DATA_CLEANED_SUCCESSFULLY.getSuccessMessage(), globalFileUploadsService
                                .removeAboveDataOtherthanImportData(operationDateTime, customUserDetails),
                        metaInfo),
                HttpStatus.OK);
    }

    @PutMapping("/automatic_process_without_import")
    public ResponseEntity<CustomResponse> automaticProcessWithoutImport(@RequestBody SearchVM searchVM,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.automaticProcessWithoutImport(searchVM, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/get_latest_approved_date")
    public ResponseEntity<CustomResponse> getLatestApprovedDate(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.getLatestApprovedDate(customUserDetails), metaInfo), HttpStatus.OK);
    }

    @PutMapping("/restriction")
    public ResponseEntity<CustomResponse> toAvoidDuplicatesImportFiles(@RequestBody SearchVM searchVM,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws ParseException {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.toAvoidDuplicatesImportFiles(searchVM, customUserDetails), metaInfo),
                HttpStatus.OK);
    }

    @GetMapping("/get_global_import_dropdown_list")
    public ResponseEntity<CustomResponse> getGlobalImportDropdownLists(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Date operationDateTime) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                        SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                        globalFileUploadsService.getGlobalImportDropdownLists(customUserDetails, operationDateTime), metaInfo),
                HttpStatus.OK);
    }

    @PostMapping(value = "/MechanicalIntegrity_upload_file")
    public ResponseEntity<CustomResponse> MechanicalIntegrityFileUpload(@RequestBody MultipartFile multipartFile,
            @RequestParam String category,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date inspectionDate,
            @RequestParam String description,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(
                globalFileUploadsService.MechanicalIntegrityFileUpload(multipartFile, category, inspectionDate, description, customUserDetails),
                HttpStatus.OK);
    }

    @PutMapping("/get_MechanicalIntegrity_uploaded_files")
    public ResponseEntity<CustomResponse> getMechanicalIntegrityUploadedFiles(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody SearchVM dateRangeVM, Pageable pageable) {
        log.debug("get all remarks details");
        HashMap<String, String> metaInfo = new HashMap<>();
        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getServiceName());
        return new ResponseEntity<>(new CustomResponse(SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessCode(),
                SuccessCode.GLOBAL_FILE_FETCH_SUCCESSFULLY.getSuccessMessage(),
                globalFileUploadsService.getMechanicalIntegrityUploadedFiles(dateRangeVM, pageable), metaInfo),
                HttpStatus.OK);
    }

}