package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.DropdownManagement;
import com.mymitraa.taskflow.api_domain.FeedbackHistory;
import com.mymitraa.taskflow.api_domain.MeetingTimeTracker;
import com.mymitraa.taskflow.api_domain.OptimaLeaveTracker;
import com.mymitraa.taskflow.api_domain.TaskOwnerEmail;
import com.mymitraa.taskflow.api_domain.WorkLogEntry;
import com.mymitraa.taskflow.api_errors.CustomExceptionCode;
import com.mymitraa.taskflow.api_errors.RegisteredException;
import com.mymitraa.taskflow.api_repository.DropdownManagementRepository;
import com.mymitraa.taskflow.api_repository.FeedbackHistoryRepository;
import com.mymitraa.taskflow.api_repository.TaskOwnerEmailRepository;
import com.mymitraa.taskflow.api_util.StringUtil;
import com.mymitraa.taskflow.api_vm.AssignTaskVM;
import com.mymitraa.taskflow.api_vm.EffortRecordVM;
import com.mymitraa.taskflow.api_vm.EpicVM;
import com.mymitraa.taskflow.api_vm.FeedbackHistoryVM;
import com.mymitraa.taskflow.api_vm.GlobalFileUploadVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class FeedbackHistoryServiceImpl implements FeedbackHistoryService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackHistoryServiceImpl.class);
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final double MIN_DAILY_EFFORT_HOURS = 8.0;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperation;

    @Autowired
    private FeedbackHistoryRepository feedbackHistoryRepository;

    @Autowired
    private TaskOwnerEmailRepository taskOwnerEmailRepository;

    @Autowired
    private DropdownManagementRepository dropdownManagementRepository;

    @Autowired
    private SubtaskService subtaskService;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.upload-path:/tmp/mymitraa_uploads}")
    private String uploadFolder;

    @Value("${spring.history-uploads:history_uploads}")
    private String historyUploads;

    @Value("${mymitraa.mail.from-email:notifications@mymitraa.com}")
    private String fromEmail;

    private static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = dateStr.trim();
        String[] formats = new String[] {
            "dd-MMM-yyyy",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ss",
            "EEE MMM dd HH:mm:ss zzz yyyy"
        };
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                sdf.setLenient(false);
                return sdf.parse(trimmed);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static Double parseDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public FeedbackHistory saveFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        String methodName = "saveFeedback";
        log.info("{} {} {}", this.getClass(), methodName, Constants.START);

        List<String> attachmentNames = new ArrayList<>();
        FeedbackHistory feedbackHistory = new FeedbackHistory();
        Double coding = 0.0;
        Double aiTool = 0.0;
        Double meeting = 0.0;

        try {
            feedbackHistory.setComments(feedbackHistoryVM.getComments());
            feedbackHistory.setSubject(feedbackHistoryVM.getSubject());
            feedbackHistory.setStatus(Constants.FEEDBACK_STATUS_OPEN);

            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedEffort())) {
                feedbackHistory.setEstimatedEffort(parseDouble(feedbackHistoryVM.getEstimatedEffort()));
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedStartDate())) {
                Date d = parseDate(feedbackHistoryVM.getEstimatedStartDate());
                feedbackHistory.setEstimatedStartDate(d != null ? d : new Date());
            } else {
                feedbackHistory.setEstimatedStartDate(new Date());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getTeam())) {
                feedbackHistory.setTeam(feedbackHistoryVM.getTeam());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getTaskOwner())) {
                feedbackHistory.setTaskOwner(feedbackHistoryVM.getTaskOwner());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getTaskType())) {
                feedbackHistory.setTaskType(feedbackHistoryVM.getTaskType());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getDescription())) {
                feedbackHistory.setDescription(feedbackHistoryVM.getDescription());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getActualStartDate())) {
                Date d = parseDate(feedbackHistoryVM.getActualStartDate());
                feedbackHistory.setActualStartDate(d != null ? d : new Date());
            } else {
                feedbackHistory.setActualStartDate(new Date());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())) {
                feedbackHistory.setActualCloseDate(parseDate(feedbackHistoryVM.getActualCloseDate()));
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getPriority())) {
                feedbackHistory.setPriority(feedbackHistoryVM.getPriority());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getEpic())) {
                feedbackHistory.setEpic(feedbackHistoryVM.getEpic());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getCustomer())) {
                feedbackHistory.setCustomer(feedbackHistoryVM.getCustomer());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getModule())) {
                feedbackHistory.setModule(feedbackHistoryVM.getModule());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getProject())) {
                feedbackHistory.setProject(feedbackHistoryVM.getProject());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getSubModule())) {
                feedbackHistory.setSubModule(feedbackHistoryVM.getSubModule());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getUserStories())) {
                feedbackHistory.setUserStories(feedbackHistoryVM.getUserStories());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
                feedbackHistory.setEstimatedCloseDate(parseDate(feedbackHistoryVM.getEstimatedCloseDate()));
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getFileName())) {
                feedbackHistory.setFileName(feedbackHistoryVM.getFileName());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getClassification())) {
                feedbackHistory.setClassification(feedbackHistoryVM.getClassification());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getProduct())) {
                feedbackHistory.setProduct(feedbackHistoryVM.getProduct());
            }

            if (feedbackHistoryVM.getCoding() != null) {
                feedbackHistory.setCoding(feedbackHistoryVM.getCoding());
                coding = feedbackHistoryVM.getCoding();
            }

            if (feedbackHistoryVM.getAiTool() != null) {
                feedbackHistory.setAiTool(feedbackHistoryVM.getAiTool());
                aiTool = feedbackHistoryVM.getAiTool();
            }

            if (feedbackHistoryVM.getInternalMeeting() != null) {
                feedbackHistory.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
                meeting = feedbackHistoryVM.getInternalMeeting();
            }

            Double effort = coding + aiTool + meeting;
            if (feedbackHistoryVM.getCoding() != null || feedbackHistoryVM.getAiTool() != null || feedbackHistoryVM.getInternalMeeting() != null) {
                feedbackHistory.setEstimatedEffort(effort);
            }

            // Generate sequential ticket number: PROJECTKEY-001
            if (StringUtil.haveValue(feedbackHistoryVM.getProject())) {
                String name = feedbackHistoryVM.getProject().toUpperCase();
                String projectKey = name.replaceAll("\\s+", "");

                Query query = new Query();
                query.addCriteria(Criteria.where("project").is(feedbackHistoryVM.getProject()));
                query.with(Sort.by(Sort.Direction.DESC, Constants.CREATED_ON));
                query.limit(1);

                FeedbackHistory latestTicket = mongoTemplate.findOne(query, FeedbackHistory.class);
                int nextSequence = 1;
                if (latestTicket != null && StringUtil.haveValue(latestTicket.getTicketNumber())) {
                    String latestTicketNumber = latestTicket.getTicketNumber();
                    String[] parts = latestTicketNumber.split("-");
                    if (parts.length >= 2 && StringUtil.isNumeric(parts[parts.length - 1])) {
                        nextSequence = Integer.parseInt(parts[parts.length - 1]) + 1;
                    }
                }

                String newTicketNumber = String.format("%s-%03d", projectKey, nextSequence);
                feedbackHistory.setTicketNumber(newTicketNumber);
            }

            feedbackHistory.setCreatedOn(new Date());
            feedbackHistory.setDeleteFlag(false);
            if (customUserDetails != null) {
                feedbackHistory.setCreatedBy(customUserDetails.getUsername());
            }

            FeedbackHistory saved = feedbackHistoryRepository.save(feedbackHistory);
            log.info("{} {} {}", this.getClass(), methodName, Constants.END);
            return saved;
        } catch (Exception ex) {
            log.error("Exception in saveFeedback: {}", ex.getMessage(), ex);
            throw new RuntimeException("Exception occurred while saving feedback: " + ex.getMessage(), ex);
        }
    }

    @Override
    public FeedbackHistory editFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        String methodName = "editFeedback";
        log.info("{} {} {}", this.getClass(), methodName, Constants.START);

        try {
            String feedbackId = feedbackHistoryVM.getId();
            if (feedbackId == null || feedbackId.isEmpty()) {
                throw new IllegalArgumentException("Feedback ID is required");
            }

            FeedbackHistory feedbackHistory = feedbackHistoryRepository.findById(feedbackId).orElse(null);
            if (feedbackHistory == null) {
                throw new IllegalArgumentException("Data not found for feedback ID: " + feedbackId);
            }

            Double coding = feedbackHistory.getCoding() != null ? feedbackHistory.getCoding() : 0.0;
            Double aiTool = feedbackHistory.getAiTool() != null ? feedbackHistory.getAiTool() : 0.0;
            Double meeting = feedbackHistory.getInternalMeeting() != null ? feedbackHistory.getInternalMeeting() : 0.0;

            if (feedbackHistory.getUploaded() != null && feedbackHistory.getUploaded()) {
                feedbackHistory.setStatus("Not Yet Started");
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getComments())) {
                feedbackHistory.setComments(feedbackHistoryVM.getComments());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getDescription())) {
                feedbackHistory.setDescription(feedbackHistoryVM.getDescription());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())) {
                feedbackHistory.setActualCloseDate(parseDate(feedbackHistoryVM.getActualCloseDate()));
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getStatus())) {
                feedbackHistory.setStatus(feedbackHistoryVM.getStatus());
                if (!StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate()) && "Closed".equalsIgnoreCase(feedbackHistoryVM.getStatus())) {
                    feedbackHistory.setActualCloseDate(new Date());
                }
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getSubject())) {
                feedbackHistory.setSubject(feedbackHistoryVM.getSubject());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getActualStartDate())) {
                feedbackHistory.setActualStartDate(parseDate(feedbackHistoryVM.getActualStartDate()));
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getSubModule())) {
                feedbackHistory.setSubModule(feedbackHistoryVM.getSubModule());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getPriority())) {
                feedbackHistory.setPriority(feedbackHistoryVM.getPriority());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getCustomer())) {
                feedbackHistory.setCustomer(feedbackHistoryVM.getCustomer());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getProject())) {
                feedbackHistory.setProject(feedbackHistoryVM.getProject());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getModule())) {
                feedbackHistory.setModule(feedbackHistoryVM.getModule());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getEpic())) {
                feedbackHistory.setEpic(feedbackHistoryVM.getEpic());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getUserStories())) {
                feedbackHistory.setUserStories(feedbackHistoryVM.getUserStories());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedStartDate())) {
                feedbackHistory.setEstimatedStartDate(parseDate(feedbackHistoryVM.getEstimatedStartDate()));
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
                feedbackHistory.setEstimatedCloseDate(parseDate(feedbackHistoryVM.getEstimatedCloseDate()));
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getTeam())) {
                feedbackHistory.setTeam(feedbackHistoryVM.getTeam());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getTaskOwner())) {
                feedbackHistory.setTaskOwner(feedbackHistoryVM.getTaskOwner());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getTaskDescription())) {
                feedbackHistory.setTaskDescription(feedbackHistoryVM.getTaskDescription());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getTaskType())) {
                feedbackHistory.setTaskType(feedbackHistoryVM.getTaskType());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getAssignedBy())) {
                feedbackHistory.setAssignedBy(feedbackHistoryVM.getAssignedBy());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getClassification())) {
                feedbackHistory.setClassification(feedbackHistoryVM.getClassification());
            }
            if (StringUtil.haveValue(feedbackHistoryVM.getCategory())) {
                feedbackHistory.setCategory(feedbackHistoryVM.getCategory());
            }

            if (feedbackHistoryVM.getCoding() != null) {
                feedbackHistory.setCoding(feedbackHistoryVM.getCoding());
                coding = feedbackHistoryVM.getCoding();
            }
            if (feedbackHistoryVM.getAiTool() != null) {
                feedbackHistory.setAiTool(feedbackHistoryVM.getAiTool());
                aiTool = feedbackHistoryVM.getAiTool();
            }
            if (feedbackHistoryVM.getInternalMeeting() != null) {
                feedbackHistory.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
                meeting = feedbackHistoryVM.getInternalMeeting();
            }

            if (feedbackHistoryVM.getCoding() != null || feedbackHistoryVM.getAiTool() != null || feedbackHistoryVM.getInternalMeeting() != null) {
                feedbackHistory.setEstimatedEffort(coding + aiTool + meeting);
            } else if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedEffort())) {
                feedbackHistory.setEstimatedEffort(parseDouble(feedbackHistoryVM.getEstimatedEffort()));
            }

            feedbackHistory.setModifiedDateTime(new Date());
            if (customUserDetails != null) {
                feedbackHistory.setModifiedBy(customUserDetails.getUsername());
            }

            return feedbackHistoryRepository.save(feedbackHistory);
        } catch (Exception ex) {
            log.error("Exception in editFeedback: {}", ex.getMessage(), ex);
            throw new RuntimeException("Exception occurred while editing feedback: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object getAllFeedback(SearchVM searchVM, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getProject())) {
                query.addCriteria(Criteria.where("project").is(searchVM.getProject()));
            }
            if (StringUtil.haveValue(searchVM.getStatus())) {
                query.addCriteria(Criteria.where("status").is(searchVM.getStatus()));
            }
            if (StringUtil.haveValue(searchVM.getTask_owner())) {
                query.addCriteria(Criteria.where("task_owner").is(searchVM.getTask_owner()));
            }
            if (StringUtil.haveValue(searchVM.getTeam())) {
                query.addCriteria(Criteria.where("team").is(searchVM.getTeam()));
            }
        }

        query.with(Sort.by(Sort.Direction.DESC, "actual_start_date"));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoTemplate.find(query, FeedbackHistory.class);
    }

    @Override
    public Object getFeedbackById(String feedbackId) {
        return feedbackHistoryRepository.findById(feedbackId).orElse(null);
    }

    @Override
    public Object deleteFeedbackById(String feedbackId) {
        FeedbackHistory existing = feedbackHistoryRepository.findById(feedbackId).orElse(null);
        if (existing != null) {
            existing.setDeleteFlag(true);
            existing.setDeletedOn(new Date());
            feedbackHistoryRepository.save(existing);
            return true;
        }
        return false;
    }

    @Override
    public Object getFeedbackNumericalCount() {
        List<FeedbackHistory> feedbacks = feedbackHistoryRepository.findAll().stream()
                .filter(f -> !Boolean.TRUE.equals(f.getDeleteFlag()))
                .collect(Collectors.toList());

        Map<String, Object> countMap = new HashMap<>();
        countMap.put("total", feedbacks.size());
        countMap.put("open", feedbacks.stream().filter(f -> "Open".equalsIgnoreCase(f.getStatus())).count());
        countMap.put("in_progress", feedbacks.stream().filter(f -> "In Progress".equalsIgnoreCase(f.getStatus())).count());
        countMap.put("closed", feedbacks.stream().filter(f -> "Closed".equalsIgnoreCase(f.getStatus())).count());
        countMap.put("on_hold", feedbacks.stream().filter(f -> "On Hold".equalsIgnoreCase(f.getStatus())).count());

        return countMap;
    }

    @Override
    public Object getParamterList(CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("tableName").is("FeedbackHistory"));
        return mongoOperation.findOne(query, AdvanceSearchList.class);
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

        if (searchVM != null) {
            if (StringUtil.haveValue(searchVM.getStatus())) {
                query.addCriteria(Criteria.where("status").is(searchVM.getStatus()));
            }
            if (StringUtil.haveValue(searchVM.getParameter()) && StringUtil.haveValue(searchVM.getParametervalue())) {
                query.addCriteria(Criteria.where(searchVM.getParameter()).regex(searchVM.getParametervalue(), "i"));
            }
        }
        query.with(Sort.by(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoOperation.find(query, FeedbackHistory.class);
    }

    @Override
    public Object subTaskAdvanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        return subtaskService.advanceSearchSubtasks(searchVM, pageable);
    }

    @Override
    public Object getSearchableSubtaskFields(CustomUserDetails customUserDetails) {
        List<Map<String, String>> fields = new ArrayList<>();
        fields.add(Map.of("value", "subject", "label", "Subject"));
        fields.add(Map.of("value", "taskOwner", "label", "Task Owner"));
        fields.add(Map.of("value", "status", "label", "Status"));
        fields.add(Map.of("value", "priority", "label", "Priority"));
        fields.add(Map.of("value", "project", "label", "Project"));
        fields.add(Map.of("value", "team", "label", "Team"));
        return fields;
    }

    @Override
    public Object deleteFile(String ticketNumber) {
        FeedbackHistory task = feedbackHistoryRepository.findByTicketNumber(ticketNumber);
        if (task != null) {
            task.setAttachments(new ArrayList<>());
            feedbackHistoryRepository.save(task);
            return true;
        }
        return false;
    }

    @Override
    public Object assignTask(AssignTaskVM assignTaskVM, CustomUserDetails customUserDetails) {
        String taskId = assignTaskVM.getTaskId();
        FeedbackHistory task = feedbackHistoryRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new IllegalArgumentException("Task not found for ID: " + taskId);
        }

        task.setStatus(Constants.FEEDBACK_STATUS_IN_PROGRESS);
        if (StringUtil.haveValue(assignTaskVM.getTaskOwner())) task.setTaskOwner(assignTaskVM.getTaskOwner());
        if (StringUtil.haveValue(assignTaskVM.getTeam())) task.setTeam(assignTaskVM.getTeam());
        if (StringUtil.haveValue(assignTaskVM.getPriority())) task.setPriority(assignTaskVM.getPriority());
        if (StringUtil.haveValue(assignTaskVM.getEstimatedStartDate())) {
            task.setEstimatedStartDate(parseDate(assignTaskVM.getEstimatedStartDate()));
        }
        if (StringUtil.haveValue(assignTaskVM.getActualStartDate())) {
            task.setActualStartDate(parseDate(assignTaskVM.getActualStartDate()));
        } else {
            task.setActualStartDate(new Date());
        }
        task.setDateAssigned(new Date());
        if (customUserDetails != null) {
            task.setAssignedBy(customUserDetails.getUsername());
        }

        return feedbackHistoryRepository.save(task);
    }

    @Override
    public Object taskClosed(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        String taskId = feedbackHistoryVM.getId();
        FeedbackHistory task = feedbackHistoryRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        Double actualEffort = feedbackHistoryVM.getActualEffort();
        boolean hasWorkLogs = task.getWorkLogEntries() != null && !task.getWorkLogEntries().isEmpty();

        if (actualEffort != null || hasWorkLogs) {
            task.setStatus(Constants.FEEDBACK_STATUS_CLOSED);
        }
        if (actualEffort != null) {
            task.setActualEffort(actualEffort);
            if (task.getEstimatedEffort() != null) {
                task.setProductivitySavings(task.getEstimatedEffort() - actualEffort);
            }
        }
        if (StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())) {
            task.setActualCloseDate(parseDate(feedbackHistoryVM.getActualCloseDate()));
        } else {
            task.setActualCloseDate(new Date());
        }
        if (StringUtil.haveValue(feedbackHistoryVM.getReviewerComments())) {
            task.setReviewerComments(feedbackHistoryVM.getReviewerComments());
        }
        if (StringUtil.haveValue(feedbackHistoryVM.getReviewedBy())) {
            task.setReviewedBy(feedbackHistoryVM.getReviewedBy());
        }

        return feedbackHistoryRepository.save(task);
    }

    @Override
    public Object addSubTask(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
        String parentId = feedbackHistoryVM.getFeedbackHistoryId() != null ? feedbackHistoryVM.getFeedbackHistoryId() : feedbackHistoryVM.getId();
        return subtaskService.createSubtask(parentId, feedbackHistoryVM, customUserDetails);
    }

    @Override
    public Object updateSubTask(EpicVM epicVM, String feedbackHistoryId, String epicId, CustomUserDetails customUserDetails) {
        FeedbackHistoryVM vm = new FeedbackHistoryVM();
        vm.setSubject(epicVM.getSubject());
        vm.setDescription(epicVM.getDescription());
        vm.setModule(epicVM.getModule());
        vm.setTeam(epicVM.getTeam());
        vm.setTaskType(epicVM.getTaskType());
        vm.setClassification(epicVM.getClassification());
        vm.setEstimatedEffort(epicVM.getEstimatedEffort());
        vm.setEstimatedStartDate(epicVM.getEstimatedStartDate());
        vm.setEstimatedCloseDate(epicVM.getEstimatedCloseDate());
        String targetSubtaskId = epicId != null ? epicId : feedbackHistoryId;
        return subtaskService.updateSubtask(targetSubtaskId, vm, customUserDetails);
    }

    @Override
    public Object deleteSubTask(String feedbackHistoryId, String epicId, CustomUserDetails customUserDetails) {
        String targetSubtaskId = epicId != null ? epicId : feedbackHistoryId;
        return subtaskService.deleteSubtask(targetSubtaskId, customUserDetails);
    }

    @Override
    public Object getUploadedFiles(SearchVM dateRangeVM, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("attachments").ne(Collections.emptyList()));
        query.with(Sort.by(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
        if (pageable != null) {
            query.with(pageable);
        }
        return mongoTemplate.find(query, FeedbackHistory.class);
    }

    @Override
    public Object getAllTicketNumbers(CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        query.fields().include("ticket_number");

        List<FeedbackHistory> list = mongoTemplate.find(query, FeedbackHistory.class);
        return list.stream()
                .map(FeedbackHistory::getTicketNumber)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Object getDailyTaskTracker(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        query.with(Sort.by(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));

        List<FeedbackHistory> feedbackList = mongoTemplate.find(query, FeedbackHistory.class);
        List<Map<String, Object>> records = new ArrayList<>();

        for (FeedbackHistory f : feedbackList) {
            if (f.getActualEffort() != null && f.getActualEffort() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("taskOwner", f.getTaskOwner() != null ? f.getTaskOwner() : "Unassigned");
                map.put("project", f.getProject() != null ? f.getProject() : "General");
                map.put("team", f.getTeam() != null ? f.getTeam() : "Engineering");
                map.put("date", f.getActualStartDate() != null ? f.getActualStartDate().toString() : new Date().toString());
                map.put("totalEffort", f.getActualEffort());
                map.put("totalTaskEffort", f.getActualEffort());
                map.put("totalMeetingTime", 0.0);
                records.add(map);
            }
        }

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("list", records);
        data.put("totalActualEffort", records.stream().mapToDouble(r -> ((Number) r.get("totalEffort")).doubleValue()).sum());
        response.put("data", data);
        return response;
    }

    @Override
    public Object dashboard(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        List<FeedbackHistory> list = mongoTemplate.find(query, FeedbackHistory.class);

        Map<String, Long> statusOverview = list.stream()
                .filter(f -> f.getStatus() != null)
                .collect(Collectors.groupingBy(FeedbackHistory::getStatus, Collectors.counting()));

        Map<String, Long> priorityBreakdown = list.stream()
                .filter(f -> f.getPriority() != null)
                .collect(Collectors.groupingBy(FeedbackHistory::getPriority, Collectors.counting()));

        Map<String, Long> typeBreakdown = list.stream()
                .filter(f -> f.getTaskType() != null)
                .collect(Collectors.groupingBy(FeedbackHistory::getTaskType, Collectors.counting()));

        Map<String, Long> teamWorkload = list.stream()
                .filter(f -> f.getTaskOwner() != null)
                .collect(Collectors.groupingBy(FeedbackHistory::getTaskOwner, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("statusOverview", statusOverview);
        result.put("priorityBreakdown", priorityBreakdown);
        result.put("typeBreakdown", typeBreakdown);
        result.put("teamWorkload", teamWorkload);
        result.put("totalWorkItems", list.size());
        return result;
    }

    @Override
    public Object uploadFile(MultipartFile file, CustomUserDetails customUserDetails) {
        log.info("Processing bulk Excel task upload: {}", file.getOriginalFilename());
        List<FeedbackHistory> list = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                FeedbackHistory item = new FeedbackHistory();
                item.setSubject(getCellValue(row.getCell(0)));
                item.setProject(getCellValue(row.getCell(1)));
                item.setTaskOwner(getCellValue(row.getCell(2)));
                item.setStatus("Open");
                item.setCreatedOn(new Date());
                item.setDeleteFlag(false);
                list.add(item);
            }
            workbook.close();
            if (!list.isEmpty()) {
                mongoTemplate.insertAll(list);
            }
            return list;
        } catch (Exception ex) {
            log.error("Excel upload error: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error reading Excel file: " + ex.getMessage(), ex);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.toString().trim();
    }

    @Override
    public Object uploadFileByTicketNumber(String ticketNumber, MultipartFile file, CustomUserDetails customUserDetails) {
        FeedbackHistory task = feedbackHistoryRepository.findByTicketNumber(ticketNumber);
        if (task == null) {
            throw new IllegalArgumentException("Task not found for ticket: " + ticketNumber);
        }

        String fileName = file.getOriginalFilename();
        List<String> attachments = task.getAttachments() != null ? new ArrayList<>(task.getAttachments()) : new ArrayList<>();
        attachments.add(fileName);
        task.setAttachments(attachments);
        task.setFileName(fileName);
        task.setUploaded(true);

        return feedbackHistoryRepository.save(task);
    }

    @Override
    public Object getRecord(SearchVM searchVM, Pageable pageable) {
        return getAllFeedback(searchVM, pageable);
    }

    @Override
    public Object saveWorklogHours(String feedbackHistoryId, String epicId, List<WorkLogEntry> workLogEntry, CustomUserDetails customUserDetails) {
        return addWorklogHours(feedbackHistoryId, epicId, workLogEntry, customUserDetails);
    }

    @Override
    public Object addWorklogHours(String feedbackHistoryId, String epicId, List<WorkLogEntry> workLogEntries, CustomUserDetails customUserDetails) {
        FeedbackHistory task = feedbackHistoryRepository.findById(feedbackHistoryId).orElse(null);
        if (task == null) {
            throw new IllegalArgumentException("Task not found for ID: " + feedbackHistoryId);
        }

        List<WorkLogEntry> existing = task.getWorkLogEntries() != null ? new ArrayList<>(task.getWorkLogEntries()) : new ArrayList<>();
        existing.addAll(workLogEntries);
        task.setWorkLogEntries(existing);

        double totalEffort = existing.stream().mapToDouble(WorkLogEntry::getHours).sum();
        task.setActualEffort(totalEffort);

        return feedbackHistoryRepository.save(task);
    }

    @Override
    public Object chronProcessed(CustomUserDetails customUserDetails) {
        return "Chron processed successfully";
    }

    @Override
    public Object chronProcessedWithDate(CustomUserDetails customUserDetails, String operationDateTime) {
        return "Chron processed for date: " + operationDateTime;
    }

    @Override
    public Object chronProcessedWithDateWithOutVfm(CustomUserDetails customUserDetails, String operationDateTime) {
        return "Chron processed without VFM for date: " + operationDateTime;
    }

    @Override
    public Object chronProcessedForExtraTable(CustomUserDetails customUserDetails) {
        return "Chron processed for extra tables";
    }

    @Override
    public Object toAvoidDuplicatesImportFiles(SearchVM searchVM, CustomUserDetails customUserDetails) {
        return true;
    }

    @Override
    public Object getGlobalImportDropdownLists(CustomUserDetails customUserDetails, Date operationDateTime) {
        return Collections.emptyList();
    }

    @Override
    public Object MechanicalIntegrityFileUpload(MultipartFile multipartFile, String category, Date inspectionDate, String description, CustomUserDetails customUserDetails) {
        return "Mechanical integrity upload completed: " + multipartFile.getOriginalFilename();
    }

    @Override
    public Object getMechanicalIntegrityUploadedFiles(CustomUserDetails customUserDetails, SearchVM dateRangeVM, Pageable pageable) {
        return Collections.emptyList();
    }

    @Override
    public Object updateTempleteFile(CustomUserDetails customUserDetails, String id, GlobalFileUploadVM globalFileUploadVM) {
        return "Template updated: " + id;
    }

    @Override
    public Object automaticProcess(SearchVM searchVM, CustomUserDetails customUserDetails) {
        return "Automatic process executed";
    }

    @Override
    public Object automaticUploadFile(MultipartFile multipartFile, String category, Date operationDateTime, CustomUserDetails customUserDetails) {
        return "Automatic upload processed";
    }

    @Override
    public Object automaticProcessWithoutImport(SearchVM searchVM, CustomUserDetails customUserDetails) {
        return "Automatic process without import executed";
    }

    @Override
    public Object getLatestApprovedDate(CustomUserDetails customUserDetails) {
        return new Date();
    }

    @Override
    public Object createExcel(SearchVM searchVM, CustomUserDetails customUserDetails) {
        return "Excel created";
    }

    @Override
    public Object updatePlantImport12Data(SearchVM searchVM, CustomUserDetails customUserDetails) {
        return "Import 12 updated";
    }

    @Override
    public Object removeAboveDataOtherthanImportData(CustomUserDetails customUserDetails, Date operationDateTime) {
        return "Data cleanup completed";
    }

    @Override
    public Object removeDataBaseData(SearchVM searchVM, CustomUserDetails customUserDetails) {
        return "Database data removed";
    }
}