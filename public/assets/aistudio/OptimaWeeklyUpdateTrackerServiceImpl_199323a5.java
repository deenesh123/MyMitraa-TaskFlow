package com.kelltontech.optima.api_service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kelltontech.optima.api_domain.AdvanceSearchList;
import com.kelltontech.optima.api_domain.DCompletionFileInfo;
import com.kelltontech.optima.api_domain.EmailRecipients;
import com.kelltontech.optima.api_domain.FeedbackHistory;
import com.kelltontech.optima.api_domain.OptimaWeeklyUpdateTracker;
import com.kelltontech.optima.api_errors.CustomExceptionCode;
import com.kelltontech.optima.api_errors.RegisteredException;
import com.kelltontech.optima.api_mv.EmailMV;
import com.kelltontech.optima.api_mv.FeedbackHistoryMV;
import com.kelltontech.optima.api_mv.PaginationDataMV;
import com.kelltontech.optima.api_repository.OptimaWeeklyUpdateTrackerRepository;
import com.kelltontech.optima.api_service.Genericservices;
import com.kelltontech.optima.api_service.OptimaWeeklyUpdateTrackerService;
import com.kelltontech.optima.api_successcode.CustomResponse;
import com.kelltontech.optima.api_successcode.SuccessCode;
import com.kelltontech.optima.api_util.StringUtil;
import com.kelltontech.optima.api_util.WellServiceAuditUtil;
import com.kelltontech.optima.api_vm.DashboardProjectVM;
import com.kelltontech.optima.api_vm.DocumentUploadVM;
import com.kelltontech.optima.api_vm.FeedbackSummaryVM;
import com.kelltontech.optima.api_vm.MailRequestVM;
import com.kelltontech.optima.api_vm.OptimaWeeklyUpdateTrackerVM;
import com.kelltontech.optima.api_vm.ProjectTrackerDefaultVM;
import com.kelltontech.optima.api_vm.SearchVM;
import com.kelltontech.optima.config.Constants;
import com.kelltontech.optima.security.CustomUserDetails;
import com.kelltontech.optima.web.rest.errors.CustomParameterizedException;
import com.kelltontech.optima.web.rest.vm.CompletionFilesVM;

@Service
public class OptimaWeeklyUpdateTrackerServiceImpl implements OptimaWeeklyUpdateTrackerService {
    private final Logger log = LoggerFactory.getLogger(OptimaWeeklyUpdateTrackerServiceImpl.class);

    @Autowired
    private OptimaWeeklyUpdateTrackerRepository optimaWeeklyUpdateTrackerRepository;
    
    @Autowired
	private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WellServiceAuditUtil wellServiceAuditUtil;
    
    @Autowired
	private FileUploadServiceImpl fileUploadServiceImpl;
    
    @Autowired
	public Genericservices genericservices;

    final String HISTORY_UPLOAD;
    final String UPLOAD_FOLDER;
    public OptimaWeeklyUpdateTrackerServiceImpl(Environment env) {
    	UPLOAD_FOLDER = env.getProperty("spring.upload-path");
        this.HISTORY_UPLOAD = env.getProperty("spring.history-uploads");
    }

    @Override
    public Object save(OptimaWeeklyUpdateTrackerVM vm, CustomUserDetails customUserDetails) {
        String methodName = "saveOptimaWeeklyUpdateTracker";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);

        try {
            OptimaWeeklyUpdateTracker tracker = new OptimaWeeklyUpdateTracker();

            tracker.setProjectName(vm.getProjectName());
            tracker.setIndustry(vm.getIndustry());
            tracker.setStatus(vm.getStatus());
            tracker.setStartDate(StringUtil.convertStringIntoDatePattern(vm.getProjectStartDate()));
            tracker.setGoLiveDate(StringUtil.convertStringIntoDatePattern(vm.getGoLiveDate()));
            tracker.setCodeBase(vm.getCodeBase());
            tracker.setDocumentation(vm.getDocumentation());
            tracker.setKtProvider(vm.getKtProvider());
            tracker.setCustomerName(vm.getCustomerName());
            tracker.setCurrentState(vm.getCurrentState());
            tracker.setCurrentStatus(vm.getCurrentStatus());
            tracker.setRef(vm.getRef());
            tracker.setProjectStatus(vm.getProjectStatus());
            tracker.setHldLink(vm.getHldLink());
            tracker.setLldLink(vm.getLldLink());
            tracker.setTesters(vm.getTesters());
            tracker.setFeDevelopers(vm.getFeDevelopers());
            tracker.setBeDevelopers(vm.getBeDevelopers());
            tracker.setPythonDevelopers(vm.getPythonDevelopers());
            tracker.setUxDesigners(vm.getUxDesigners());
            tracker.setSharedResources(vm.getSharedResources());
            tracker.setLastWeekStatus(vm.getLastWeekStatus());
            tracker.setPriority(vm.getPriority());
            tracker.setProjectSummery(vm.getProjectSummery());
            tracker.setCustomerGeography(vm.getCustomerGeography());
            tracker.setRisk(vm.getRisk());
            tracker.setTotalEnv(vm.getTotalEnv());
            tracker.setDevUrl(vm.getDevUrl());
            tracker.setDevArrow(vm.isDevArrow());
            tracker.setQaUrl(vm.getQaUrl());
            tracker.setQaArrow(vm.isQaArrow());
            tracker.setLaUrl(vm.getLaUrl());
            tracker.setLaArrow(vm.isLaArrow());
            tracker.setOtherUrl(vm.getOtherUrl());
            tracker.setOtherArrow(vm.isOtherArrow());
            tracker.setWeeklyStartDate(StringUtil.convertStringIntoDatePattern(vm.getWeeklyStartDate()));
            tracker.setWeeklyEndDate(StringUtil.convertStringIntoDatePattern(vm.getWeeklyEndDate()));

            // Handle HLD attachments
            if (StringUtil.haveValue(vm.getHldAttachment())) {
                List<String> savedHldPaths = new ArrayList<>();
                for (MultipartFile file : vm.getHldAttachment()) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        CompletionFilesVM completionFilesVM = new CompletionFilesVM();
                        completionFilesVM.setMultipartFile(file);
                        completionFilesVM.setCategory("HLD");

                        DCompletionFileInfo uploadedFile = fileUploadServiceImpl.uploadCompletionFile(
                            completionFilesVM, "OptimaWeekly", customUserDetails);

                        String relativePath = uploadedFile.getFileName();
                        log.info("Uploaded HLD file: " + originalFilename + " -> " + relativePath);
                        savedHldPaths.add(relativePath);
                    }
                }
                tracker.setHldAttachment(savedHldPaths);
            }

            // Handle LLD attachments
            if (StringUtil.haveValue(vm.getLldAttachment())) {
                List<String> savedLldPaths = new ArrayList<>();
                for (MultipartFile file : vm.getLldAttachment()) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        CompletionFilesVM completionFilesVM = new CompletionFilesVM();
                        completionFilesVM.setMultipartFile(file);
                        completionFilesVM.setCategory("LLD");

                        DCompletionFileInfo uploadedFile = fileUploadServiceImpl.uploadCompletionFile(
                            completionFilesVM, "OptimaWeekly", customUserDetails);

                        String relativePath = uploadedFile.getFileName();
                        log.info("Uploaded LLD file: " + originalFilename + " -> " + relativePath);
                        savedLldPaths.add(relativePath);
                    }
                }
                tracker.setLldAttachment(savedLldPaths);
            }

            OptimaWeeklyUpdateTracker savedTracker = optimaWeeklyUpdateTrackerRepository.save(tracker);

            if (StringUtil.haveValue(savedTracker.getId())) {
                wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                        savedTracker.getId(), "Inserted new OptimaWeeklyUpdateTracker record: " + savedTracker, "");
            }

            log.info(this.getClass() + " " + methodName + " " + Constants.END);
            return savedTracker;

        } catch (Exception e) {
        	e.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
            throw new CustomParameterizedException(
                RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVING_EXCEPTION.getException(),
                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVING_EXCEPTION.getErrCode());
        }
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        String methodName = "getById";
        log.info(this.getClass() + " " + methodName + " for ID: " + id + " " + Constants.START);

        try {
            OptimaWeeklyUpdateTracker data = optimaWeeklyUpdateTrackerRepository.findByDeleteFlagAndId(Constants.FALSE, id);

            if (!StringUtil.haveValue(data)) {
                throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                        CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                        CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            }

            log.info(this.getClass() + " " + methodName + " " + Constants.END);

            wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(), id,
                       "Fetches a record from OptimaWeeklyUpdateTracker, the record details :" + data , "");

            return data;
        } catch (CustomParameterizedException customException) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage() + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM ) {
        String methodName = "fetchAll";
        log.info(this.getClass() + " " + methodName + " for user: " + username + " " + Constants.START);
        List<OptimaWeeklyUpdateTracker> list = null;

        try {
            if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
                Date startDate = StringUtil.setStringToStartDay(searchVM.getStartDate());
                Date endDate = StringUtil.setStringToEndDay(searchVM.getEndDate());
                // Search based on date range
                list = optimaWeeklyUpdateTrackerRepository.findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
                        Constants.FALSE, startDate, endDate);
            } else {
                // Default search (no date range)
                list = optimaWeeklyUpdateTrackerRepository.findByDeleteFlagOrderByModifiedDateTimeDesc(false);
            }
        } catch (CustomParameterizedException customException) {
            customException.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                    + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }

        log.info(this.getClass() + " " + methodName + " " + Constants.END);
        return list;
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        String methodName = "fetchAll";
        log.info(this.getClass() + " " + methodName + " for user: " + username + " " + Constants.START);
        List<OptimaWeeklyUpdateTracker> feedbackHistory = null;
		PaginationDataMV paginationDataMV = null;
		Query query = new Query();
		long noOfDocs;

        try {
        	query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false));
			if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
			    Date startDate = Date.from(
			        LocalDate.parse(searchVM.getStartDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
			            .atStartOfDay().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
			    Date endDate = Date.from(
			        LocalDate.parse(searchVM.getEndDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
			            .atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

			    query.addCriteria(new Criteria().andOperator(
		                Criteria.where("weekly_end_date").gte(startDate),
		                Criteria.where("weekly_start_date").lte(endDate)
		            ));
			}
			query.with(new Sort(Sort.Direction.DESC, "weekly_start_date"));
            noOfDocs = mongoOperations.count(query, OptimaWeeklyUpdateTracker.class);

            feedbackHistory = mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);
            paginationDataMV = new PaginationDataMV();
            paginationDataMV.setContent(feedbackHistory);
            paginationDataMV.setNumber(pageable.getPageNumber());
            paginationDataMV.setNumberOfElements(feedbackHistory.size());
            paginationDataMV.setSize(pageable.getPageSize());
            paginationDataMV.setTotalElements(noOfDocs);
            paginationDataMV.setTotalPages((int) Math.ceil((double) noOfDocs / pageable.getPageSize()));
			
        } catch (CustomParameterizedException customException) {
            customException.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                    + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }

        log.info(this.getClass() + " " + methodName + " " + Constants.END);
        return paginationDataMV;
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        String methodName = "Deleting OptimaWeeklyUpdateTracker with ID: " + id;
        log.info(this.getClass() + " " + methodName + " " + Constants.START);
        OptimaWeeklyUpdateTracker oldData = null;

        try {
            oldData = optimaWeeklyUpdateTrackerRepository.findByDeleteFlagAndId(Constants.FALSE, id);
            if (!StringUtil.haveValue(oldData)) {
                throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                        CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                        CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            } else {
                oldData.setDeleteFlag(Constants.TRUE);
                OptimaWeeklyUpdateTracker savedData = optimaWeeklyUpdateTrackerRepository.save(oldData);

                if (StringUtil.haveValue(savedData.getId()))
                    wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                            savedData.getId(), "Deleted a record in OptimaWeeklyUpdateTracker  : " + oldData , "");

                log.info(this.getClass() + " " + methodName + " " + Constants.END);
                return savedData;
            }

        } catch (CustomParameterizedException customException) {
            customException.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                    + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_DELETING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_DELETING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_DELETING_EXCEPTION.getErrCode());
        }
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        String methodName = "advanceSearch";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);

        List<OptimaWeeklyUpdateTracker> list = null;
        PaginationDataMV paginationDataMV = null;
        Query query = new Query();
        Date startDate = null;
        Date endDate = null;
        int intvalue = 0;
        double doublevalue = 0.0;
        double convertDouble = 0.0;
        long noOfDocs;
        try {
            query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Boolean.FALSE));
            
            if(StringUtil.haveValue(searchVM.getProjectStatus())) {
            	query.addCriteria(Criteria.where("project_status").is(searchVM.getProjectStatus()));
            }
            
            if(StringUtil.haveValue(searchVM.getPriority())) {
            	query.addCriteria(Criteria.where("priority").is(searchVM.getPriority()));
            }
            
            if(StringUtil.haveValue(searchVM.getCustomerGeography())) {
            	query.addCriteria(Criteria.where("customer_geography").regex(searchVM.getCustomerGeography(), "i"));
            }

            //Date Range
            if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
                startDate = StringUtil.getDayStartTimeFromSimpleDateString(searchVM.getStartDate());
                endDate = StringUtil.getDayEndTimeFromSimpleDateString(searchVM.getEndDate());
                query.addCriteria(Criteria.where(Constants.MODIFIED_DATE_TIME).gte(startDate).lte(endDate));
            }

            if (StringUtil.haveValue(searchVM.getParameter()) && StringUtil.haveValue(searchVM.getParametervalue())) {
                // For double & int range values
                if (Constants.OPTIMA_WEEKLY_UPDATE_TRACKER_DOUBLE.contains(searchVM.getParameter())) {
                    if (StringUtil.haveValue(searchVM.getParametervalue())) {
                        convertDouble = Double.parseDouble(searchVM.getParametervalue());
                        intvalue = (int) (convertDouble);
                    }
                    doublevalue = intvalue;
                    doublevalue = doublevalue + 0.99;

                    query.addCriteria(Criteria.where(searchVM.getParameter()).gte(intvalue).lte(doublevalue));
                // For Date range
                } else if (Constants.OPTIMA_WEEKLY_UPDATE_TRACKER_DATE.contains(searchVM.getParameter())) {
                    Date startDate1 = StringUtil.getDayStartTimeFromSimpleDateString(searchVM.getParametervalue());
                    Date endDate1 = StringUtil.getDayEndTimeFromSimpleDateString(searchVM.getParametervalue());
                    query.addCriteria(Criteria.where(searchVM.getParameter()).gte(startDate1).lt(endDate1));
                // For String
                } else {
                    query.addCriteria(Criteria.where(searchVM.getParameter()).regex(searchVM.getParametervalue(), "i"));
                }
            }

            if (StringUtil.haveValue(searchVM.getSortName())) {
                if ("ASC".equalsIgnoreCase(searchVM.getSortType())) {
                    query.with(new Sort(Sort.Direction.ASC, searchVM.getSortName()));
                } else {
                    query.with(new Sort(Sort.Direction.DESC, searchVM.getSortName()));
                }
            } else {
                if ("ASC".equalsIgnoreCase(searchVM.getSortType())) {
                    query.with(new Sort(Sort.Direction.ASC, Constants.CREATED_ON));
                } else {
                    query.with(new Sort(Sort.Direction.DESC, Constants.CREATED_ON));
                }
            }

            query.with(pageable);
            list = mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);
            noOfDocs = mongoOperations.count(query, OptimaWeeklyUpdateTracker.class);

            paginationDataMV = new PaginationDataMV();
            paginationDataMV.setContent(list);
            paginationDataMV.setNumber(pageable.getPageNumber());
            paginationDataMV.setNumberOfElements(list.size());
            paginationDataMV.setSize(pageable.getPageSize());
            paginationDataMV.setTotalElements(noOfDocs);
            paginationDataMV.setTotalPages((int) Math.ceil((double) noOfDocs / pageable.getPageSize()));

            log.info(this.getClass() + " " + methodName + " " + Constants.END);
            return paginationDataMV;
        } catch (CustomParameterizedException customException) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage() + " ]");
            throw new CustomParameterizedException( RegisteredException.ADVACNE_SEARCH_EXCEPTION.getException(),
                CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrMsg(),
                CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrCode());
        }
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        String methodName = "getParamterList";
        String TableName = "OptimaWeeklyUpdateTracker";
        AdvanceSearchList optimaWeeklyUpdateTrackerList = null;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DELETEFLAG).is(Boolean.FALSE).and("tableName").is(TableName));
            optimaWeeklyUpdateTrackerList = mongoOperations.findOne(query, AdvanceSearchList.class);
        } catch (CustomParameterizedException customException) {
            customException.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                    + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode()
            );
        }
        log.info(this.getClass() + " " + methodName + " " + Constants.END);
        return optimaWeeklyUpdateTrackerList;
    }

    @Override
    public Object update(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        String methodName = "update";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);

        OptimaWeeklyUpdateTracker oldData = null;
        CompletionFilesVM completionFilesVM;

        try {
            oldData = optimaWeeklyUpdateTrackerRepository.findByDeleteFlagAndId(Constants.FALSE, optimaWeeklyUpdateTrackerVM.getProjectId());

            if (!StringUtil.haveValue(oldData)) {
                throw new CustomParameterizedException(
                    RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            }

            // Handle HLD attachments
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getHldAttachment())) {
                List<String> savedHldPaths = new ArrayList<>();
                for (MultipartFile file : optimaWeeklyUpdateTrackerVM.getHldAttachment()) {
                    if (!file.isEmpty()) {
                        completionFilesVM = new CompletionFilesVM();
                        completionFilesVM.setMultipartFile(file);
                        completionFilesVM.setCategory("HLD");

                        DCompletionFileInfo uploadedFile = fileUploadServiceImpl.uploadCompletionFile(
                            completionFilesVM, "OptimaWeekly", customUserDetails);

                        String relativePath = uploadedFile.getFileName();
                        log.info("Uploaded HLD file: " + file.getOriginalFilename() + " -> " + relativePath);
                        savedHldPaths.add(relativePath);
                    }
                }
                oldData.setHldAttachment(savedHldPaths);
            }


            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getLldAttachment())) {
                List<String> savedLldPaths = new ArrayList<>();
                for (MultipartFile file : optimaWeeklyUpdateTrackerVM.getLldAttachment()) {
                    if (!file.isEmpty()) {
                        completionFilesVM = new CompletionFilesVM();
                        completionFilesVM.setMultipartFile(file);
                        completionFilesVM.setCategory("LLD");

                        DCompletionFileInfo uploadedFile = fileUploadServiceImpl.uploadCompletionFile(
                            completionFilesVM, "OptimaWeekly", customUserDetails);

                        String relativePath = uploadedFile.getFileName();
                        log.info("Uploaded LLD file: " + file.getOriginalFilename() + " -> " + relativePath);
                        savedLldPaths.add(relativePath);
                    }
                }
                oldData.setLldAttachment(savedLldPaths);
            }

            // Update fields
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getProjectName())) {
                oldData.setProjectName(optimaWeeklyUpdateTrackerVM.getProjectName());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getIndustry())) {
                oldData.setIndustry(optimaWeeklyUpdateTrackerVM.getIndustry());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getStatus())) {
                oldData.setStatus(optimaWeeklyUpdateTrackerVM.getStatus());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getProjectStartDate())) {
                oldData.setStartDate(StringUtil.convertStringIntoDatePattern(optimaWeeklyUpdateTrackerVM.getProjectStartDate()));
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getGoLiveDate())) {
                oldData.setGoLiveDate(StringUtil.convertStringIntoDatePattern(optimaWeeklyUpdateTrackerVM.getGoLiveDate()));
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getCodeBase())) {
                oldData.setCodeBase(optimaWeeklyUpdateTrackerVM.getCodeBase());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getDocumentation())) {
                oldData.setDocumentation(optimaWeeklyUpdateTrackerVM.getDocumentation());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getKtProvider())) {
                oldData.setKtProvider(optimaWeeklyUpdateTrackerVM.getKtProvider());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getCustomerName())) {
                oldData.setCustomerName(optimaWeeklyUpdateTrackerVM.getCustomerName());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getCurrentState())) {
                oldData.setCurrentState(optimaWeeklyUpdateTrackerVM.getCurrentState());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getCurrentStatus())) {
                oldData.setCurrentStatus(optimaWeeklyUpdateTrackerVM.getCurrentStatus());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getRef())) {
                oldData.setRef(optimaWeeklyUpdateTrackerVM.getRef());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getLastWeekStatus())) {
                oldData.setLastWeekStatus(optimaWeeklyUpdateTrackerVM.getLastWeekStatus());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getProjectStatus())) {
                oldData.setProjectStatus(optimaWeeklyUpdateTrackerVM.getProjectStatus());
            }
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getTesters())) {
                oldData.setTesters(optimaWeeklyUpdateTrackerVM.getTesters());
            }
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getBeDevelopers())) {
                oldData.setBeDevelopers(optimaWeeklyUpdateTrackerVM.getBeDevelopers());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getProjectSummery())) {
                oldData.setProjectSummery(optimaWeeklyUpdateTrackerVM.getProjectSummery());
            }
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getFeDevelopers())) {
                oldData.setFeDevelopers(optimaWeeklyUpdateTrackerVM.getFeDevelopers());
            }
            if (optimaWeeklyUpdateTrackerVM.getHldLink() != null && optimaWeeklyUpdateTrackerVM.getHldLink() != "") {
                oldData.setHldLink(optimaWeeklyUpdateTrackerVM.getHldLink());
            }
            if (optimaWeeklyUpdateTrackerVM.getLldLink() != null && optimaWeeklyUpdateTrackerVM.getLldLink() != "") {
                oldData.setLldLink(optimaWeeklyUpdateTrackerVM.getLldLink());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getPriority())) {
                oldData.setPriority(optimaWeeklyUpdateTrackerVM.getPriority());
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getWeeklyStartDate())) {
                oldData.setWeeklyStartDate(StringUtil.convertStringIntoDatePattern(optimaWeeklyUpdateTrackerVM.getWeeklyStartDate()));
            }
            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getWeeklyEndDate())) {
                oldData.setWeeklyEndDate(StringUtil.convertStringIntoDatePattern(optimaWeeklyUpdateTrackerVM.getWeeklyEndDate()));
            }
            if (optimaWeeklyUpdateTrackerVM.getCustomerGeography() != null && optimaWeeklyUpdateTrackerVM.getCustomerGeography() != "") {
                oldData.setCustomerGeography(optimaWeeklyUpdateTrackerVM.getCustomerGeography());
            }
            if (optimaWeeklyUpdateTrackerVM.getRisk() != null && optimaWeeklyUpdateTrackerVM.getRisk() != "") {
                oldData.setRisk(optimaWeeklyUpdateTrackerVM.getRisk());
            }
            if (optimaWeeklyUpdateTrackerVM.getTotalEnv() >= 0) {
                oldData.setTotalEnv(optimaWeeklyUpdateTrackerVM.getTotalEnv());
            }

            // Static environment fields
            if (optimaWeeklyUpdateTrackerVM.getDevUrl() != null && optimaWeeklyUpdateTrackerVM.getDevUrl() != "") {
                oldData.setDevUrl(optimaWeeklyUpdateTrackerVM.getDevUrl());
            }
            oldData.setDevArrow(optimaWeeklyUpdateTrackerVM.isDevArrow());

            if (optimaWeeklyUpdateTrackerVM.getQaUrl() != null && optimaWeeklyUpdateTrackerVM.getQaUrl() != "") {
                oldData.setQaUrl(optimaWeeklyUpdateTrackerVM.getQaUrl());
            }
            oldData.setQaArrow(optimaWeeklyUpdateTrackerVM.isQaArrow());

            if (optimaWeeklyUpdateTrackerVM.getLaUrl() != null && optimaWeeklyUpdateTrackerVM.getLaUrl() != "") {
                oldData.setLaUrl(optimaWeeklyUpdateTrackerVM.getLaUrl());
            }
            oldData.setLaArrow(optimaWeeklyUpdateTrackerVM.isLaArrow());

            if (optimaWeeklyUpdateTrackerVM.getOtherUrl() != null && optimaWeeklyUpdateTrackerVM.getOtherUrl() != "") {
                oldData.setOtherUrl(optimaWeeklyUpdateTrackerVM.getOtherUrl());
            }
            oldData.setOtherArrow(optimaWeeklyUpdateTrackerVM.isOtherArrow());
            
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getPythonDevelopers())) {
                oldData.setPythonDevelopers(optimaWeeklyUpdateTrackerVM.getPythonDevelopers());
            }
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getUxDesigners())) {
                oldData.setUxDesigners(optimaWeeklyUpdateTrackerVM.getUxDesigners());
            }
            if (StringUtil.haveElements(optimaWeeklyUpdateTrackerVM.getSharedResources())) {
                oldData.setSharedResources(optimaWeeklyUpdateTrackerVM.getSharedResources());
            }

            oldData = optimaWeeklyUpdateTrackerRepository.save(oldData);
            log.info(this.getClass() + " " + methodName + " " + Constants.END);

        } catch (CustomParameterizedException customException) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage() + " ]");
            throw new CustomParameterizedException(
                RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_UPDATING_EXCEPTION.getException(),
                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_UPDATING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_UPDATING_EXCEPTION.getErrCode());
        }

        if (StringUtil.haveValue(oldData)) {
            wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                oldData.getId(), "Updated a record in Diagnosis: " + oldData.toString(), "");
        }

        return oldData;
    }

    @Override
    public Object download(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Downloading Excel for user: " + customUserDetails.getUsername());
        String filePath = "";
        List<OptimaWeeklyUpdateTracker> optimaWeeklyUpdateTrackerList = new ArrayList<>();
        try {
            
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Boolean.FALSE));

            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getStartDate()) && StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getEndDate())) {
                Date startDate = StringUtil.setStringToStartDay(optimaWeeklyUpdateTrackerVM.getStartDate());
                Date endDate = StringUtil.setStringToEndDay(optimaWeeklyUpdateTrackerVM.getEndDate());
                query.addCriteria(Criteria.where("created_on").gte(startDate).lte(endDate));
            }

            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getProjectName())) {
                query.addCriteria(Criteria.where("project_name").is(optimaWeeklyUpdateTrackerVM.getProjectName()));
            }

            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getIndustry())) {
                query.addCriteria(Criteria.where("industry").is(optimaWeeklyUpdateTrackerVM.getIndustry()));
            }

            if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVM.getStatus())) {
                query.addCriteria(Criteria.where("status").is(optimaWeeklyUpdateTrackerVM.getStatus()));
            }


            optimaWeeklyUpdateTrackerList = mongoOperations.find(query, OptimaWeeklyUpdateTracker.class);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("OptimaWeeklyUpdateTracker");

            
            String[] headers = new String[] {
            	    "Project Name", "Customer Geograohy", "Priority", "Project Status", "Risk", "Project Summery", "Industry",
            	    "Total Envs", "DEV (URL)", "DEV (Arrow)", "QA (URL)", "QA (Arrow)", "LA (URL)", "LA (Arrow)", "Other (URL)", "Other (Arrow)",
            	    "State", "Start Date", "Go live Date", "Code base", "Documentation", "KT Provider", "Customer Name",
            	    "Current State", "Current Status", "Ref", "Weekly Start Date", "Weekly End Date", "Testers",
            	    "FE Developers", "BE Developers", "Python Developers", "UX Designers", "Shared Resources", "Last Week Status"
            	};


            for (int i = 0; i < headers.length; i++) {
                XSSFRow row = spreadsheet.createRow(i);
                row.createCell(0).setCellValue(headers[i]);
            }

            int columnIndex = 1;
	        DateFormat df = new SimpleDateFormat("MMM/yyyy");
	        DateFormat dfFull = new SimpleDateFormat("yyyy-MM-dd");

	        for (OptimaWeeklyUpdateTracker data : optimaWeeklyUpdateTrackerList) {

	            XSSFRow row0 = spreadsheet.getRow(0);
	            if (row0 == null) row0 = spreadsheet.createRow(0);
	            row0.createCell(columnIndex).setCellValue(data.getProjectName() != null ? data.getProjectName() : "");

	            XSSFRow row1 = spreadsheet.getRow(1);
	            if (row1 == null) row1 = spreadsheet.createRow(1);
	            row1.createCell(columnIndex).setCellValue(data.getCustomerGeography() != null ? data.getCustomerGeography() : "");

	            XSSFRow row2 = spreadsheet.getRow(2);
	            if (row2 == null) row2 = spreadsheet.createRow(2);
	            row2.createCell(columnIndex).setCellValue(data.getPriority() != null ? data.getPriority() : "");

	            XSSFRow row3 = spreadsheet.getRow(3);
	            if (row3 == null) row3 = spreadsheet.createRow(3);
	            row3.createCell(columnIndex).setCellValue(data.getProjectStatus() != null ? data.getProjectStatus() : "");

	            XSSFRow row4 = spreadsheet.getRow(4);
	            if (row4 == null) row4 = spreadsheet.createRow(4);
	            row4.createCell(columnIndex).setCellValue(data.getRisk() != null ? data.getRisk() : "");

	            XSSFRow row5 = spreadsheet.getRow(5);
	            if (row5 == null) row5 = spreadsheet.createRow(5);
	            row5.createCell(columnIndex).setCellValue(data.getProjectSummery() != null ? data.getProjectSummery() : "");

	            XSSFRow row6 = spreadsheet.getRow(6);
	            if (row6 == null) row6 = spreadsheet.createRow(6);
	            row6.createCell(columnIndex).setCellValue(data.getIndustry() != null ? data.getIndustry() : "");

	            // ➕ New Environment Fields
	            XSSFRow row7 = spreadsheet.getRow(7);
	            if (row7 == null) row7 = spreadsheet.createRow(7);
	            row7.createCell(columnIndex).setCellValue(data.getTotalEnv());

	            XSSFRow row8 = spreadsheet.getRow(8);
	            if (row8 == null) row8 = spreadsheet.createRow(8);
	            row8.createCell(columnIndex).setCellValue(data.getDevUrl() != null ? data.getDevUrl() : "");

	            XSSFRow row9 = spreadsheet.getRow(9);
	            if (row9 == null) row9 = spreadsheet.createRow(9);
	            row9.createCell(columnIndex).setCellValue(data.isDevArrow() ? "Active" : "Inactive");

	            XSSFRow row10 = spreadsheet.getRow(10);
	            if (row10 == null) row10 = spreadsheet.createRow(10);
	            row10.createCell(columnIndex).setCellValue(data.getQaUrl() != null ? data.getQaUrl() : "");

	            XSSFRow row11 = spreadsheet.getRow(11);
	            if (row11 == null) row11 = spreadsheet.createRow(11);
	            row11.createCell(columnIndex).setCellValue(data.isQaArrow() ? "Active" : "Inactive");

	            XSSFRow row12 = spreadsheet.getRow(12);
	            if (row12 == null) row12 = spreadsheet.createRow(12);
	            row12.createCell(columnIndex).setCellValue(data.getLaUrl() != null ? data.getLaUrl() : "");

	            XSSFRow row13 = spreadsheet.getRow(13);
	            if (row13 == null) row13 = spreadsheet.createRow(13);
	            row13.createCell(columnIndex).setCellValue(data.isLaArrow() ? "Active" : "Inactive");

	            XSSFRow row14 = spreadsheet.getRow(14);
	            if (row14 == null) row14 = spreadsheet.createRow(14);
	            row14.createCell(columnIndex).setCellValue(data.getOtherUrl() != null ? data.getOtherUrl() : "");

	            XSSFRow row15 = spreadsheet.getRow(15);
	            if (row15 == null) row15 = spreadsheet.createRow(15);
	            row15.createCell(columnIndex).setCellValue(data.isOtherArrow() ? "Active" : "Inactive");

	            // 🔄 Continue existing fields with shifted row numbers
	            XSSFRow row16 = spreadsheet.getRow(16);
	            if (row16 == null) row16 = spreadsheet.createRow(16);
	            row16.createCell(columnIndex).setCellValue(data.getStatus() != null ? data.getStatus() : "");

	            XSSFRow row17 = spreadsheet.getRow(17);
	            if (row17 == null) row17 = spreadsheet.createRow(17);
	            row17.createCell(columnIndex).setCellValue(data.getStartDate() != null ? df.format(data.getStartDate()) : "");

	            XSSFRow row18 = spreadsheet.getRow(18);
	            if (row18 == null) row18 = spreadsheet.createRow(18);
	            row18.createCell(columnIndex).setCellValue(data.getGoLiveDate() != null ? df.format(data.getGoLiveDate()) : "");

	            XSSFRow row19 = spreadsheet.getRow(19);
	            if (row19 == null) row19 = spreadsheet.createRow(19);
	            row19.createCell(columnIndex).setCellValue(data.getCodeBase() != null ? data.getCodeBase() : "");

	            XSSFRow row20 = spreadsheet.getRow(20);
	            if (row20 == null) row20 = spreadsheet.createRow(20);
	            row20.createCell(columnIndex).setCellValue(data.getDocumentation() != null ? data.getDocumentation() : "");

	            XSSFRow row21 = spreadsheet.getRow(21);
	            if (row21 == null) row21 = spreadsheet.createRow(21);
	            row21.createCell(columnIndex).setCellValue(data.getKtProvider() != null ? data.getKtProvider() : "");

	            XSSFRow row22 = spreadsheet.getRow(22);
	            if (row22 == null) row22 = spreadsheet.createRow(22);
	            row22.createCell(columnIndex).setCellValue(data.getCustomerName() != null ? data.getCustomerName() : "");

	            XSSFRow row23 = spreadsheet.getRow(23);
	            if (row23 == null) row23 = spreadsheet.createRow(23);
	            row23.createCell(columnIndex).setCellValue(data.getCurrentState() != null ? data.getCurrentState() : "");

	            XSSFRow row24 = spreadsheet.getRow(24);
	            if (row24 == null) row24 = spreadsheet.createRow(24);
	            row24.createCell(columnIndex).setCellValue(data.getCurrentStatus() != null ? data.getCurrentStatus() : "");

	            XSSFRow row25 = spreadsheet.getRow(25);
	            if (row25 == null) row25 = spreadsheet.createRow(25);
	            row25.createCell(columnIndex).setCellValue(data.getRef() != null ? data.getRef() : "");

	            XSSFRow row26 = spreadsheet.getRow(26);
	            if (row26 == null) row26 = spreadsheet.createRow(26);
	            row26.createCell(columnIndex).setCellValue(data.getWeeklyStartDate() != null ? dfFull.format(data.getWeeklyStartDate()) : "");

	            XSSFRow row27 = spreadsheet.getRow(27);
	            if (row27 == null) row27 = spreadsheet.createRow(27);
	            row27.createCell(columnIndex).setCellValue(data.getWeeklyEndDate() != null ? dfFull.format(data.getWeeklyEndDate()) : "");

	            XSSFRow row28 = spreadsheet.getRow(28);
	            if (row28 == null) row28 = spreadsheet.createRow(28);
	            row28.createCell(columnIndex).setCellValue(data.getProjectStatus() != null ? data.getProjectStatus() : "");

	            XSSFRow row29 = spreadsheet.getRow(29);
	            if (row29 == null) row29 = spreadsheet.createRow(29);
	            row29.createCell(columnIndex).setCellValue(data.getTesters() != null ? String.join(", ", data.getTesters()) : "");

	            XSSFRow row30 = spreadsheet.getRow(30);
	            if (row30 == null) row30 = spreadsheet.createRow(30);
	            row30.createCell(columnIndex).setCellValue(data.getFeDevelopers() != null ? String.join(", ", data.getFeDevelopers()) : "");

	            XSSFRow row31 = spreadsheet.getRow(31);
	            if (row31 == null) row31 = spreadsheet.createRow(31);
	            row31.createCell(columnIndex).setCellValue(data.getBeDevelopers() != null ? String.join(", ", data.getBeDevelopers()) : "");
	            
	            XSSFRow row32 = spreadsheet.getRow(32);
	            if (row32 == null) row32 = spreadsheet.createRow(32);
	            row32.createCell(columnIndex).setCellValue(data.getPythonDevelopers() != null ? String.join(", ", data.getPythonDevelopers()) : "");
	            
	            XSSFRow row33 = spreadsheet.getRow(33);
	            if (row33 == null) row33 = spreadsheet.createRow(33);
	            row33.createCell(columnIndex).setCellValue(data.getUxDesigners() != null ? String.join(", ", data.getUxDesigners()) : "");
	            
	            XSSFRow row34 = spreadsheet.getRow(34);
	            if (row34 == null) row34 = spreadsheet.createRow(34);
	            row34.createCell(columnIndex).setCellValue(data.getSharedResources() != null ? String.join(", ", data.getSharedResources()) : "");

	            XSSFRow row35 = spreadsheet.getRow(35);
	            if (row35 == null) row35 = spreadsheet.createRow(35);
	            row35.createCell(columnIndex).setCellValue(data.getLastWeekStatus() != null ? data.getLastWeekStatus() : "");

	            columnIndex++;
	        }

            File file = new File(HISTORY_UPLOAD + "/OptimaWeeklyUpdateTracker.xlsx");
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            workbook.close();

            // Audit log
            wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(), "", "Downloaded records from OptimaWeeklyUpdateTracker", "");

            filePath = "/history_uploads/OptimaWeeklyUpdateTracker.xlsx";

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomParameterizedException(RegisteredException.FORMATION_EXCEPTION.getException(),
                    CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(), CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
        }

        return filePath;
    }

	@Override
	public Object dashboard(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVm,
			CustomUserDetails customUserDetails) {
		String methodName = "dashboard";
		log.info(this.getClass() + " " + methodName + " START");

		List<DashboardProjectVM> dashboardList = new ArrayList<>();
		List<FeedbackSummaryVM> feedbackSummaryList = new ArrayList<>();

		try {
			Query projectQuery = new Query();
			projectQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Boolean.FALSE));

			if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getStartDate())
					&& StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getEndDate())) {
				Date startDate = StringUtil.setStringToStartDay(optimaWeeklyUpdateTrackerVm.getStartDate());
				Date endDate = StringUtil.setStringToEndDay(optimaWeeklyUpdateTrackerVm.getEndDate());
				projectQuery.addCriteria(Criteria.where("modified_date_time").gte(startDate).lte(endDate));
			}

			if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getProjectName())) {
				projectQuery
						.addCriteria(Criteria.where("project_name").is(optimaWeeklyUpdateTrackerVm.getProjectName()));
			}

			if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getIndustry())) {
				projectQuery.addCriteria(Criteria.where("industry").is(optimaWeeklyUpdateTrackerVm.getIndustry()));
			}

			if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getStatus())) {
				projectQuery.addCriteria(Criteria.where("status").is(optimaWeeklyUpdateTrackerVm.getStatus()));
			}

			projectQuery.fields().include("projectName").include("status").include("startDate").include("goLiveDate")
					.include("currentState").include("currentStatus");

			List<OptimaWeeklyUpdateTracker> projects = mongoTemplate.find(projectQuery,
					OptimaWeeklyUpdateTracker.class);

			dashboardList = projects.stream().map(project -> {
				DashboardProjectVM vm = new DashboardProjectVM();
				vm.setProjectName(project.getProjectName());
				vm.setStatus(project.getStatus());
				vm.setStartDate(project.getStartDate());
				vm.setGoLiveDate(project.getGoLiveDate());
				vm.setCurrentState(project.getCurrentState());
				vm.setCurrentStatus(project.getCurrentStatus());
				return vm;
			}).sorted(Comparator.comparing(DashboardProjectVM::getProjectName, String.CASE_INSENSITIVE_ORDER))
					.collect(Collectors.toList());

			Query feedbackQuery = new Query();
			feedbackQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Boolean.FALSE));

			if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getStartDate())
					&& StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getEndDate())) {
				Date startDate = StringUtil.setStringToStartDay(optimaWeeklyUpdateTrackerVm.getStartDate());
				Date endDate = StringUtil.setStringToEndDay(optimaWeeklyUpdateTrackerVm.getEndDate());
				feedbackQuery.addCriteria(Criteria.where("modified_date_time").gte(startDate).lte(endDate));
			}

			if (StringUtil.haveValue(optimaWeeklyUpdateTrackerVm.getProjectName())) {
				feedbackQuery.addCriteria(Criteria.where("project").is(optimaWeeklyUpdateTrackerVm.getProjectName()));
			}

			feedbackQuery.fields().include("project").include("team").include("task_owner");

			List<FeedbackHistory> feedbackList = mongoTemplate.find(feedbackQuery, FeedbackHistory.class);

			feedbackSummaryList = feedbackList.stream().map(fb -> {
				FeedbackSummaryVM vm = new FeedbackSummaryVM();
				vm.setProject(fb.getProject());
				vm.setTeam(fb.getTeam());
				vm.setTaskOwner(fb.getTaskOwner());
				return vm;
			}).filter(vm -> StringUtil.haveValue(vm.getTeam()) && StringUtil.haveValue(vm.getTaskOwner()))
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator
									.comparing(f -> f.getProject() + "|" + f.getTeam() + "|" + f.getTaskOwner()))),
							ArrayList::new))
					.stream()
					.sorted(Comparator.comparing(FeedbackSummaryVM::getTaskOwner, String.CASE_INSENSITIVE_ORDER))
					.collect(Collectors.toList());

			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("projectDashboard", dashboardList);
			dataMap.put("feedbackSummary", feedbackSummaryList);

			return dataMap;

		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(
					RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
					CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
					CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object uploadDocuments(String id, DocumentUploadVM documentVM, CustomUserDetails customUserDetails) {
	    String methodName = "uploadWeeklyDocuments";
	    log.info(this.getClass() + " " + methodName + " " + Constants.START);
	    HashMap<String, String> metaInfo = new HashMap<>();
	    List<String> savedFileNames = new ArrayList<>();

	    try {
	        Date weekStartDate = StringUtil.setStringToStartDay(documentVM.getWeekStartDate());
	        Date weekEndDate = StringUtil.setStringToEndDay(documentVM.getWeekEndDate());

	        Query query = new Query();
	        query.addCriteria(Criteria.where("delete_flag").is(Boolean.FALSE)
	            .and("weekly_start_date").gte(weekStartDate)
	            .and("weekly_end_date").lte(weekEndDate));

	        List<OptimaWeeklyUpdateTracker> weeklyRecords = mongoTemplate.find(query, OptimaWeeklyUpdateTracker.class);

	        if (weeklyRecords.isEmpty()) {
	            throw new CustomParameterizedException(
	                RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
	                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
	                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
	        }

	        if (StringUtil.haveValue(documentVM.getAttachments())) {
	            for (MultipartFile file : documentVM.getAttachments()) {
	                if (!file.isEmpty()) {
	                    String originalFilename = file.getOriginalFilename();

	                    CompletionFilesVM completionFilesVM = new CompletionFilesVM();
	                    completionFilesVM.setMultipartFile(file);
	                    completionFilesVM.setCategory(documentVM.getDocType());

	                    DCompletionFileInfo uploadedFile = fileUploadServiceImpl.uploadCompletionFile(
	                        completionFilesVM, "OptimaWeekly", customUserDetails);

	                    String relativePath = uploadedFile.getFileName();
	                    log.info("Uploaded file: " + originalFilename + " -> " + relativePath);
	                    savedFileNames.add(relativePath);

	                    for (OptimaWeeklyUpdateTracker record : weeklyRecords) {
	                        if ("HLD".equalsIgnoreCase(documentVM.getDocType())) {
	                            record.setHldAttachment(Collections.singletonList(relativePath));
	                        } else if ("LLD".equalsIgnoreCase(documentVM.getDocType())) {
	                            record.setLldAttachment(Collections.singletonList(relativePath));
	                        } else {
	                            throw new RuntimeException("Invalid docType. Expected 'HLD' or 'LLD'");
	                        }
	                        mongoTemplate.save(record);
	                    }
	                }
	            }
	        }

	        metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
	        log.info(this.getClass() + " " + methodName + " " + Constants.END);

	        return new CustomResponse(Boolean.TRUE,
	                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
	                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
	                savedFileNames, metaInfo);

	    } catch (Exception e) {
	        log.error(this.getClass() + " " + methodName + " Exception: " + e.getMessage(), e);
	        return new CustomResponse(Boolean.FALSE,
	                CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
	                CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
	                e.getMessage(), metaInfo);
	    }
	}

	@Override
	public Object getFetchAll(String projectName) {
	    String methodName = "getFetchAll";
	    log.info(this.getClass() + " " + methodName + " for project: " + projectName + " " + Constants.START);

	    OptimaWeeklyUpdateTracker defaultRecord = null;
	    try {
	        Query query = new Query();
	        query.addCriteria(Criteria.where("project_name").is(projectName).and("delete_flag").is(Boolean.FALSE));
	        query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
	        query.limit(1);

	        defaultRecord = mongoTemplate.findOne(query, OptimaWeeklyUpdateTracker.class);

	        if (!StringUtil.haveValue(defaultRecord)) {
	            throw new CustomParameterizedException(
	                RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
	                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
	                CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
	        }

	        ProjectTrackerDefaultVM vm = new ProjectTrackerDefaultVM();
	        vm.setProjectName(defaultRecord.getProjectName());
	        vm.setIndustry(defaultRecord.getIndustry());
	        vm.setStatus(defaultRecord.getStatus());
	        vm.setStartDate(defaultRecord.getStartDate());
	        vm.setGoLiveDate(defaultRecord.getGoLiveDate());
	        vm.setCodeBase(defaultRecord.getCodeBase());
	        vm.setDocumentation(defaultRecord.getDocumentation());
	        vm.setKtProvider(defaultRecord.getKtProvider());
	        vm.setCustomerName(defaultRecord.getCustomerName());
	        vm.setTesters(defaultRecord.getTesters());
	        vm.setFeDevelopers(defaultRecord.getFeDevelopers());
	        vm.setBeDevelopers(defaultRecord.getBeDevelopers());
	        vm.setCurrentState(defaultRecord.getCurrentState());
	        vm.setCurrentStatus(defaultRecord.getCurrentStatus());
	        vm.setRef(defaultRecord.getRef());
	        vm.setProjectStatus(defaultRecord.getProjectStatus());
	        vm.setHldLink(defaultRecord.getHldLink());
	        vm.setLldLink(defaultRecord.getLldLink());
	        vm.setLastWeekStatus(defaultRecord.getLastWeekStatus());
	        vm.setPriority(defaultRecord.getPriority());
	        vm.setProjectSummery(defaultRecord.getProjectSummery());
	        vm.setCustomerGeography(defaultRecord.getCustomerGeography());
	        vm.setRisk(defaultRecord.getRisk());
	        vm.setTotalEnv(defaultRecord.getTotalEnv());
	        vm.setDevUrl(defaultRecord.getDevUrl());
	        vm.setDevArrow(defaultRecord.isDevArrow());
	        vm.setQaUrl(defaultRecord.getQaUrl());
	        vm.setQaArrow(defaultRecord.isQaArrow());
	        vm.setLaUrl(defaultRecord.getLaUrl());
	        vm.setLaArrow(defaultRecord.isLaArrow());
	        vm.setOtherUrl(defaultRecord.getOtherUrl());
	        vm.setOtherArrow(defaultRecord.isOtherArrow());
	        vm.setPythonDevelopers(defaultRecord.getPythonDevelopers());
	        vm.setUxDesigners(defaultRecord.getUxDesigners());
	        vm.setSharedResources(defaultRecord.getSharedResources());

	        log.info(this.getClass() + " " + methodName + " " + Constants.END);
	        return vm;

	    } catch (CustomParameterizedException customException) {
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + customException.getMessage() + " ]");
	        throw customException;
	    } catch (Exception exception) {
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage() + " ]");
	        throw new CustomParameterizedException(
					RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
					CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
					CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
	    }
	}

	@Override
	public Object sendWeeklyMail(MailRequestVM mailRequestVM, CustomUserDetails customUserDetails) {
	    String methodName = "sendWeeklyMail";
	    log.info(this.getClass() + " " + methodName + " " + Constants.START);

	    try {
	    	
	        Date weekStartDate = StringUtil.setStringToStartDay(mailRequestVM.getWeekStartDate());
	        Date weekEndDate = StringUtil.setStringToEndDay(mailRequestVM.getWeekEndDate());

	        List<OptimaWeeklyUpdateTracker> weeklyRecords = optimaWeeklyUpdateTrackerRepository
	                .findByDeleteFlagAndWeeklyStartDateGreaterThanEqualAndWeeklyEndDateLessThanEqualOrderByCreatedOnDesc(
	                        Constants.FALSE, weekStartDate, weekEndDate);

	        if (!StringUtil.haveValue(weeklyRecords)) {
                throw new CustomParameterizedException(
                    RegisteredException.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            }

	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet spreadsheet = workbook.createSheet("OptimaWeeklyUpdateTracker");

	        String[] headers = new String[] {
            	    "Project Name", "Customer Geograohy", "Priority", "Project Status", "Risk", "Project Summery", "Industry",
            	    "Total Envs", "DEV (URL)", "DEV (Arrow)", "QA (URL)", "QA (Arrow)", "LA (URL)", "LA (Arrow)", "Other (URL)", "Other (Arrow)",
            	    "State", "Start Date", "Go live Date", "Code base", "Documentation", "KT Provider", "Customer Name",
            	    "Current State", "Current Status", "Ref", "Weekly Start Date", "Weekly End Date", "Testers",
            	    "FE Developers", "BE Developers", "Python Developers", "UX Designers", "Shared Resources", "Last Week Status"
            	};

	        for (int i = 0; i < headers.length; i++) {
	            XSSFRow row = spreadsheet.createRow(i);
	            row.createCell(0).setCellValue(headers[i]);
	        }

	        int columnIndex = 1;
	        DateFormat df = new SimpleDateFormat("MMM/yyyy");
	        DateFormat dfFull = new SimpleDateFormat("yyyy-MM-dd");

	        for (OptimaWeeklyUpdateTracker data : weeklyRecords) {

	            XSSFRow row0 = spreadsheet.getRow(0);
	            if (row0 == null) row0 = spreadsheet.createRow(0);
	            row0.createCell(columnIndex).setCellValue(data.getProjectName() != null ? data.getProjectName() : "");

	            XSSFRow row1 = spreadsheet.getRow(1);
	            if (row1 == null) row1 = spreadsheet.createRow(1);
	            row1.createCell(columnIndex).setCellValue(data.getCustomerGeography() != null ? data.getCustomerGeography() : "");

	            XSSFRow row2 = spreadsheet.getRow(2);
	            if (row2 == null) row2 = spreadsheet.createRow(2);
	            row2.createCell(columnIndex).setCellValue(data.getPriority() != null ? data.getPriority() : "");

	            XSSFRow row3 = spreadsheet.getRow(3);
	            if (row3 == null) row3 = spreadsheet.createRow(3);
	            row3.createCell(columnIndex).setCellValue(data.getProjectStatus() != null ? data.getProjectStatus() : "");

	            XSSFRow row4 = spreadsheet.getRow(4);
	            if (row4 == null) row4 = spreadsheet.createRow(4);
	            row4.createCell(columnIndex).setCellValue(data.getRisk() != null ? data.getRisk() : "");

	            XSSFRow row5 = spreadsheet.getRow(5);
	            if (row5 == null) row5 = spreadsheet.createRow(5);
	            row5.createCell(columnIndex).setCellValue(data.getProjectSummery() != null ? data.getProjectSummery() : "");

	            XSSFRow row6 = spreadsheet.getRow(6);
	            if (row6 == null) row6 = spreadsheet.createRow(6);
	            row6.createCell(columnIndex).setCellValue(data.getIndustry() != null ? data.getIndustry() : "");

	            // ➕ New Environment Fields
	            XSSFRow row7 = spreadsheet.getRow(7);
	            if (row7 == null) row7 = spreadsheet.createRow(7);
	            row7.createCell(columnIndex).setCellValue(data.getTotalEnv());

	            XSSFRow row8 = spreadsheet.getRow(8);
	            if (row8 == null) row8 = spreadsheet.createRow(8);
	            row8.createCell(columnIndex).setCellValue(data.getDevUrl() != null ? data.getDevUrl() : "");

	            XSSFRow row9 = spreadsheet.getRow(9);
	            if (row9 == null) row9 = spreadsheet.createRow(9);
	            row9.createCell(columnIndex).setCellValue(data.isDevArrow() ? "Active" : "Inactive");

	            XSSFRow row10 = spreadsheet.getRow(10);
	            if (row10 == null) row10 = spreadsheet.createRow(10);
	            row10.createCell(columnIndex).setCellValue(data.getQaUrl() != null ? data.getQaUrl() : "");

	            XSSFRow row11 = spreadsheet.getRow(11);
	            if (row11 == null) row11 = spreadsheet.createRow(11);
	            row11.createCell(columnIndex).setCellValue(data.isQaArrow() ? "Active" : "Inactive");

	            XSSFRow row12 = spreadsheet.getRow(12);
	            if (row12 == null) row12 = spreadsheet.createRow(12);
	            row12.createCell(columnIndex).setCellValue(data.getLaUrl() != null ? data.getLaUrl() : "");

	            XSSFRow row13 = spreadsheet.getRow(13);
	            if (row13 == null) row13 = spreadsheet.createRow(13);
	            row13.createCell(columnIndex).setCellValue(data.isLaArrow() ? "Active" : "Inactive");

	            XSSFRow row14 = spreadsheet.getRow(14);
	            if (row14 == null) row14 = spreadsheet.createRow(14);
	            row14.createCell(columnIndex).setCellValue(data.getOtherUrl() != null ? data.getOtherUrl() : "");

	            XSSFRow row15 = spreadsheet.getRow(15);
	            if (row15 == null) row15 = spreadsheet.createRow(15);
	            row15.createCell(columnIndex).setCellValue(data.isOtherArrow() ? "Active" : "Inactive");

	            // 🔄 Continue existing fields with shifted row numbers
	            XSSFRow row16 = spreadsheet.getRow(16);
	            if (row16 == null) row16 = spreadsheet.createRow(16);
	            row16.createCell(columnIndex).setCellValue(data.getStatus() != null ? data.getStatus() : "");

	            XSSFRow row17 = spreadsheet.getRow(17);
	            if (row17 == null) row17 = spreadsheet.createRow(17);
	            row17.createCell(columnIndex).setCellValue(data.getStartDate() != null ? df.format(data.getStartDate()) : "");

	            XSSFRow row18 = spreadsheet.getRow(18);
	            if (row18 == null) row18 = spreadsheet.createRow(18);
	            row18.createCell(columnIndex).setCellValue(data.getGoLiveDate() != null ? df.format(data.getGoLiveDate()) : "");

	            XSSFRow row19 = spreadsheet.getRow(19);
	            if (row19 == null) row19 = spreadsheet.createRow(19);
	            row19.createCell(columnIndex).setCellValue(data.getCodeBase() != null ? data.getCodeBase() : "");

	            XSSFRow row20 = spreadsheet.getRow(20);
	            if (row20 == null) row20 = spreadsheet.createRow(20);
	            row20.createCell(columnIndex).setCellValue(data.getDocumentation() != null ? data.getDocumentation() : "");

	            XSSFRow row21 = spreadsheet.getRow(21);
	            if (row21 == null) row21 = spreadsheet.createRow(21);
	            row21.createCell(columnIndex).setCellValue(data.getKtProvider() != null ? data.getKtProvider() : "");

	            XSSFRow row22 = spreadsheet.getRow(22);
	            if (row22 == null) row22 = spreadsheet.createRow(22);
	            row22.createCell(columnIndex).setCellValue(data.getCustomerName() != null ? data.getCustomerName() : "");

	            XSSFRow row23 = spreadsheet.getRow(23);
	            if (row23 == null) row23 = spreadsheet.createRow(23);
	            row23.createCell(columnIndex).setCellValue(data.getCurrentState() != null ? data.getCurrentState() : "");

	            XSSFRow row24 = spreadsheet.getRow(24);
	            if (row24 == null) row24 = spreadsheet.createRow(24);
	            row24.createCell(columnIndex).setCellValue(data.getCurrentStatus() != null ? data.getCurrentStatus() : "");

	            XSSFRow row25 = spreadsheet.getRow(25);
	            if (row25 == null) row25 = spreadsheet.createRow(25);
	            row25.createCell(columnIndex).setCellValue(data.getRef() != null ? data.getRef() : "");

	            XSSFRow row26 = spreadsheet.getRow(26);
	            if (row26 == null) row26 = spreadsheet.createRow(26);
	            row26.createCell(columnIndex).setCellValue(data.getWeeklyStartDate() != null ? dfFull.format(data.getWeeklyStartDate()) : "");

	            XSSFRow row27 = spreadsheet.getRow(27);
	            if (row27 == null) row27 = spreadsheet.createRow(27);
	            row27.createCell(columnIndex).setCellValue(data.getWeeklyEndDate() != null ? dfFull.format(data.getWeeklyEndDate()) : "");

	            XSSFRow row28 = spreadsheet.getRow(28);
	            if (row28 == null) row28 = spreadsheet.createRow(28);
	            row28.createCell(columnIndex).setCellValue(data.getProjectStatus() != null ? data.getProjectStatus() : "");

	            XSSFRow row29 = spreadsheet.getRow(29);
	            if (row29 == null) row29 = spreadsheet.createRow(29);
	            row29.createCell(columnIndex).setCellValue(data.getTesters() != null ? String.join(", ", data.getTesters()) : "");

	            XSSFRow row30 = spreadsheet.getRow(30);
	            if (row30 == null) row30 = spreadsheet.createRow(30);
	            row30.createCell(columnIndex).setCellValue(data.getFeDevelopers() != null ? String.join(", ", data.getFeDevelopers()) : "");

	            XSSFRow row31 = spreadsheet.getRow(31);
	            if (row31 == null) row31 = spreadsheet.createRow(31);
	            row31.createCell(columnIndex).setCellValue(data.getBeDevelopers() != null ? String.join(", ", data.getBeDevelopers()) : "");

	            XSSFRow row32 = spreadsheet.getRow(32);
	            if (row32 == null) row32 = spreadsheet.createRow(32);
	            row32.createCell(columnIndex).setCellValue(data.getPythonDevelopers() != null ? String.join(", ", data.getPythonDevelopers()) : "");
	            
	            XSSFRow row33 = spreadsheet.getRow(33);
	            if (row33 == null) row33 = spreadsheet.createRow(33);
	            row33.createCell(columnIndex).setCellValue(data.getUxDesigners() != null ? String.join(", ", data.getUxDesigners()) : "");
	            
	            XSSFRow row34 = spreadsheet.getRow(34);
	            if (row34 == null) row34 = spreadsheet.createRow(34);
	            row34.createCell(columnIndex).setCellValue(data.getSharedResources() != null ? String.join(", ", data.getSharedResources()) : "");

	            XSSFRow row35 = spreadsheet.getRow(35);
	            if (row35 == null) row35 = spreadsheet.createRow(35);
	            row35.createCell(columnIndex).setCellValue(data.getLastWeekStatus() != null ? data.getLastWeekStatus() : "");

	            columnIndex++;
	        }

	        String filePath = HISTORY_UPLOAD + "/OptimaWeeklyUpdateTracker.xlsx";
	        File file = new File(filePath);
	        if (file.exists()) {
	            file.delete();
	        }

	        FileOutputStream out = new FileOutputStream(file);
	        workbook.write(out);
	        out.close();
	        workbook.close();

			Query query = new Query();
			query.addCriteria(
					Criteria.where("operation").is("WeeklyStatus").and(Constants.DELETE_FLAG).is(false));
			EmailRecipients emailRecipients = mongoOperations.findOne(query, EmailRecipients.class);
			if (StringUtil.haveValue(emailRecipients)) {
				Map<String, String> fileInfo = new HashMap<>();
				EmailMV emailMV = new EmailMV();
				emailMV.setTo(emailRecipients.getRecipients());
				emailMV.setSubject("Weekly Optima Update Tracker Report");
				emailMV.setHtml(true);
				emailMV.setMultipart(true);
				String emailContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
						+ "body { font-family: Arial, sans-serif; color: #555555; font-size: 14px; line-height: 1.6; padding: 20px; }"
						+ ".footer { margin-top: 30px; font-size: 12px; color: #999999; }" + "</style>" + "</head>"
						+ "<body>" + "<p>Dear Team,</p>"
						+ "<p>Please find attached the weekly Optima update tracker report summarizing project updates for the week.</p>"
						+ "<p>If you have any questions or need further details, please let us know.</p>"
						+ "<p>Thank you,<br>Best regards</p>"
						+ "<p class=\"footer\">This is an automated email — please do not reply directly.</p>" + "</body>"
						+ "</html>";

				emailMV.setContent(emailContent);
				fileInfo.put(file.getName(), file.getAbsolutePath());
				emailMV.setFileInfo(fileInfo);

				genericservices.sendEmailWithAttachmentWithUploadpath(emailMV);
			}
			

	        wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
	                "", "Sent weekly Optima update tracker mail", "");

	        log.info(this.getClass() + " " + methodName + " " + Constants.END);
	        
	        return "Weekly mail sent successfully.";

	    } catch (Exception e) {
	        e.printStackTrace();
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
	        throw new CustomParameterizedException(
                    RegisteredException.EMAIL_EXCEPTION.getException(),
                    CustomExceptionCode.EXCEPTION_WHILE_SENDING_MAIL.getErrMsg(),
                    CustomExceptionCode.EXCEPTION_WHILE_SENDING_MAIL.getErrCode());
	    }
	}	
	

}