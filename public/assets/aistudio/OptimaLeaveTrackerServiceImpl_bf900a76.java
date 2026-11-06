package com.kelltontech.optima.api_service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.kelltontech.optima.api_domain.AdvanceSearchList;
import com.kelltontech.optima.api_domain.EmailRecipients;
import com.kelltontech.optima.api_domain.OptimaLeaveTracker;
import com.kelltontech.optima.api_errors.CustomExceptionCode;
import com.kelltontech.optima.api_errors.RegisteredException;
import com.kelltontech.optima.api_mv.EmailMV;
import com.kelltontech.optima.api_mv.PaginationDataMV;
import com.kelltontech.optima.api_repository.OptimaLeaveTrackerRepository;
import com.kelltontech.optima.api_service.OptimaLeaveTrackerService;
import com.kelltontech.optima.api_util.StringUtil;
import com.kelltontech.optima.api_util.WellServiceAuditUtil;
import com.kelltontech.optima.api_vm.LeaveSummaryVM;
import com.kelltontech.optima.api_vm.OptimaLeaveTrackerVM;
import com.kelltontech.optima.api_vm.SearchVM;
import com.kelltontech.optima.config.Constants;
import com.kelltontech.optima.security.CustomUserDetails;
import com.kelltontech.optima.web.rest.errors.CustomParameterizedException;

@Service
public class OptimaLeaveTrackerServiceImpl implements OptimaLeaveTrackerService {
    private final Logger log = LoggerFactory.getLogger(OptimaLeaveTrackerServiceImpl.class);

    @Autowired
    private OptimaLeaveTrackerRepository optimaLeaveTrackerRepository;

    @Autowired
    private MongoOperations mongoOperations;
    
    @Autowired
	private GenericServicesImpl genericServicesImpl;

    @Autowired
    private WellServiceAuditUtil wellServiceAuditUtil;

    final String HISTORY_UPLOAD;
    public OptimaLeaveTrackerServiceImpl(Environment env) {
        this.HISTORY_UPLOAD = env.getProperty("spring.history-uploads");
    }

    @Override
    public Object save(OptimaLeaveTracker optimaLeaveTracker, CustomUserDetails customUserDetails) {
        String methodName = "saveOptimaLeaveTracker";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);
        OptimaLeaveTracker optimaLeaveTrackerWithId = null;
        try {
        	if (optimaLeaveTracker.getStartDate() != null) {
        	    LocalDate localDate = optimaLeaveTracker.getStartDate().toInstant()
        	            .atZone(ZoneId.systemDefault())
        	            .toLocalDate();
        	    String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        	    optimaLeaveTracker.setDay(dayOfWeek);
        	}
        	if (optimaLeaveTracker.getEndDate() != null) {
        	    LocalDate localDate = optimaLeaveTracker.getEndDate().toInstant()
        	            .atZone(ZoneId.systemDefault())
        	            .toLocalDate();
        	    String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        	    optimaLeaveTracker.setDay(dayOfWeek);
        	}
            optimaLeaveTrackerWithId = optimaLeaveTrackerRepository.save(optimaLeaveTracker);

            if (StringUtil.haveValue(optimaLeaveTrackerWithId.getId()))
                wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                        optimaLeaveTrackerWithId.getId(), "Inserted a new record in OptimaLeaveTracker : " + optimaLeaveTrackerWithId , "");

            log.info(this.getClass() + " " + methodName + " " + Constants.END);
        } catch (CustomParameterizedException customException) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DATA_SAVING_EXCEPTION.getException(),
                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_SAVING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_SAVING_EXCEPTION.getErrCode());
        }
        return optimaLeaveTrackerWithId;
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        String methodName = "getById";
        log.info(this.getClass() + " " + methodName + " for ID: " + id + " " + Constants.START);

        try {
            OptimaLeaveTracker data = optimaLeaveTrackerRepository.findByDeleteFlagAndId(Constants.FALSE, id);

            if (!StringUtil.haveValue(data)) {
                throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                        CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                        CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            }

            log.info(this.getClass() + " " + methodName + " " + Constants.END);

            wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(), id,
                       "Fetches a record from OptimaLeaveTracker, the record details :" + data , "");

            return data;
        } catch (CustomParameterizedException customException) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage() + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM ) {
        String methodName = "fetchAll";
        log.info(this.getClass() + " " + methodName + " for user: " + username + " " + Constants.START);
        List<OptimaLeaveTracker> list = null;

        try {
            if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
                Date startDate = StringUtil.setStringToStartDay(searchVM.getStartDate());
                Date endDate = StringUtil.setStringToEndDay(searchVM.getEndDate());
                // Search based on date range
                list = optimaLeaveTrackerRepository.findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
                        Constants.FALSE, startDate, endDate);
            } else {
                // Default search (no date range)
                list = optimaLeaveTrackerRepository.findByDeleteFlagOrderByModifiedDateTimeDesc(false);
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
            throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }

        log.info(this.getClass() + " " + methodName + " " + Constants.END);
        return list;
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        String methodName = "fetchAll";
        log.info(this.getClass() + " " + methodName + " for user: " + username + " " + Constants.START);
        Page<OptimaLeaveTracker> list = null;

        try {
            if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
                Date startDate = StringUtil.setStringToStartDay(searchVM.getStartDate());
                Date endDate = StringUtil.setStringToEndDay(searchVM.getEndDate());
                // Search based on date range
                list = optimaLeaveTrackerRepository.findByDeleteFlagAndCreatedOnBetweenOrderByModifiedDateTimeDesc(
                        Constants.FALSE, startDate, endDate, pageable);
            } else {
                // Default search (no date range)
                list = optimaLeaveTrackerRepository.findByDeleteFlagOrderByModifiedDateTimeDesc(false, pageable);
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
            throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }

        log.info(this.getClass() + " " + methodName + " " + Constants.END);
        return list;
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        String methodName = "Deleting OptimaLeaveTracker with ID: " + id;
        log.info(this.getClass() + " " + methodName + " " + Constants.START);
        OptimaLeaveTracker oldData = null;

        try {
            oldData = optimaLeaveTrackerRepository.findByDeleteFlagAndId(Constants.FALSE, id);
            if (!StringUtil.haveValue(oldData)) {
                throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                        CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                        CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            } else {
                oldData.setDeleteFlag(Constants.TRUE);
                OptimaLeaveTracker savedData = optimaLeaveTrackerRepository.save(oldData);

                if (StringUtil.haveValue(savedData.getId()))
                    wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                            savedData.getId(), "Deleted a record in OptimaLeaveTracker  : " + oldData , "");

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
            throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DATA_DELETING_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_DELETING_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_DELETING_EXCEPTION.getErrCode());
        }
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        String methodName = "advanceSearch";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);

        List<OptimaLeaveTracker> list = null;
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

            //Date Range
            if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
                startDate = StringUtil.getDayStartTimeFromSimpleDateString(searchVM.getStartDate());
                endDate = StringUtil.getDayEndTimeFromSimpleDateString(searchVM.getEndDate());
                query.addCriteria(Criteria.where(Constants.MODIFIED_DATE_TIME).gte(startDate).lte(endDate));
            }

            if (StringUtil.haveValue(searchVM.getParameter()) && StringUtil.haveValue(searchVM.getParametervalue())) {
                // For double & int range values
                if (Constants.OPTIMA_LEAVE_TRACKER_DOUBLE.contains(searchVM.getParameter())) {
                    if (StringUtil.haveValue(searchVM.getParametervalue())) {
                        convertDouble = Double.parseDouble(searchVM.getParametervalue());
                        intvalue = (int) (convertDouble);
                    }
                    doublevalue = intvalue;
                    doublevalue = doublevalue + 0.99;

                    query.addCriteria(Criteria.where(searchVM.getParameter()).gte(intvalue).lte(doublevalue));
                // For Date range
                } else if (Constants.OPTIMA_LEAVE_TRACKER_DATE.contains(searchVM.getParameter())) {
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
            list = mongoOperations.find(query, OptimaLeaveTracker.class);
            noOfDocs = mongoOperations.count(query, OptimaLeaveTracker.class);

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
        String TableName = "OptimaLeaveTracker";
        AdvanceSearchList optimaLeaveTrackerList = null;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DELETEFLAG).is(Boolean.FALSE).and("tableName").is(TableName));
            optimaLeaveTrackerList = mongoOperations.findOne(query, AdvanceSearchList.class);
        } catch (CustomParameterizedException customException) {
            customException.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                    + " ]");
            throw new CustomParameterizedException( RegisteredException.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode()
            );
        }
        log.info(this.getClass() + " " + methodName + " " + Constants.END);
        return optimaLeaveTrackerList;
    }

    @Override
    public Object update(String id, OptimaLeaveTrackerVM optimaLeaveTrackerVM , CustomUserDetails customUserDetails) {
        String methodName = "update";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);

        OptimaLeaveTracker oldData = null;
        try {
            oldData = optimaLeaveTrackerRepository.findByDeleteFlagAndId(Constants.FALSE, id);

            if (!StringUtil.haveValue(oldData)) {
                throw new CustomParameterizedException(
                    RegisteredException.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getException(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrMsg(),
                    CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_NOT_FOUND_EXCEPTION.getErrCode());
            } else {

                if (StringUtil.haveValue(optimaLeaveTrackerVM.getId())) {
                    oldData.setId(optimaLeaveTrackerVM.getId());
                }

                if (StringUtil.haveValue(optimaLeaveTrackerVM.getName())) {
                    oldData.setName(optimaLeaveTrackerVM.getName());
                }

                if (StringUtil.haveValue(optimaLeaveTrackerVM.getRole())) {
                    oldData.setRole(optimaLeaveTrackerVM.getRole());
                }

                if (StringUtil.haveValue(optimaLeaveTrackerVM.getLeaveStartDate())) {
                    Date updatedDate = optimaLeaveTrackerVM.getLeaveStartDate();
                    oldData.setStartDate(updatedDate);

                    LocalDate localDate = updatedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    oldData.setDay(dayOfWeek);
                }
                
                if (StringUtil.haveValue(optimaLeaveTrackerVM.getLeaveEndDate())) {
                    Date updatedDate = optimaLeaveTrackerVM.getLeaveEndDate();
                    oldData.setEndDate(updatedDate);

                    LocalDate localDate = updatedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    oldData.setDay(dayOfWeek);
                }

                if (StringUtil.haveValue(optimaLeaveTrackerVM.getLeaveType())) {
                    oldData.setLeaveType(optimaLeaveTrackerVM.getLeaveType());
                }
                
                if (StringUtil.haveValue(optimaLeaveTrackerVM.getComments())) {
                    oldData.setComments(optimaLeaveTrackerVM.getComments());
                }

                oldData = optimaLeaveTrackerRepository.save(oldData);
            }
            log.info(this.getClass() + " " + methodName + " " + Constants.END);
        } catch (CustomParameterizedException customException) {
            customException.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                    + exception.getMessage() + " ]");
            throw new CustomParameterizedException(
                RegisteredException.OPTIMA_LEAVE_TRACKER_DATA_UPDATING_EXCEPTION.getException(),
                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_UPDATING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DATA_UPDATING_EXCEPTION.getErrCode());
        }

        if (StringUtil.haveValue(oldData))
            wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                oldData.getId(), "Updated a record in Diagnosis : " + oldData.toString(), "");

        return oldData;
    }

    @Override
    public Object download(OptimaLeaveTrackerVM optimaLeaveTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Downloading Excel for user: " + customUserDetails.getUsername());
        String filePath = "";
        List<OptimaLeaveTracker> optimaLeaveTrackerList = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("OptimaLeaveTracker");
            XSSFRow row = spreadsheet.createRow(0);
            Cell c = null;

            row.createCell(0).setCellValue("Id");
            row.createCell(1).setCellValue("Name");
            row.createCell(2).setCellValue("Role");
            row.createCell(3).setCellValue("Start Date");
            row.createCell(4).setCellValue("End Date");
            row.createCell(5).setCellValue("Leave Type");

            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Boolean.FALSE));

            if (StringUtil.haveValue(optimaLeaveTrackerVM.getStartDate()) && StringUtil.haveValue(optimaLeaveTrackerVM.getEndDate())) {
                Date startDate = StringUtil.setStringToStartDay(optimaLeaveTrackerVM.getStartDate());
                Date endDate = StringUtil.setStringToEndDay(optimaLeaveTrackerVM.getEndDate());
                query.addCriteria(Criteria.where("created_on").gte(startDate).lte(endDate));
            }

            if (StringUtil.haveValue(optimaLeaveTrackerVM.getId())) {
                query.addCriteria(Criteria.where("id").regex(optimaLeaveTrackerVM.getId(), "i"));
            }

            if (StringUtil.haveValue(optimaLeaveTrackerVM.getName())) {
                query.addCriteria(Criteria.where("name").regex(optimaLeaveTrackerVM.getName(), "i"));
            }

            if (StringUtil.haveValue(optimaLeaveTrackerVM.getRole())) {
                query.addCriteria(Criteria.where("role").regex(optimaLeaveTrackerVM.getRole(), "i"));
            }

            if (StringUtil.haveValue(optimaLeaveTrackerVM.getLeaveStartDate())) {
                query.addCriteria(Criteria.where("start_date").is(optimaLeaveTrackerVM.getLeaveStartDate()));
            }
            
            if (StringUtil.haveValue(optimaLeaveTrackerVM.getLeaveEndDate())) {
                query.addCriteria(Criteria.where("end_date").is(optimaLeaveTrackerVM.getLeaveEndDate()));
            }

            if (StringUtil.haveValue(optimaLeaveTrackerVM.getLeaveType())) {
                query.addCriteria(Criteria.where("leave_type").regex(optimaLeaveTrackerVM.getLeaveType(), "i"));
            }

            optimaLeaveTrackerList = mongoOperations.find(query, OptimaLeaveTracker.class);

            File file = new File(HISTORY_UPLOAD + "/OptimaLeaveTracker.xlsx");
            if (file.exists()) {
                file.delete();
            }

            if (StringUtil.haveElements(optimaLeaveTrackerList)) {
                int rowid = 1;
                DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

                for (OptimaLeaveTracker data : optimaLeaveTrackerList) {

                    row = spreadsheet.createRow(rowid++);

                    if (StringUtil.haveValue(data.getId())) {
                        c = row.createCell(0);
                        c.setCellValue(data.getId());
                    }

                    if (StringUtil.haveValue(data.getName())) {
                        c = row.createCell(1);
                        c.setCellValue(data.getName());
                    }

                    if (StringUtil.haveValue(data.getRole())) {
                        c = row.createCell(2);
                        c.setCellValue(data.getRole());
                    }

                    if (StringUtil.haveValue(data.getStartDate())) {
                        c = row.createCell(3);
                        XSSFCellStyle cellStyle = (XSSFCellStyle) c.getCellStyle();
                        CreationHelper createHelper = workbook.getCreationHelper();
                        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MMM-yyyy"));
                        c.setCellValue(df.format(data.getStartDate()));
                    }
                    
                    if (StringUtil.haveValue(data.getEndDate())) {
                        c = row.createCell(4);
                        XSSFCellStyle cellStyle = (XSSFCellStyle) c.getCellStyle();
                        CreationHelper createHelper = workbook.getCreationHelper();
                        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MMM-yyyy"));
                        c.setCellValue(df.format(data.getEndDate()));
                    }

                    if (StringUtil.haveValue(data.getLeaveType())) {
                        c = row.createCell(5);
                        c.setCellValue(data.getLeaveType());
                    }

                }

                FileOutputStream out = new FileOutputStream(new File(HISTORY_UPLOAD + "/OptimaLeaveTracker.xlsx"));

                wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(), "", "Downloaded records from OptimaLeaveTracker" , "");

                filePath = "/history_uploads/OptimaLeaveTracker.xlsx";
                workbook.write(out);
                out.close();
            } else {
                throw new CustomParameterizedException(RegisteredException.FORMATION_EXCEPTION.getException(),
                        CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
                        CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CustomParameterizedException(RegisteredException.FORMATION_EXCEPTION.getException(),
                    CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(), CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
        }

        return filePath;
    }

	@Override
	public Object dashboard(OptimaLeaveTrackerVM optimaLeaveTrackerVm, CustomUserDetails customUserDetails) {
	    String methodName = "dashboard";
	    String username = customUserDetails.getUsername();
	    log.info(this.getClass() + " " + methodName + " for user: " + username + " " + Constants.START);

	    try {
	        Query query = new Query();
	        
	        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Boolean.FALSE));
	        if (StringUtil.haveValue(optimaLeaveTrackerVm.getStartDate())) {
	            optimaLeaveTrackerVm.setLeaveStartDate(StringUtil.setStringToStartDay(optimaLeaveTrackerVm.getStartDate()));
	        }
	        if (StringUtil.haveValue(optimaLeaveTrackerVm.getEndDate())) {
	            optimaLeaveTrackerVm.setLeaveEndDate(StringUtil.setStringToEndDay(optimaLeaveTrackerVm.getEndDate()));
	        }

	        if (StringUtil.haveValue(optimaLeaveTrackerVm.getName())) {
	            query.addCriteria(Criteria.where("name").is(optimaLeaveTrackerVm.getName()));
	        }
	        if (StringUtil.haveValue(optimaLeaveTrackerVm.getRole())) {
	            query.addCriteria(Criteria.where("role").is(optimaLeaveTrackerVm.getRole()));
	        }
	        if (StringUtil.haveValue(optimaLeaveTrackerVm.getLeaveType())) {
	            query.addCriteria(Criteria.where("leave_type").is(optimaLeaveTrackerVm.getLeaveType()));
	        }

	        if (optimaLeaveTrackerVm.getLeaveStartDate() != null && optimaLeaveTrackerVm.getLeaveEndDate() != null) {
	            query.addCriteria(
	                new Criteria().andOperator(
	                    Criteria.where("start_date").gte(optimaLeaveTrackerVm.getLeaveStartDate()),
	                    Criteria.where("end_date").lte(optimaLeaveTrackerVm.getLeaveEndDate())
	                )
	            );
	        }

	        List<OptimaLeaveTracker> records = mongoOperations.find(query, OptimaLeaveTracker.class);

	        int totalPlannedLeave = 0;
	        int totalSickLeave = 0;
	        int totalApprovedWfh = 0;
	        int totalUnpprovedWfh = 0;
	        int totalUnplannedLeave = 0;

	        Map<String, LeaveSummaryVM> summaryMap = new HashMap<>();

	        for (OptimaLeaveTracker record : records) {
	            String name = record.getName();
	            String role = record.getRole();
	            String leaveType = record.getLeaveType();

	            LocalDate startDate = record.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	            LocalDate endDate = record.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
	            
	            if ("Holiday".equalsIgnoreCase(leaveType)) {
	                continue;
	            }

	            switch (leaveType) {
	                case "Planned Leave":
	                    totalPlannedLeave += days;
	                    break;
	                case "Sick Leave":
	                    totalSickLeave += days;
	                    break;
	                case "WFH(APPROVED)":
	                    totalApprovedWfh += days;
	                    break;
	                case "WFH(UNAPPROVED)":
	                	totalUnpprovedWfh += days;
	                    break;
	                case "Unplanned Leave":
	                    totalUnplannedLeave += days;
	                    break;
	            }

	            summaryMap.putIfAbsent(name, new LeaveSummaryVM());
	            LeaveSummaryVM summary = summaryMap.get(name);
	            summary.setName(name);
	            summary.setRole(role);

	            switch (leaveType) {
	                case "Planned Leave":
	                    summary.setPlannedLeave(summary.getPlannedLeave() + (int) days);
	                    break;
	                case "Sick Leave":
	                    summary.setSickLeave(summary.getSickLeave() + (int) days);
	                    break;
	                case "WFH(APPROVED)":
	                    summary.setWfhApproved(summary.getWfhApproved() + (int) days);
	                    break;
	                case "WFH(UNAPPROVED)":
	                    summary.setWfhUnapproved(summary.getWfhUnapproved() + (int) days);
	                    break;
	                case "Unplanned Leave":
	                    summary.setUnplannedLeave(summary.getUnplannedLeave() + (int) days);
	                    break;
	            }
	        }

	        List<LeaveSummaryVM> summaryList = new ArrayList<>(summaryMap.values());

	        if (StringUtil.haveValue(optimaLeaveTrackerVm.getSortName()) && StringUtil.haveValue(optimaLeaveTrackerVm.getSortType())) {
	            Comparator<LeaveSummaryVM> comparator = Comparator.comparing(LeaveSummaryVM::getName);

	            switch (optimaLeaveTrackerVm.getSortName()) {
	                case "plannedLeave":
	                    comparator = Comparator.comparing(LeaveSummaryVM::getPlannedLeave);
	                    break;
	                case "sickLeave":
	                    comparator = Comparator.comparing(LeaveSummaryVM::getSickLeave);
	                    break;
	                case "wfhApproved":
	                    comparator = Comparator.comparing(LeaveSummaryVM::getWfhApproved);
	                    break;
	                case "wfhUnapproved":
	                    comparator = Comparator.comparing(LeaveSummaryVM::getWfhUnapproved);
	                    break;
	                case "unplannedLeave":
	                    comparator = Comparator.comparing(LeaveSummaryVM::getUnplannedLeave);
	                    break;
	            }

	            if ("desc".equalsIgnoreCase(optimaLeaveTrackerVm.getSortType())) {
	                comparator = comparator.reversed();
	            }

	            summaryList.sort(comparator);
	        }

	        Map<String, Object> numericalAnalysis = new HashMap<>();
	        numericalAnalysis.put("totalPlannedLeave", totalPlannedLeave);
	        numericalAnalysis.put("totalSickLeave", totalSickLeave);
	        numericalAnalysis.put("totalApprovedWfh", totalApprovedWfh);
	        numericalAnalysis.put("totalUnpprovedWfh", totalUnpprovedWfh);
	        numericalAnalysis.put("totalWfh", totalApprovedWfh + totalUnpprovedWfh);
	        numericalAnalysis.put("totalUnplannedLeave", totalUnplannedLeave);

	        Map<String, Object> finalResponse = new LinkedHashMap<>();
	        finalResponse.put("numericalAnalysis", numericalAnalysis);
	        finalResponse.put("content", summaryList);

	        log.info(this.getClass() + " " + methodName + " " + Constants.END);
	        return finalResponse;

	    } catch (CustomParameterizedException customException) {
	        customException.printStackTrace();
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + customException.getMessage() + " ]");
	        throw customException;
	    } catch (Exception exception) {
	        exception.printStackTrace();
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage() + " ]");
	        throw new CustomParameterizedException(
	                RegisteredException.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
	                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
	                CustomExceptionCode.OPTIMA_LEAVE_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
	    }
	}
	
	
	@Override
	public void sendMonthlyLeaveReport() {
	    try {
	        
	        YearMonth previousMonth = YearMonth.now().minusMonths(1);
	        LocalDate start = previousMonth.atDay(1);
	        LocalDate end = previousMonth.atEndOfMonth();

	        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        Date endDate = Date.from(end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

	        
	        Query query = new Query();
	        query.addCriteria(Criteria.where("start_date").gte(startDate).lte(endDate));

	        query.with(new Sort(Sort.Direction.ASC, "start_date"));
	        List<OptimaLeaveTracker> leaveList = mongoOperations.find(query, OptimaLeaveTracker.class);
	        leaveList = leaveList.stream()
	                .filter(l -> l.getLeaveType() != null)
	                .filter(l -> !"holiday".equalsIgnoreCase(l.getLeaveType()))
	                .collect(Collectors.toList());

	        Query emailQuery = new Query();
	        emailQuery.addCriteria(Criteria.where("operation").is("leavereport")
	                .and(Constants.DELETE_FLAG).is(false));
	        EmailRecipients emailRecipients = mongoOperations.findOne(emailQuery, EmailRecipients.class);
	        

	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("Monthly Leave");

	        Row header = sheet.createRow(0);
	        String[] headers = { "Name", "Start Date", "End Date", "Day", "Leave Type" };
	        for (int i = 0; i < headers.length; i++) {
	            header.createCell(i).setCellValue(headers[i]);
	        }

	        CellStyle dateStyle = workbook.createCellStyle();
	        dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

	        int rowNum = 1;
	        for (OptimaLeaveTracker leave : leaveList) {
	            Row row = sheet.createRow(rowNum++);

	            row.createCell(0).setCellValue(leave.getName());

	            if (leave.getStartDate() != null) {
	                Cell c = row.createCell(2);
	                c.setCellValue(leave.getStartDate());
	                c.setCellStyle(dateStyle);
	            }

	            if (leave.getEndDate() != null) {
	                Cell c = row.createCell(3);
	                c.setCellValue(leave.getEndDate());
	                c.setCellStyle(dateStyle);
	            }

	            row.createCell(4).setCellValue(leave.getDay());
	            row.createCell(5).setCellValue(leave.getLeaveType());
	        }

	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        String fileName = "OptimaLeaveTracker_" + previousMonth.getMonth() + "_" + previousMonth.getYear() + ".xlsx";
	        String filePath = HISTORY_UPLOAD + File.separator + fileName;

	        File uploadDir = new File(HISTORY_UPLOAD);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdirs();
	        }

	        File file = new File(filePath);
	        if (file.exists()) {
	            file.delete();
	        }

	        try (FileOutputStream fos = new FileOutputStream(file)) {
	            workbook.write(fos);
	        }
	        workbook.close();
	        
	        String emailContent =
	                "<!DOCTYPE html><html><head><style>"
	                        + "body { font-family: Arial, sans-serif; color: #555555; font-size: 14px; line-height: 1.6; padding: 20px; }"
	                        + ".footer { margin-top: 30px; font-size: 12px; color: #999999; }"
	                        + "</style></head><body>"
	                        + "<p>Hi Team,</p>"
	                        + "<p>Please find attached the <b>Monthly Leave Report</b> for <b>"
	                        + previousMonth.getMonth() + " " + previousMonth.getYear()
	                        + "</b> from <b>OptimaLeaveTracker</b>.</p>"
	                        + "<p>Thank you!</p>"
	                        + "<p class=\"footer\">This is an automated email — please do not reply directly.</p>"
	                        + "</body></html>";

	        EmailMV emailMV = new EmailMV();
	        emailMV.setTo(emailRecipients.getRecipients());
	        emailMV.setSubject("Monthly Leave Report - " + previousMonth);
	        emailMV.setHtml(true);
	        emailMV.setMultipart(true);
	        emailMV.setContent(emailContent);

	        Map<String, String> fileInfo = new HashMap<>();
	        fileInfo.put(file.getName(), file.getAbsolutePath());
	        emailMV.setFileInfo(fileInfo);

	        genericServicesImpl.sendEmailWithAttachmentWithUploadpath(emailMV);

	        if (file.exists()) {
	            file.delete();
	        }

	    } catch (Exception e) {
	        log.error("Error while sending monthly leave report", e);
	    }
	}



}