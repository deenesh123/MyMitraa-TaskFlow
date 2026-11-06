package com.kelltontech.optima.api_service.impl;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.kelltontech.optima.api_domain.*;
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
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kelltontech.optima.api_errors.CustomExceptionCode;
import com.kelltontech.optima.api_errors.RegisteredException;
import com.kelltontech.optima.api_mv.EmailMV;
import com.kelltontech.optima.api_mv.PaginationDataMV;
import com.kelltontech.optima.api_repository.DropdownManagementRepository;
import com.kelltontech.optima.api_repository.SubtaskRepository;
import com.kelltontech.optima.api_repository.TaskOwnerEmailRepository;
import com.kelltontech.optima.api_service.FeedbackHistoryService;
import com.kelltontech.optima.api_service.SubtaskService;
import com.kelltontech.optima.api_util.StringUtil;
import com.kelltontech.optima.api_util.WellServiceAuditUtil;
import com.kelltontech.optima.api_vm.AssignTaskVM;
import com.kelltontech.optima.api_vm.EffortRecordVM;
import com.kelltontech.optima.api_vm.EpicVM;
import com.kelltontech.optima.api_vm.FeedbackHistoryVM;
import com.kelltontech.optima.config.Constants;
import com.kelltontech.optima.repository.CompletionFilesRepository;
import com.kelltontech.optima.repository.FeedbackHistoryRepository;
import com.kelltontech.optima.security.CustomUserDetails;
import com.kelltontech.optima.web.rest.errors.CustomParameterizedException;
import com.kelltontech.optima.web.rest.vm.CompletionFilesVM;
import com.kelltontech.optima.web.rest.vm.SearchVM;

@Service
public class FeedbackHistoryServiceImpl implements FeedbackHistoryService {

	private final Logger log = LoggerFactory.getLogger(FeedbackHistoryServiceImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GenericServicesImpl genericServicesImpl;

	@Autowired
	private FeedbackHistoryRepository feedbackHistoryRepository;

	@Autowired
	private CompletionFilesRepository completionFilesRepository;

	@Autowired
	private TaskOwnerEmailRepository taskOwnerEmailRepository;

	@Autowired
	private DropdownManagementRepository dropdownManagementRepository;

	@Autowired
	private WellServiceAuditUtil wellServiceAuditUtil;

	@Autowired
	private MongoOperations mongoOperation;

	@Autowired
	private SubtaskService subtaskService;

	@Autowired
	private FileUploadServiceImpl fileUploadServiceImpl;

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final double MIN_DAILY_EFFORT_HOURS = 8.0;
    private static final String WEEKLY_TEMPLATE_PATH = "templates/weeklyEffortAlert.html";
    private static final String WEEKLY_ROW_TEMPLATE_PATH = "templates/weeklyEffortRow.html";


    @Autowired
	private EmailService emailService;

	@Autowired
	private SubtaskRepository subtaskRepository;

	private final Environment env;
	final String UPLOAD_FOLDER;
	final String HISTORY_UPLOADS;

	public FeedbackHistoryServiceImpl(Environment env) {
		this.env = env;
		UPLOAD_FOLDER = env.getProperty("spring.upload-path");
		HISTORY_UPLOADS = env.getProperty("spring.history-uploads");
	}

	@Override
	public FeedbackHistory saveFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "saveFeedback";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		CompletionFilesVM completionFilesVM = null;

		List<String> attachmentNames = new ArrayList<>();
		Map<String, String> fileInfo = new HashMap<>();
		FeedbackHistory feedbackHistory = null;
        Double coding = 0.0;
        Double aiTool = 0.0;
        Double meeting = 0.0;
		try {
            feedbackHistory = new FeedbackHistory();
            feedbackHistory.setComments(feedbackHistoryVM.getComments());
            feedbackHistory.setSubject(feedbackHistoryVM.getSubject());
            feedbackHistory.setStatus(Constants.FEEDBACK_STATUS_OPEN);

            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedEffort())) {
                feedbackHistory.setEstimatedEffort(feedbackHistoryVM.getEstimatedEffort());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedStartDate())) {
                feedbackHistory.setEstimatedStartDate(
                    StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedStartDate()));
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
                feedbackHistory.setActualStartDate(
                    StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getActualStartDate()));
            } else {
                feedbackHistory.setActualStartDate(new Date());
            }

            if (StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())) {
                feedbackHistory.setActualCloseDate(
                    StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getActualCloseDate()));
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
                feedbackHistory.setEstimatedCloseDate(
                    StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedCloseDate()));
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
            } else {
                feedbackHistory.setEstimatedEffort(feedbackHistoryVM.getEstimatedEffort());
            }


            // Generate ticket number
            if (StringUtil.haveValue(feedbackHistoryVM.getProject())) {
                String name = feedbackHistoryVM.getProject().toUpperCase();
                String projectKey = name.replaceAll("\\s+", "");

                Query query = new Query();
                query.addCriteria(Criteria.where("project").is(feedbackHistoryVM.getProject()));
                query.with(new Sort(Sort.Direction.DESC, Constants.CREATED_ON));
                query.limit(1);

                FeedbackHistory latestTicket = mongoTemplate.findOne(query, FeedbackHistory.class);

                int nextSequence = 1;
                if (latestTicket != null && StringUtil.haveValue(latestTicket.getTicketNumber())) {
                    String latestTicketNumber = latestTicket.getTicketNumber();
                    String[] parts = latestTicketNumber.split("-");
                    if (parts.length == 2 && StringUtil.isNumeric(parts[1])) {
                        nextSequence = Integer.parseInt(parts[1]) + 1;
                    }
                }

                String newTicketNumber = String.format("%s-%03d", projectKey, nextSequence);
                feedbackHistory.setTicketNumber(newTicketNumber);
            }

            if (feedbackHistoryVM.getAttachment() != null && !feedbackHistoryVM.getAttachment().isEmpty()) {
                for (MultipartFile file : feedbackHistoryVM.getAttachment()) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();

                        completionFilesVM = new CompletionFilesVM();
                        completionFilesVM.setMultipartFile(file);
                        completionFilesVM.setCategory(Constants.FEEDBACK_CATEGORY);
                        completionFilesVM.setComments(Constants.FEEDBACK_COMMENTS);
                        log.info("Uploading file: " + originalFilename);
                        DCompletionFileInfo uploadedFile = fileUploadServiceImpl.uploadCompletionFile(completionFilesVM,
                            feedbackHistory.getTicketNumber(), customUserDetails);
                        String relativePath = uploadedFile.getFileName();

                        fileInfo.put(originalFilename, relativePath);
                        attachmentNames.add(relativePath);
                    }
                }
            }
            feedbackHistory.setAttachments(attachmentNames);

            mongoTemplate.save(feedbackHistory);

            log.info(this.getClass() + " " + methodName + " " + Constants.END);
            return feedbackHistory;

        } catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public FeedbackHistory editFeedback(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "editFeedback";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		FeedbackHistory feedbackHistory = null;
		try {
			if (feedbackHistoryVM.getFeedbackHistoryId() == null
					|| feedbackHistoryVM.getFeedbackHistoryId().isEmpty()) {
				throw new CustomParameterizedException(RegisteredException.FEED_BACK_ID_REQUIRED.getException(),
						CustomExceptionCode.FEEDBACK_ID_REQUIRED.getErrMsg(),
						CustomExceptionCode.FEEDBACK_ID_REQUIRED.getErrCode());
			}

			feedbackHistory = feedbackHistoryRepository
					.findByFeedbackHistoryIdAndDeleteFlag(feedbackHistoryVM.getFeedbackHistoryId(), false);

			if (feedbackHistory == null) {
				throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
						CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
						CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
			} else {
                Double coding = feedbackHistory.getCoding() != null ? feedbackHistory.getCoding() : 0.0;
                Double aiTool = feedbackHistory.getAiTool() != null ? feedbackHistory.getAiTool() : 0.0;
                Double meeting = feedbackHistory.getInternalMeeting() != null ? feedbackHistory.getInternalMeeting() : 0.0;
				// For uploaded tasks, when editing, set status to "Not Yet Started"
				if (feedbackHistory.getUploaded() != null && feedbackHistory.getUploaded() == true) {
					feedbackHistory.setStatus("Not Yet Started");
				}

				if (feedbackHistoryVM.getComments() != null && !feedbackHistoryVM.getComments().isEmpty()) {
					feedbackHistory.setComments(feedbackHistoryVM.getComments());
				}
				if (feedbackHistoryVM.getDescription() != null && !feedbackHistoryVM.getDescription().isEmpty()) {
					feedbackHistory.setDescription(feedbackHistoryVM.getDescription());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())) {
					feedbackHistory.setActualCloseDate(
							StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getActualCloseDate()));
				}
				if (feedbackHistoryVM.getStatus() != null && !feedbackHistoryVM.getStatus().isEmpty()) {
					feedbackHistory.setStatus(feedbackHistoryVM.getStatus());
					if (!StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())
							&& feedbackHistoryVM.getStatus().equalsIgnoreCase("Closed")) {
						feedbackHistory.setActualCloseDate(new Date());
					}
				}
				if (feedbackHistoryVM.getSubject() != null && !feedbackHistoryVM.getSubject().isEmpty()) {
					feedbackHistory.setSubject(feedbackHistoryVM.getSubject());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getActualStartDate())) {
					feedbackHistory.setActualStartDate(
							StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getActualStartDate()));
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
					feedbackHistory.setEstimatedStartDate(
							StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedStartDate()));
				} else if (feedbackHistory.getEstimatedStartDate() == null) {
					feedbackHistory.setEstimatedStartDate(new Date());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
					feedbackHistory.setEstimatedCloseDate(
							StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedCloseDate()));
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
				if (StringUtil.haveValue(feedbackHistoryVM.getCategoryDescription())) {
					feedbackHistory.setCategoryDescription(feedbackHistoryVM.getCategoryDescription());
				} else if (StringUtil.haveValue(feedbackHistoryVM.getSubject())) {
					feedbackHistory.setCategoryDescription(feedbackHistoryVM.getSubject());
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
                } else {
                    feedbackHistory.setEstimatedEffort(feedbackHistoryVM.getEstimatedEffort());
                }

				if (StringUtil.haveValue(feedbackHistoryVM.getWhoReported())) {
					feedbackHistory.setWhoReported(feedbackHistoryVM.getWhoReported());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getReviewerComments())) {
					feedbackHistory.setReviewerComments(feedbackHistoryVM.getReviewerComments());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getReviewedBy())) {
					feedbackHistory.setReviewedBy(feedbackHistoryVM.getReviewedBy());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getFileName())) {
					feedbackHistory.setFileName(feedbackHistoryVM.getFileName());
				}

                if (StringUtil.haveValue(feedbackHistoryVM.getProduct())) {
                    feedbackHistory.setProduct(feedbackHistoryVM.getProduct());
                }

				feedbackHistoryRepository.save(feedbackHistory);
			}
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return feedbackHistory;

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Object getAllFeedback(SearchVM searchVM, Pageable pageable) {
		String methodName = "getAllFeedback";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		PaginationDataMV paginationDataMV = null;
		Query query = new Query();
		long noOfDocs;
		try {
			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

			if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
				Date startDate = Date.from(
						LocalDate.parse(searchVM.getStartDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
								.atStartOfDay().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
				Date endDate = Date
						.from(LocalDate.parse(searchVM.getEndDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
								.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
				query.addCriteria(new Criteria().andOperator(Criteria.where("actual_start_date").gte(startDate),
						Criteria.where("actual_close_date").lte(endDate)));
			}

			noOfDocs = mongoOperation.count(query, FeedbackHistory.class);
			query.with(new Sort(Sort.Direction.DESC, "actual_start_date"));
			query.with(pageable);
			List<FeedbackHistory> list = mongoTemplate.find(query, FeedbackHistory.class);

			paginationDataMV = new PaginationDataMV();
			paginationDataMV.setContent(list);
			paginationDataMV.setNumber(pageable.getPageNumber());
			paginationDataMV.setNumberOfElements(list.size());
			paginationDataMV.setSize(pageable.getPageSize());
			paginationDataMV.setTotalElements(noOfDocs);
			paginationDataMV.setTotalPages((int) Math.ceil((double) noOfDocs / pageable.getPageSize()));

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return paginationDataMV;

		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Map<String, Long> getFeedbackNumericalCount() {
		String methodName = "getFeedbackNumericalCount";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Map<String, Long> feedbackMap = new HashMap<>();
		try {
			List<FeedbackHistory> feedbacks = feedbackHistoryRepository.findByDeleteFlag(false);
			feedbackMap.put("total", Long.valueOf(feedbacks.size()));
			feedbackMap.put(Constants.FEEDBACK_STATUS_OPEN, feedbacks.stream()
					.filter(feedback -> feedback.getStatus().equals(Constants.FEEDBACK_STATUS_OPEN)).count());
			feedbackMap.put(Constants.FEEDBACK_STATUS_ACKNOWLEDGED, feedbacks.stream()
					.filter(feedback -> feedback.getStatus().equals(Constants.FEEDBACK_STATUS_ACKNOWLEDGED)).count());
			feedbackMap.put(Constants.FEEDBACK_STATUS_CLOSED, feedbacks.stream()
					.filter(feedback -> feedback.getStatus().equals(Constants.FEEDBACK_STATUS_CLOSED)).count());
			feedbackMap.put(Constants.FEEDBACK_STATUS_FEATURE_RELEASED,
					feedbacks.stream()
							.filter(feedback -> feedback.getStatus().equals(Constants.FEEDBACK_STATUS_FEATURE_RELEASED))
							.count());

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return feedbackMap;

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}


	@Override
	public FeedbackHistory getFeedbackById(String feedbackId) {
		String methodName = "getFeedbackById";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		FeedbackHistory feedbackHistory = null;
		try {
			feedbackHistory = feedbackHistoryRepository.findByFeedbackHistoryIdAndDeleteFlag(feedbackId, false);
			if (feedbackHistory == null) {
				throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
						CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
						CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
			}
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return feedbackHistory;

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public FeedbackHistory deleteFeedbackById(String feedbackId) {
		String methodName = "deleteFeedbackById";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		FeedbackHistory feedbackHistoryType = null;
		try {
			feedbackHistoryType = feedbackHistoryRepository.findByFeedbackHistoryIdAndDeleteFlag(feedbackId, false);
			if (feedbackHistoryType != null) {
				feedbackHistoryType.setDeleteFlag(true);
				feedbackHistoryRepository.save(feedbackHistoryType);
			}
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return feedbackHistoryType;

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Object deleteFile(String ticketNumber) {
		String methodName = "deleteFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		List<String> deletedFileNames = new ArrayList<>();
		FeedbackHistory feedbackHistory = null;
		try {
			boolean isSubTask = ticketNumber.matches("^[A-Z]+-\\d+-\\d+$");
			String parentTicket = isSubTask ? ticketNumber.substring(0, ticketNumber.lastIndexOf('-')) : ticketNumber;

			feedbackHistory = feedbackHistoryRepository.findByTicketNumberAndDeleteFlag(parentTicket, false);
			if (feedbackHistory == null) {
				throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
						CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
						CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
			}

			boolean updated = false;

			if (!isSubTask) {
				List<String> mainAttachments = feedbackHistory.getAttachments();
				if (mainAttachments != null && !mainAttachments.isEmpty()) {
					for (String attachment : new ArrayList<>(mainAttachments)) {
						File actualFile = new File(UPLOAD_FOLDER + File.separator + attachment);
						if (actualFile.exists())
							actualFile.delete();

						DCompletionFileInfo file = completionFilesRepository
								.findByFileNameAndWellIdAndDeleteFlag(attachment, ticketNumber, false);
						if (file != null) {
							file.setDeleteFlag(true);
							completionFilesRepository.save(file);
							deletedFileNames.add(attachment);
						}
					}
					mainAttachments.clear();
					updated = true;
				}
			} else {
				Subtask subtask = subtaskService.getSubtaskByTicketNumber(ticketNumber);
				if (subtask != null && subtask.getStoredAttachmentNames() != null) {
					for (String attachment : new ArrayList<>(subtask.getStoredAttachmentNames())) {
						File actualFile = new File(UPLOAD_FOLDER + File.separator + attachment);
						if (actualFile.exists())
							actualFile.delete();

						DCompletionFileInfo file = completionFilesRepository
								.findByFileNameAndWellIdAndDeleteFlag(attachment, ticketNumber, false);
						if (file != null) {
							file.setDeleteFlag(true);
							completionFilesRepository.save(file);
							deletedFileNames.add(attachment);
						}
					}
					subtask.getStoredAttachmentNames().clear();
					updated = true;
				}
			}

			if (updated) {
				feedbackHistoryRepository.save(feedbackHistory);
			}
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return feedbackHistory;

		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Object assignTask(AssignTaskVM assignTaskVM, CustomUserDetails customUserDetails) {
		String methodName = "assignTask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			if (StringUtil.haveValue(assignTaskVM.getEpicId())) {
				Subtask assignedSubtask = subtaskService.assignSubtask(assignTaskVM.getEpicId(), assignTaskVM,
						customUserDetails);
				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return assignedSubtask;
			} else {
				FeedbackHistory task = feedbackHistoryRepository
						.findByFeedbackHistoryIdAndDeleteFlag(assignTaskVM.getTaskId(), false);
				if (task == null) {
					throw new CustomParameterizedException(RegisteredException.TASK_NOT_FOUND_EXCEPTION.getException(),
							CustomExceptionCode.TASK_NOT_FOUND_EXCEPTION.getErrMsg(),
							CustomExceptionCode.TASK_NOT_FOUND_EXCEPTION.getErrCode());
				}

				task.setStatus(Constants.FEEDBACK_STATUS_IN_PROGRESS);

				if (StringUtil.haveValue(assignTaskVM.getClassification())) {
					task.setClassification(assignTaskVM.getClassification());
				}
				if (StringUtil.haveValue(assignTaskVM.getCategory())) {
					task.setCategory(assignTaskVM.getCategory());
				}
				if (StringUtil.haveValue(assignTaskVM.getCategoryDescription())) {
					task.setCategoryDescription(assignTaskVM.getCategoryDescription());
				} else if (StringUtil.haveValue(task.getSubject())) {
					task.setCategoryDescription(task.getSubject());
				}
				if (StringUtil.haveValue(assignTaskVM.getTaskDescription())) {
					task.setTaskDescription(assignTaskVM.getTaskDescription());
				} else if (StringUtil.haveValue(task.getSubject())) {
					task.setTaskDescription(task.getSubject());
				}
				if (StringUtil.haveValue(assignTaskVM.getEstimatedEffort())) {
					task.setEstimatedEffort(assignTaskVM.getEstimatedEffort());
				}
				if (StringUtil.haveValue(assignTaskVM.getTaskOwner())) {
					task.setTaskOwner(assignTaskVM.getTaskOwner());
				}
				if (StringUtil.haveValue(assignTaskVM.getTeam())) {
					task.setTeam(assignTaskVM.getTeam());
				}
				if (StringUtil.haveValue(assignTaskVM.getTaskType())) {
					task.setTaskType(assignTaskVM.getTaskType());
				}
				if (StringUtil.haveValue(customUserDetails.getUsername())) {
					task.setAssignedBy(customUserDetails.getUsername());
				}
				if (StringUtil.haveValue(assignTaskVM.getEstimatedStartDate())) {
					task.setEstimatedStartDate(
							StringUtil.convertStringIntoDatePattern(assignTaskVM.getEstimatedStartDate()));
				}
				if (StringUtil.haveValue(assignTaskVM.getWhoReported())) {
					task.setWhoReported(assignTaskVM.getWhoReported());
				}
				if (StringUtil.haveValue(assignTaskVM.getActualStartDate())) {
					task.setActualStartDate(StringUtil.convertStringIntoDatePattern(assignTaskVM.getActualStartDate()));
				} else {
					task.setActualStartDate(new Date());
				}
				task.setDateAssigned(new Date());

				wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
						task.getFeedbackHistoryId(), "Parent task assigned to: " + assignTaskVM.getTaskOwner(), "");

				task = feedbackHistoryRepository.save(task);
				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return task;
			}
		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " EXCEPTION [ " + customException.getMessage() + " ]");
			throw customException;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " EXCEPTION [ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.TASK_ASSIGN_EXCEPTION.getException(),
					CustomExceptionCode.TASK_ASSIGN_EXCEPTION.getErrMsg(),
					CustomExceptionCode.TASK_ASSIGN_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object taskClosed(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "closeTask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
        Double coding = 0.0;
        Double aiTool = 0.0;
        Double meeting = 0.0;
        Double actualEffortToSet = 0.0;
		try {
			if (StringUtil.haveValue(feedbackHistoryVM.getEpicId())) {
				Subtask closedSubtask = subtaskService.closeSubtask(feedbackHistoryVM.getEpicId(), feedbackHistoryVM,
						customUserDetails);
				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return closedSubtask;
			} else {
				FeedbackHistory task = feedbackHistoryRepository
						.findByFeedbackHistoryIdAndDeleteFlag(feedbackHistoryVM.getFeedbackHistoryId(), false);
				if (task == null) {
					throw new CustomParameterizedException(RegisteredException.TASK_NOT_FOUND_EXCEPTION.getException(),
							CustomExceptionCode.TASK_NOT_FOUND_EXCEPTION.getErrMsg(),
							CustomExceptionCode.TASK_NOT_FOUND_EXCEPTION.getErrCode());
				}

                if (feedbackHistoryVM.getCoding() != null) {
                    task.setCoding(feedbackHistoryVM.getCoding());
                    coding = feedbackHistoryVM.getCoding();
                }

                if (feedbackHistoryVM.getAiTool() != null) {
                    task.setAiTool(feedbackHistoryVM.getAiTool());
                    aiTool = feedbackHistoryVM.getAiTool();
                }

                if (feedbackHistoryVM.getInternalMeeting() != null) {
                    task.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
                    meeting = feedbackHistoryVM.getInternalMeeting();
                }

                Double effort = coding + aiTool + meeting;

				boolean hasWorkLogs = task.getWorkLogEntries() != null && !task.getWorkLogEntries().isEmpty();
                if(effort != null){
                    actualEffortToSet = effort;
                } else{
                    actualEffortToSet = task.getActualEffort();
                }


				if (!StringUtil.haveValue(actualEffortToSet)) {
					if (actualEffortToSet <= task.getEstimatedEffort()) {
						throw new CustomParameterizedException(RegisteredException.TASK_CLOSE_EXCEPTION.getException(),
								"Actual Effort must be greater than Estimated Effort",
								CustomExceptionCode.TASK_CLOSE_EXCEPTION.getErrCode());
					}
				}

				if (actualEffortToSet != null || hasWorkLogs) {
					task.setStatus(Constants.FEEDBACK_STATUS_CLOSED);
				}

				if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
					task.setEstimatedCloseDate(
							StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedCloseDate()));
				}
				if (!hasWorkLogs && actualEffortToSet != null) {
					task.setActualEffort(actualEffortToSet);
					if (task.getEstimatedEffort() != null) {
						task.setProductivitySavings(task.getEstimatedEffort() - actualEffortToSet);
					}
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getReviewerComments())) {
					task.setReviewerComments(feedbackHistoryVM.getReviewerComments());
				}
				if (StringUtil.haveValue(feedbackHistoryVM.getReviewedBy())) {
					task.setReviewedBy(feedbackHistoryVM.getReviewedBy());
				}

				task = feedbackHistoryRepository.save(task);
				wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
						task.getFeedbackHistoryId(), "", methodName);
				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return task;
			}
		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " EXCEPTION [ " + customException.getMessage() + " ]");
			throw customException;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " EXCEPTION [ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.TASK_CLOSE_EXCEPTION.getException(),
					CustomExceptionCode.TASK_CLOSE_EXCEPTION.getErrMsg(),
					CustomExceptionCode.TASK_CLOSE_EXCEPTION.getErrCode());
		}
	}

	@Override
	public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
		String methodName = "getParamterList";
		String TableName = "FeedbackHistory";
		AdvanceSearchList dailyTaskTrackerList = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETEFLAG).is(false).and("tableName").is(TableName));
			dailyTaskTrackerList = mongoOperation.findOne(query, AdvanceSearchList.class);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return dailyTaskTrackerList;
		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
					+ customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(
					RegisteredException.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
					CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
					CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
		String methodName = "advanceSearch";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		List<FeedbackHistory> list = null;
		PaginationDataMV paginationDataMV = null;
		Query query = new Query();
		long noOfDocs;
		try {
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

			if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
				Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(searchVM.getStartDate());
				Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(searchVM.getEndDate());
				query.addCriteria(new Criteria().andOperator(Criteria.where("actual_start_date").lte(endDate),
						Criteria.where("actual_close_date").gte(startDate)));
			}
			if (StringUtil.haveValue(searchVM.getStatus())) {
				query.addCriteria(Criteria.where("status").is(searchVM.getStatus()));
			}
			if (StringUtil.haveValue(searchVM.getParameter()) && StringUtil.haveValue(searchVM.getParametervalue())) {
				String actualField = searchVM.getParameter();
				if (Constants.DAILY_TASK_TRACKER_DATE.contains(searchVM.getParameter())) {
					Date startDate1 = StringUtil.getDayStartTimeFromSimpleDateString(searchVM.getParametervalue());
					Date endDate1 = StringUtil.getDayEndTimeFromSimpleDateString(searchVM.getParametervalue());
					query.addCriteria(Criteria.where(actualField).gte(startDate1).lt(endDate1));
				} else {
					query.addCriteria(Criteria.where(actualField).regex(searchVM.getParametervalue(), "i"));
				}
			}

			if (StringUtil.haveValue(searchVM.getSortName())) {
				if ("ASC".equalsIgnoreCase(searchVM.getSortType())) {
					query.with(new Sort(Sort.Direction.ASC, searchVM.getSortName()));
				} else {
					query.with(new Sort(Sort.Direction.DESC, searchVM.getSortName()));
				}
			} else {
				query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
			}

			if (searchVM.isEnablePagination()) {
				query.with(pageable);
				list = mongoOperation.find(query, FeedbackHistory.class);
				noOfDocs = mongoOperation.count(query, FeedbackHistory.class);

				paginationDataMV = new PaginationDataMV();
				paginationDataMV.setContent(list);
				paginationDataMV.setNumber(pageable.getPageNumber());
				paginationDataMV.setNumberOfElements(list.size());
				paginationDataMV.setSize(pageable.getPageSize());
				paginationDataMV.setTotalElements(noOfDocs);
				paginationDataMV.setTotalPages((int) Math.ceil((double) noOfDocs / pageable.getPageSize()));
			} else {
				list = mongoOperation.find(query, FeedbackHistory.class);
				paginationDataMV = new PaginationDataMV();
				paginationDataMV.setContent(list);
				paginationDataMV.setNumber(0);
				paginationDataMV.setNumberOfElements(list.size());
				paginationDataMV.setSize(list.size());
				paginationDataMV.setTotalElements(list.size());
				paginationDataMV.setTotalPages(1);
			}
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return paginationDataMV;

		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
					+ customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.ADVACNE_SEARCH_EXCEPTION.getException(),
					CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrMsg(),
					CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object subTaskAdvanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
		String methodName = "subTaskAdvanceSearch";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {
			Object result = subtaskService.advanceSearchSubtasks(searchVM, pageable);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return result;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.ADVACNE_SEARCH_EXCEPTION.getException(),
					CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrMsg(),
					CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object addSubTask(FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "addSubTask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {
			Object result = subtaskService.createSubtask(feedbackHistoryVM.getFeedbackHistoryId(), feedbackHistoryVM,
					customUserDetails);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return result;
		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
					+ customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.SUBTASK_SAVING_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURRED_WHILE_SAVING_SUBTASK.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURRED_WHILE_SAVING_SUBTASK.getErrCode());
		}
	}

	@Override
	public Object updateSubTask(EpicVM epicVM, String feedbackHistoryId, String epicId,
			CustomUserDetails customUserDetails) {
		String methodName = "updateSubTask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {
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
            vm.setProduct(epicVM.getProduct());

			Subtask updatedSubtask = subtaskService.updateSubtask(epicId, vm, customUserDetails);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return updatedSubtask;
		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " Exception log[ " + customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " Exception log[ " + exception.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Object deleteSubTask(String feedbackHistoryId, String epicId, CustomUserDetails customUserDetails) {
		String methodName = "deleteSubTask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {
			Subtask deletedSubtask = subtaskService.deleteSubtask(epicId, customUserDetails);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return deletedSubtask;
		} catch (CustomParameterizedException customException) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
					+ customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Object getUploadedFiles(SearchVM dateRangeVM, Pageable pageable) {
		String methodName = "getUploadedFiles";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		List<Map<String, Object>> flattenedResult = new ArrayList<>();

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

			if (StringUtil.haveValue(dateRangeVM.getStartDate()) && StringUtil.haveValue(dateRangeVM.getEndDate())) {
				Date startDate = StringUtil.convertStringIntoDatePattern(dateRangeVM.getStartDate());
				Date endDate = StringUtil.convertStringIntoDatePattern(dateRangeVM.getEndDate());
				query.addCriteria(Criteria.where("created_on").gte(startDate).lte(endDate));
			}
			if (StringUtil.haveValue(dateRangeVM.getTeam())) {
				query.addCriteria(Criteria.where("team").is(dateRangeVM.getTeam()));
			}
			if (StringUtil.haveValue(dateRangeVM.getStatus())) {
				query.addCriteria(Criteria.where("status").is(dateRangeVM.getStatus()));
			}
			if (StringUtil.haveValue(dateRangeVM.getProject())) {
				query.addCriteria(Criteria.where("project").is(dateRangeVM.getProject()));
			}
			if (StringUtil.haveValue(dateRangeVM.getModule())) {
				query.addCriteria(Criteria.where("module").is(dateRangeVM.getModule()));
			}
			if (StringUtil.haveValue(dateRangeVM.getTaskOwner())) {
				query.addCriteria(Criteria.where("task_owner").is(dateRangeVM.getTaskOwner()));
			}
			if (StringUtil.haveValue(dateRangeVM.getTicketNumber())) {
				query.addCriteria(Criteria.where("ticket_number").is(dateRangeVM.getTicketNumber()));
			}

			query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
			List<FeedbackHistory> feedbacks = mongoTemplate.find(query, FeedbackHistory.class);

			for (FeedbackHistory feedback : feedbacks) {
				if (feedback.getAttachments() != null && !feedback.getAttachments().isEmpty()) {
					for (String fileName : feedback.getAttachments()) {
						Map<String, Object> record = new HashMap<>();
						record.put("ticketNumber", feedback.getTicketNumber());
						record.put("subject", feedback.getSubject());
						record.put("description", feedback.getDescription());
						record.put("status", feedback.getStatus());
						record.put("project", feedback.getProject());
						record.put("module", feedback.getModule());
						record.put("team", feedback.getTeam());
						record.put("createdOn", feedback.getCreatedOn());
						record.put("taskOwner", feedback.getTaskOwner());
						record.put("priority", feedback.getPriority());
						record.put("customer", feedback.getCustomer());
						record.put("fileName", Paths.get(fileName).getFileName().toString());
						record.put("filePath", fileName.replace("\\", "/"));
						record.put("source", "mainTask");
						flattenedResult.add(record);
					}
				}
			}

			List<Subtask> subtasks = subtaskService.getAllSubtasks();
			for (Subtask subtask : subtasks) {
				if (subtask.getStoredAttachmentNames() != null && !subtask.getStoredAttachmentNames().isEmpty()) {
					for (String fileName : subtask.getStoredAttachmentNames()) {
						Map<String, Object> record = new HashMap<>();
						record.put("ticketNumber", subtask.getTicketNumber());
						record.put("subject", subtask.getSubject());
						record.put("description", subtask.getDescription());
						record.put("status", subtask.getStatus());
						record.put("project", subtask.getProject());
						record.put("module", subtask.getModule());
						record.put("team", subtask.getTeam());
						record.put("createdOn", subtask.getCreatedOn());
						record.put("taskOwner", subtask.getTaskOwner());
						record.put("priority", subtask.getPriority());
						record.put("customer", subtask.getCustomer());
						record.put("fileName", Paths.get(fileName).getFileName().toString());
						record.put("filePath", fileName.replace("\\", "/"));
						record.put("source", "subtask");
						flattenedResult.add(record);
					}
				}
			}

			int pageSize = pageable.getPageSize();
			int pageNumber = pageable.getPageNumber();
			int total = flattenedResult.size();
			int fromIndex = Math.min(pageNumber * pageSize, total);
			int toIndex = Math.min(fromIndex + pageSize, total);
			List<Map<String, Object>> paginated = flattenedResult.subList(fromIndex, toIndex);

			Map<String, Object> response = new HashMap<>();
			response.put("data", paginated);
			response.put("totalRecords", total);
			response.put("pageSize", pageSize);
			response.put("currentPage", pageNumber);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return response;

		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
		}
	}

	@Override
	public Object getAllTicketNumbers(CustomUserDetails customUserDetails) {
		String methodName = "getAllTicketNumbers";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		List<String> ticketNumbers = new ArrayList<>();

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			query.fields().include("ticket_number");

			List<FeedbackHistory> feedbackList = mongoTemplate.find(query, FeedbackHistory.class);
			for (FeedbackHistory feedback : feedbackList) {
				if (feedback.getTicketNumber() != null && !feedback.getTicketNumber().isEmpty()) {
					ticketNumbers.add(feedback.getTicketNumber());
				}
			}

			List<Subtask> subtasks = subtaskService.getAllSubtasks();
			for (Subtask subtask : subtasks) {
				if (subtask.getTicketNumber() != null && !subtask.getTicketNumber().isEmpty()) {
					ticketNumbers.add(subtask.getTicketNumber());
				}
			}

			ticketNumbers = ticketNumbers.stream().distinct().sorted().collect(Collectors.toList());
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return ticketNumbers;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " Exception: " + e.getMessage(), e);
			throw new CustomParameterizedException(RegisteredException.ADVACNE_SEARCH_EXCEPTION.getException(),
					CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrMsg(),
					CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object getSearchableSubtaskFields(CustomUserDetails customUserDetails) {
		Set<String> excludedFields = new HashSet<>(Arrays.asList("id", "subtaskId", "storedAttachmentNames",
				"estimatedCloseDate", "actualCloseDate", "dateAssigned", "actualStartDate", "estimatedStartDate",
				"workLogEntries", "deleteFlag", "createdBy", "createdOn", "modifiedBy", "modifiedDateTime", "deletedBy",
				"deletedOn", "version", "parentTicketNumber", "parentFeedbackHistoryId", "activeFlag", "statusFlag",
				"historyFlag", "reportGeneratedDate"));

		Field[] allFields = Subtask.class.getDeclaredFields();
		List<Map<String, String>> filteredFields = new ArrayList<>();

		for (Field field : allFields) {
			if (!excludedFields.contains(field.getName())) {
				Map<String, String> map = new HashMap<>();
				map.put("value", field.getName());
				map.put("label", convertToLabel(field.getName()));
				filteredFields.add(map);
			}
		}
		return filteredFields;
	}

	private String convertToLabel(String field) {
		return Arrays.stream(field.split("(?=[A-Z])")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
				.collect(Collectors.joining(" "));
	}

	@Override
	public Object addWorklogHours(String feedbackHistoryId, String epicId, List<WorkLogEntry> workLogEntries,
			CustomUserDetails customUserDetails) {
		String methodName = "addWorklogHours";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			if (epicId != null && !epicId.isEmpty()) {
				Subtask updatedSubtask = subtaskService.addWorkLog(epicId, workLogEntries, customUserDetails);
				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return updatedSubtask;
			} else {
				if (feedbackHistoryId == null || feedbackHistoryId.isEmpty()) {
					throw new CustomParameterizedException(RegisteredException.FEED_BACK_ID_REQUIRED.getException(),
							CustomExceptionCode.FEEDBACK_ID_REQUIRED.getErrMsg(),
							CustomExceptionCode.FEEDBACK_ID_REQUIRED.getErrCode());
				}

				FeedbackHistory feedbackHistory = feedbackHistoryRepository
						.findByFeedbackHistoryIdAndDeleteFlag(feedbackHistoryId, false);
				if (feedbackHistory == null) {
					throw new CustomParameterizedException(
							RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
							CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
							CustomExceptionCode.DATA_NOT_FOUND.getErrCode());
				}

				Date allowedStartDate = feedbackHistory.getActualStartDate();
				Date allowedEndDate = feedbackHistory.getActualCloseDate();

				for (WorkLogEntry entry : workLogEntries) {
					if (entry.getDate().compareTo(allowedStartDate) < 0
							|| entry.getDate().compareTo(allowedEndDate) > 0) {
						throw new CustomParameterizedException(RegisteredException.INVALID_DATE.getException(),
								"Work log date must be between " + allowedStartDate + " and " + allowedEndDate,
								CustomExceptionCode.DATE_RANGE_EXCEPTION.getErrCode());
					}
					if (entry.getHours() <= 0 || entry.getHours() > 24) {
						throw new CustomParameterizedException(RegisteredException.INVALID_DATE.getException(),
								"Work hours must be between 0 and 24",
								CustomExceptionCode.DATE_RANGE_EXCEPTION.getErrCode());
					}
				}

				List<WorkLogEntry> feedbackWorkLogs = feedbackHistory.getWorkLogEntries();
				if (feedbackWorkLogs == null) {
					feedbackWorkLogs = new ArrayList<>();
				}
				feedbackWorkLogs.addAll(workLogEntries);
				feedbackHistory.setWorkLogEntries(feedbackWorkLogs);

//				double totalEffort = feedbackWorkLogs.stream().mapToDouble(WorkLogEntry::getHours).sum();
//				feedbackHistory.setActualEffort(totalEffort);
				feedbackHistoryRepository.save(feedbackHistory);

				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return feedbackHistory;
			}
		} catch (CustomParameterizedException e) {
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " Exception: " + e.getMessage(), e);
			throw new CustomParameterizedException(RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode());
		}
	}

	@Override
	public Object uploadFileByTicketNumber(String ticketNumber, MultipartFile file, CustomUserDetails customUserDetails) {
	    String methodName = "uploadFileByTicketNumber";
	    log.info(this.getClass() + " " + methodName + " " + Constants.START);

	    try {
	        // Validate ticket number
	        if (ticketNumber == null || ticketNumber.isEmpty()) {
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("exception", "FileUploadException");
	            errorResponse.put("exceptionMessage", "Ticket number is required");
	            errorResponse.put("errorCodeList", Arrays.asList("WS_ERR12405"));
	            errorResponse.put("success", false);
	            return errorResponse;
	        }

	        // Validate file
	        if (file == null || file.isEmpty()) {
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("exception", "FileUploadException");
	            errorResponse.put("exceptionMessage", "File is empty");
	            errorResponse.put("errorCodeList", Arrays.asList("WS_ERR12405"));
	            errorResponse.put("success", false);
	            return errorResponse;
	        }

	        // Validate file extension
	        List<String> allowedExtensions = Arrays.asList(".png", ".jpg", ".jpeg", ".pdf", ".doc", ".docx", ".xlsx", ".xls", ".txt", ".csv");
	        String originalFileName = file.getOriginalFilename();
	        String fileExtension = "";

	        if (originalFileName != null && originalFileName.lastIndexOf(".") > 0) {
	            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
	        }

	        if (!allowedExtensions.contains(fileExtension)) {
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("exception", "FileUploadException");
	            errorResponse.put("exceptionMessage", "Please Upload .png/.jpg/.pdf/.doc/.xlsx extension");
	            errorResponse.put("errorCodeList", Arrays.asList("WS_ERR12405"));
	            errorResponse.put("success", false);
	            return errorResponse;
	        }

	        // Check if it's a subtask or parent task
	        boolean isSubTask = ticketNumber.matches("^[A-Z]+-\\d+-\\d+$");

	        // Create upload directory
	        String uploadPath = UPLOAD_FOLDER + File.separator + HISTORY_UPLOADS + File.separator + ticketNumber;
	        File directory = new File(uploadPath);
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }

	        // Generate unique file name
	        String timestamp = String.valueOf(System.currentTimeMillis());
	        String uniqueFileName = timestamp + "_" + originalFileName;
	        String filePath = uploadPath + File.separator + uniqueFileName;
	        String relativePath = HISTORY_UPLOADS + File.separator + ticketNumber + File.separator + uniqueFileName;

	        // Save file to disk
	        File destFile = new File(filePath);
	        file.transferTo(destFile);

	        // Save file reference to database
	        if (isSubTask) {
	            // Handle subtask
	            Subtask subtask = subtaskService.getSubtaskByTicketNumber(ticketNumber);
	            if (subtask == null) {
	                Map<String, Object> errorResponse = new HashMap<>();
	                errorResponse.put("exception", "FileUploadException");
	                errorResponse.put("exceptionMessage", "Subtask not found for ticket: " + ticketNumber);
	                errorResponse.put("errorCodeList", Arrays.asList("WS_ERR12405"));
	                errorResponse.put("success", false);
	                return errorResponse;
	            }

	            List<String> attachments = subtask.getStoredAttachmentNames();
	            if (attachments == null) {
	                attachments = new ArrayList<>();
	            }
	            attachments.add(relativePath);
	            subtask.setStoredAttachmentNames(attachments);
	            subtaskRepository.save(subtask);

	        } else {
	            // Handle parent task
	            FeedbackHistory feedbackHistory = feedbackHistoryRepository.findByTicketNumberAndDeleteFlag(ticketNumber, false);
	            if (feedbackHistory == null) {
	                Map<String, Object> errorResponse = new HashMap<>();
	                errorResponse.put("exception", "FileUploadException");
	                errorResponse.put("exceptionMessage", "Task not found for ticket: " + ticketNumber);
	                errorResponse.put("errorCodeList", Arrays.asList("WS_ERR12405"));
	                errorResponse.put("success", false);
	                return errorResponse;
	            }

	            List<String> attachments = feedbackHistory.getAttachments();
	            if (attachments == null) {
	                attachments = new ArrayList<>();
	            }
	            attachments.add(relativePath);
	            feedbackHistory.setAttachments(attachments);
	            feedbackHistoryRepository.save(feedbackHistory);
	        }

	        // Save to completion files repository
	        DCompletionFileInfo completionFile = new DCompletionFileInfo();
	        completionFile.setFileName(relativePath);
	        completionFile.setWellId(ticketNumber);
	        completionFilesRepository.save(completionFile);

	        // Prepare response
	        Map<String, Object> response = new HashMap<>();
	        response.put("success", true);
	        response.put("ticketNumber", ticketNumber);
	        response.put("fileName", originalFileName);
	        response.put("filePath", relativePath);
	        response.put("message", "File uploaded successfully");

	        log.info(this.getClass() + " " + methodName + " " + Constants.END);
	        return response;

	    } catch (Exception e) {
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("exception", "FileUploadException");
	        errorResponse.put("exceptionMessage", "Error uploading file: " + e.getMessage());
	        errorResponse.put("errorCodeList", Arrays.asList("WS_ERR12405"));
	        errorResponse.put("success", false);
	        return errorResponse;
	    }
	}

	@Override
	public Object dashboard(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
		String methodName = "dashboard";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		Map<String, Object> response = new HashMap<>();

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

			if (StringUtil.haveValue(effortRecordVM.getProject())) {
				query.addCriteria(Criteria.where("project").is(effortRecordVM.getProject()));
			}
			if (StringUtil.haveValue(effortRecordVM.getStartDate())
					&& StringUtil.haveValue(effortRecordVM.getEndDate())) {
				Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(effortRecordVM.getStartDate());
				Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(effortRecordVM.getEndDate());
				query.addCriteria(Criteria.where(Constants.MODIFIED_DATE_TIME).gte(startDate).lte(endDate));
			}
			if (StringUtil.haveValue(effortRecordVM.getActualStartDate())
					&& StringUtil.haveValue(effortRecordVM.getActualEndDate())) {
				Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(effortRecordVM.getActualStartDate());
				Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(effortRecordVM.getActualEndDate());
				query.addCriteria(
						new Criteria().orOperator(Criteria.where("actual_start_date").gte(startDate).lte(endDate),
								Criteria.where("actual_close_date").gte(startDate).lte(endDate)));
			}
			if (StringUtil.haveValue(effortRecordVM.getEstimatedStartDate())
					&& StringUtil.haveValue(effortRecordVM.getEstimatedEndDate())) {
				Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(effortRecordVM.getEstimatedStartDate());
				Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(effortRecordVM.getEstimatedEndDate());
				query.addCriteria(
						new Criteria().orOperator(Criteria.where("estimated_start_date").gte(startDate).lte(endDate),
								Criteria.where("estimated_close_date").gte(startDate).lte(endDate)));
			}

			query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
			List<FeedbackHistory> feedbackList = mongoTemplate.find(query, FeedbackHistory.class);

			Query meetingQuery = new Query();
			meetingQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			List<MeetingTimeTracker> meetingList = mongoTemplate.find(meetingQuery, MeetingTimeTracker.class);

			List<Subtask> subtaskList = subtaskService.getAllSubtasks();

			Predicate<String> validString = s -> s != null && !s.trim().equalsIgnoreCase("unknown")
					&& !s.trim().isEmpty();

			List<FeedbackHistory> combinedList = new ArrayList<>();
			for (FeedbackHistory parent : feedbackList) {
				combinedList.add(parent);
			}

			for (Subtask subtask : subtaskList) {
				FeedbackHistory subTaskItem = new FeedbackHistory();
				subTaskItem.setEpic(subtask.getEpic());
				subTaskItem.setStatus(subtask.getStatus());
				subTaskItem.setTaskType(subtask.getTaskType());
				subTaskItem.setPriority(subtask.getPriority());
				subTaskItem.setTaskOwner(subtask.getTaskOwner());
				subTaskItem.setProject(subtask.getProject());
				subTaskItem.setTeam(subtask.getTeam());
				combinedList.add(subTaskItem);
			}

			Map<String, Long> statusOverview = combinedList.stream().filter(f -> validString.test(f.getStatus()))
					.collect(Collectors.groupingBy(FeedbackHistory::getStatus, TreeMap::new, Collectors.counting()));

			Map<String, Long> rawPriorityBreakdown = combinedList.stream()
					.filter(f -> validString.test(f.getPriority()))
					.collect(Collectors.groupingBy(FeedbackHistory::getPriority, Collectors.counting()));

			List<Map.Entry<String, Long>> sortedPriorityList = new ArrayList<>(rawPriorityBreakdown.entrySet());
			sortedPriorityList.sort(Comparator.comparingInt(e -> {
				String key = e.getKey();
				String pNumStr = key.replaceAll(".*P(\\d).*", "$1");
				try {
					return Integer.parseInt(pNumStr);
				} catch (NumberFormatException ex) {
					return Integer.MAX_VALUE;
				}
			}));

			Map<String, Long> priorityBreakdown = new LinkedHashMap<>();
			for (Map.Entry<String, Long> entry : sortedPriorityList) {
				priorityBreakdown.put(entry.getKey(), entry.getValue());
			}

			Map<String, Long> typeBreakdown = combinedList.stream().filter(f -> validString.test(f.getTaskType()))
					.collect(Collectors.groupingBy(FeedbackHistory::getTaskType, TreeMap::new, Collectors.counting()));

			Map<String, Long> teamWorkload = combinedList.stream().filter(f -> validString.test(f.getTaskOwner()))
					.collect(Collectors.groupingBy(FeedbackHistory::getTaskOwner, TreeMap::new, Collectors.counting()));

			Map<String, Map<String, Long>> epicProgress = combinedList.stream()
					.filter(f -> validString.test(f.getEpic()))
					.collect(Collectors.groupingBy(FeedbackHistory::getEpic, TreeMap::new,
							Collectors.groupingBy(f -> validString.test(f.getStatus()) ? f.getStatus() : "Epic",
									TreeMap::new, Collectors.counting())));

			long totalWorkItems = combinedList.size();

			List<String> allKnownOwners = dropdownManagementRepository
					.findByDomainNameAndDeleteFlag("Task Owner", false).stream().map(DropdownManagement::getValue)
					.filter(validString).map(String::trim).collect(Collectors.toList());

			Set<String> dbTaskOwners = feedbackList.stream().map(FeedbackHistory::getTaskOwner).filter(validString)
					.map(String::trim).collect(Collectors.toSet());

			Set<String> subtaskTaskOwners = subtaskList.stream().map(Subtask::getTaskOwner).filter(validString)
					.map(String::trim).collect(Collectors.toSet());

			Set<String> meetingTaskOwners = meetingList.stream().map(MeetingTimeTracker::getMeetingBy)
					.filter(validString).map(String::trim).collect(Collectors.toSet());

			Set<String> allDbOwners = new HashSet<>();
			allDbOwners.addAll(dbTaskOwners);
			allDbOwners.addAll(subtaskTaskOwners);
			allDbOwners.addAll(meetingTaskOwners);

			Set<String> missingOwnersSet = allKnownOwners.stream().filter(owner -> !allDbOwners.contains(owner))
					.collect(Collectors.toSet());

			List<String> missingOwners = new ArrayList<>(missingOwnersSet);
			Collections.sort(missingOwners, String.CASE_INSENSITIVE_ORDER);

			response.put("statusOverview", statusOverview);
			response.put("priorityBreakdown", priorityBreakdown);
			response.put("typeBreakdown", typeBreakdown);
			response.put("teamWorkload", teamWorkload);
			response.put("epicProgress", epicProgress);
			response.put("totalWorkItems", totalWorkItems);
			response.put("inactiveTaskOwners", missingOwners);
			response.put("inactiveTaskOwnerCount", missingOwners.size());

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return response;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " Exception: " + e.getMessage(), e);
			throw new CustomParameterizedException(
					RegisteredException.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
					CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
					CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
		}
	}

	@Override
	public Object getDailyTaskTracker(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
		String methodName = "getDailyTaskTracker";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		Map<String, Object> response = new HashMap<>();

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

			Date filterStartDate = null;
			Date filterEndDate = null;

			if (StringUtil.haveValue(effortRecordVM.getActualStartDate())
					&& StringUtil.haveValue(effortRecordVM.getActualEndDate())) {
				filterStartDate = StringUtil.getDayStartTimeFromSimpleDateString(effortRecordVM.getActualStartDate());
				filterEndDate = StringUtil.getDayEndTimeFromSimpleDateString(effortRecordVM.getActualEndDate());
				query.addCriteria(new Criteria().orOperator(
						Criteria.where("actual_start_date").gte(filterStartDate).lte(filterEndDate),
						Criteria.where("actual_close_date").gte(filterStartDate).lte(filterEndDate)));
			} else if (StringUtil.haveValue(effortRecordVM.getEstimatedStartDate())
					&& StringUtil.haveValue(effortRecordVM.getEstimatedEndDate())) {
				filterStartDate = StringUtil
						.getDayStartTimeFromSimpleDateString(effortRecordVM.getEstimatedStartDate());
				filterEndDate = StringUtil.getDayEndTimeFromSimpleDateString(effortRecordVM.getEstimatedEndDate());
				query.addCriteria(new Criteria().orOperator(
						Criteria.where("estimated_start_date").gte(filterStartDate).lte(filterEndDate),
						Criteria.where("estimated_close_date").gte(filterStartDate).lte(filterEndDate)));
			} else if (StringUtil.haveValue(effortRecordVM.getStartDate())
					&& StringUtil.haveValue(effortRecordVM.getEndDate())) {
				filterStartDate = StringUtil.getDayStartTimeFromSimpleDateString(effortRecordVM.getStartDate());
				filterEndDate = StringUtil.getDayEndTimeFromSimpleDateString(effortRecordVM.getEndDate());
				query.addCriteria(Criteria.where(Constants.MODIFIED_DATE_TIME).gte(filterStartDate).lte(filterEndDate));
			}

			if (StringUtil.haveValue(effortRecordVM.getProject())) {
				query.addCriteria(Criteria.where("project").is(effortRecordVM.getProject()));
			}
			if (StringUtil.haveValue(effortRecordVM.getTaskOwner())) {
				query.addCriteria(Criteria.where("task_owner").is(effortRecordVM.getTaskOwner()));
			}
			if (StringUtil.haveValue(effortRecordVM.getTeam())) {
				query.addCriteria(Criteria.where("team").is(effortRecordVM.getTeam()));
			}

			query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
			List<FeedbackHistory> feedbackList = mongoTemplate.find(query, FeedbackHistory.class);
			List<Map<String, Object>> allLogs = new ArrayList<>();

			for (FeedbackHistory f : feedbackList) {
				if (f.getActualEffort() != null && f.getActualEffort() > 0 && f.getActualStartDate() != null
						&& !f.getActualStartDate().before(filterStartDate)
						&& !f.getActualStartDate().after(filterEndDate)) {
					allLogs.add(buildLogMap(f.getActualEffort(), f.getTaskOwner(), f.getProject(), f.getTeam(), "task",
							f.getActualStartDate()));
				}

				if (f.getWorkLogEntries() != null) {
					for (WorkLogEntry entry : f.getWorkLogEntries()) {
						if (entry != null && entry.getHours() != null && entry.getHours() > 0 && entry.getDate() != null
								&& !entry.getDate().before(filterStartDate) && !entry.getDate().after(filterEndDate)) {
							allLogs.add(buildLogMap(entry.getHours(), f.getTaskOwner(), f.getProject(), f.getTeam(),
									"task", entry.getDate()));
						}
					}
				}
			}

			List<Subtask> subtaskList = subtaskService.getAllSubtasks();
			for (Subtask subtask : subtaskList) {
				if (subtask.getActualEffort() != null && subtask.getActualEffort() > 0
						&& subtask.getActualStartDate() != null && !subtask.getActualStartDate().before(filterStartDate)
						&& !subtask.getActualStartDate().after(filterEndDate)) {
					allLogs.add(buildLogMap(subtask.getActualEffort(), subtask.getTaskOwner(), subtask.getProject(),
							subtask.getTeam(), "task", subtask.getActualStartDate()));
				}

				if (subtask.getWorkLogEntries() != null) {
					for (WorkLogEntry entry : subtask.getWorkLogEntries()) {
						if (entry != null && entry.getHours() != null && entry.getHours() > 0 && entry.getDate() != null
								&& !entry.getDate().before(filterStartDate) && !entry.getDate().after(filterEndDate)) {
							allLogs.add(buildLogMap(entry.getHours(), subtask.getTaskOwner(), subtask.getProject(),
									subtask.getTeam(), "task", entry.getDate()));
						}
					}
				}
			}

			Query meetingQuery = new Query();
			meetingQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			if (StringUtil.haveValue(effortRecordVM.getProject())) {
				meetingQuery.addCriteria(Criteria.where("project").is(effortRecordVM.getProject()));
			}
			if (StringUtil.haveValue(effortRecordVM.getTaskOwner())) {
				meetingQuery.addCriteria(Criteria.where("meeting_by").is(effortRecordVM.getTaskOwner()));
			}
			if (StringUtil.haveValue(effortRecordVM.getTeam())) {
				meetingQuery.addCriteria(Criteria.where("team").is(effortRecordVM.getTeam()));
			}
			if (filterStartDate != null && filterEndDate != null) {
				meetingQuery.addCriteria(Criteria.where("date").gte(filterStartDate).lte(filterEndDate));
			}

			List<MeetingTimeTracker> meetingList = mongoTemplate.find(meetingQuery, MeetingTimeTracker.class);
			for (MeetingTimeTracker m : meetingList) {
				if (m.getTotalTime() != null && m.getTotalTime() > 0 && m.getDate() != null) {
					allLogs.add(buildLogMap(m.getTotalTime(), m.getMeetingBy(), m.getProject(), m.getTeam(), "meeting",
							m.getDate()));
				}
			}

			Map<String, List<Map<String, Object>>> grouped = allLogs.stream().collect(Collectors.groupingBy(m -> {
				return (m.get("taskOwner") + "|" + m.get("project") + "|" + m.get("team") + "|" + m.get("date"));
			}));

			List<Map<String, Object>> groupedList = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
				String[] parts = entry.getKey().split("\\|", -1);
				if ("Unknown".equalsIgnoreCase(parts[0]))
					continue;

				List<Map<String, Object>> records = entry.getValue();
				double taskEffort = records.stream().filter(m -> "task".equals(m.get("type")))
						.mapToDouble(m -> ((Number) m.get("hours")).doubleValue()).sum();

				double meetingTime = records.stream().filter(m -> "meeting".equals(m.get("type")))
						.mapToDouble(m -> ((Number) m.get("hours")).doubleValue()).sum();

				Map<String, Object> map = new HashMap<>();
				map.put("taskOwner", parts[0]);
				map.put("project", parts[1]);
				map.put("team", parts[2]);
				map.put("date", parts[3]);
				map.put("totalEffort", taskEffort + meetingTime);
				map.put("totalTaskEffort", taskEffort);
				map.put("totalMeetingTime", meetingTime);

				groupedList.add(map);
			}

			groupedList.sort(Comparator.comparing(m -> m.get("taskOwner").toString(), String.CASE_INSENSITIVE_ORDER));

			double grandTotalEffort = groupedList.stream()
					.mapToDouble(m -> ((Number) m.get("totalEffort")).doubleValue()).sum();

			Map<String, Object> data = new HashMap<>();
			data.put("totalActualEffort", grandTotalEffort);
			data.put("list", groupedList);
			response.put("data", data);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return response;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " Exception: " + e.getMessage(), e);
			throw new CustomParameterizedException(
					RegisteredException.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
					CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
					CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
		}
	}

	private Map<String, Object> buildLogMap(Number hours, String taskOwner, String project, String team, String type,
			Date date) {
		Map<String, Object> map = new HashMap<>();
		map.put("hours", hours);
		map.put("taskOwner", taskOwner);
		map.put("project", project);
		map.put("team", team);
		map.put("type", type);
		map.put("date", toIsoUTC(date));
		return map;
	}

	private static String toIsoUTC(Date date) {
		return DateTimeFormatter.ISO_INSTANT.format(date.toInstant());
	}


    @Override
    public void checkAndSendWeeklyEffortMails() {
        try {
            LocalDate today = LocalDate.now(IST);

            if (today.getDayOfWeek() != DayOfWeek.FRIDAY) {
                return;
            }

            LocalDate weekStart = today.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = today.minusDays(1);

            List<LocalDate> weekDays = new ArrayList<>();
            for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
                weekDays.add(d);
            }


            Set<String> allOwners = dropdownManagementRepository
                .findByDomainNameAndDeleteFlag("Task Owner", false)
                .stream()
                .map(DropdownManagement::getValue)
                .filter(o -> o != null && !o.trim().isEmpty() && !o.equalsIgnoreCase("unknown"))
                .map(String::trim)
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

            if (allOwners.isEmpty()) {
                return;
            }

            Map<String, Set<LocalDate>> leaveDatesByOwner = getLeaveDatesForOwners(allOwners, weekStart.toString(), weekEnd.toString());
            Map<String, Map<LocalDate, Double>> effortByOwnerByDate = new HashMap<>();

            for (LocalDate day : weekDays) {
                EffortRecordVM vm = new EffortRecordVM();
                vm.setActualStartDate(day.toString());
                vm.setActualEndDate(day.toString());

                Object result = getDailyTaskTrackers(vm, null);
                Map<String, Double> dayEffortByOwner = extractEffortByOwner(result, day);

                for (String owner : allOwners) {
                    double effort = dayEffortByOwner.getOrDefault(owner, 0.0);
                    effortByOwnerByDate
                        .computeIfAbsent(owner, k -> new HashMap<>())
                        .put(day, effort);
                }
            }

            Query query = new Query();
            query.addCriteria(Criteria.where("operation").is("WeeklyStatus").and(Constants.DELETE_FLAG).is(false));
            EmailRecipients emailRecipients = mongoOperation.findOne(query, EmailRecipients.class);
            List<String> ccList = (emailRecipients != null && emailRecipients.getRecipients() != null)
                ? emailRecipients.getRecipients()
                : Collections.emptyList();

            List<TaskOwnerEmail> ownerEmails = taskOwnerEmailRepository.findByTaskOwnerInAndDeleteFlag(allOwners, Boolean.FALSE);
            Map<String, TaskOwnerEmail> emailMap = ownerEmails.stream()
                .filter(e -> e.getEmail() != null && !e.getEmail().trim().isEmpty())
                .collect(Collectors.toMap(TaskOwnerEmail::getTaskOwner, Function.identity(), (a, b) -> a));

            int sentCount = 0;
            int missingEmailCount = 0;

            for (String taskOwner : allOwners) {
                Map<LocalDate, Double> dailyEffort = effortByOwnerByDate.get(taskOwner);

                List<LocalDate> shortfallDays = new ArrayList<>();
                for (LocalDate day : weekDays) {
                    double effort = dailyEffort.getOrDefault(day, 0.0);
                    if (effort < MIN_DAILY_EFFORT_HOURS) {
                        shortfallDays.add(day);
                    }
                }

                if (shortfallDays.isEmpty()) {
                    continue;
                }

                Set<LocalDate> leaveDates = leaveDatesByOwner.getOrDefault(taskOwner, Collections.emptySet());
                boolean isOnLeaveOnShortfallDay = shortfallDays.stream().anyMatch(leaveDates::contains);

                if (isOnLeaveOnShortfallDay) {
                    continue;
                }

                TaskOwnerEmail ownerEmail = emailMap.get(taskOwner);
                if (ownerEmail == null) {
                    missingEmailCount++;
                    continue;
                }

                String emailContent;
                try {
                    emailContent = buildWeeklyEffortEmailContent(taskOwner, weekStart, weekEnd, shortfallDays, dailyEffort);
                } catch (Exception templateEx) {
                    continue;
                }

                EmailMV emailMV = new EmailMV();
                emailMV.setTo(Collections.singletonList(ownerEmail.getEmail()));
                emailMV.setCc(ccList);
                emailMV.setSubject("Weekly Effort Alert (" + weekStart + " to " + weekEnd + ")");
                emailMV.setHtml(true);
                emailMV.setMultipart(false);
                emailMV.setContent(emailContent);

                try {
                    genericServicesImpl.sendEmailWithAttachmentWithUploadpath(emailMV);
                    sentCount++;
                } catch (Exception mailEx) {
                    log.error("Failed to send weekly effort mail to {} ({})", taskOwner, ownerEmail.getEmail(), mailEx);
                }
            }

        } catch (Exception e) {
            log.error("Exception occurred while sending weekly effort emails: {}", e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> extractEffortByOwner(Object result, LocalDate targetDate) {
        Map<String, Double> effortByOwner = new HashMap<>();

        if (!(result instanceof Map)) {
            return effortByOwner;
        }

        Map<String, Object> resultMap = (Map<String, Object>) result;
        Object dataObj = resultMap.get("data");
        if (!(dataObj instanceof Map)) {
            return effortByOwner;
        }

        Object listObj = ((Map<String, Object>) dataObj).get("list");
        if (!(listObj instanceof List)) {
            return effortByOwner;
        }

        List<Map<String, Object>> records = (List<Map<String, Object>>) listObj;
        for (Map<String, Object> record : records) {
            Object ownerObj = record.get("taskOwner");
            if (!(ownerObj instanceof String) || ((String) ownerObj).trim().isEmpty()) {
                continue;
            }
            String taskOwner = ((String) ownerObj).trim();

            Object effortObj = record.get("totalEffort");
            double effort = (effortObj instanceof Number) ? ((Number) effortObj).doubleValue() : 0.0;

            effortByOwner.merge(taskOwner, effort, Double::sum);
        }
        return effortByOwner;
    }

    private String loadTemplate(String classpathLocation) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathLocation)) {
            if (is == null) {
                throw new IllegalStateException("Template not found on classpath: " + classpathLocation);
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load template: " + classpathLocation, e);
        }
    }

    private String buildWeeklyEffortEmailContent(String taskOwner, LocalDate weekStart, LocalDate weekEnd,
                                                 List<LocalDate> shortfallDays, Map<LocalDate, Double> dailyEffort) {

        String rowTemplate = loadTemplate(WEEKLY_ROW_TEMPLATE_PATH);
        StringBuilder rows = new StringBuilder();

        for (LocalDate day : shortfallDays) {
            double effort = dailyEffort.getOrDefault(day, 0.0);
            boolean isZero = effort == 0.0;

            String statusBadge = isZero
                ? "<span style='display:inline-block;padding:3px 10px;border-radius:12px;background:#fdecea;color:#c0392b;font-size:12px;font-weight:bold;'>NOT LOGGED</span>"
                : "<span style='display:inline-block;padding:3px 10px;border-radius:12px;background:#fff4e5;color:#b9770e;font-size:12px;font-weight:bold;'>SHORTFALL</span>";

            String detail = isZero
                ? "No effort logged"
                : effort + " hrs logged &nbsp;&bull;&nbsp; short by " + (MIN_DAILY_EFFORT_HOURS - effort) + " hrs";

            String dayName = day.getDayOfWeek().toString().charAt(0)
                + day.getDayOfWeek().toString().substring(1).toLowerCase();

            String row = rowTemplate
                .replace("{{DAY_NAME}}", dayName)
                .replace("{{DAY_DATE}}", day.toString())
                .replace("{{DETAIL}}", detail)
                .replace("{{STATUS_BADGE}}", statusBadge);

            rows.append(row);
        }

        String template = loadTemplate(WEEKLY_TEMPLATE_PATH);

        return template
            .replace("{{TASK_OWNER}}", taskOwner)
            .replace("{{WEEK_START}}", weekStart.toString())
            .replace("{{WEEK_END}}", weekEnd.toString())
            .replace("{{MIN_HOURS}}", String.valueOf((int) MIN_DAILY_EFFORT_HOURS))
            .replace("{{SHORTFALL_COUNT}}", String.valueOf(shortfallDays.size()))
            .replace("{{TABLE_ROWS}}", rows.toString());
    }

    @Override
    public void checkAndSendWeeklyInactiveTaskOwnersMail() {
        try {
            LocalDate today = LocalDate.now(IST);

            if (today.getDayOfWeek() != DayOfWeek.MONDAY) {
                return;
            }

            LocalDate weekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
            LocalDate weekEnd = today.minusWeeks(1).with(DayOfWeek.FRIDAY);

            Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(weekStart.toString());
            Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(weekEnd.toString());

            Query feedbackQuery = new Query();
            feedbackQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("actual_start_date")
                .lte(endDate).and("actual_close_date").gte(startDate));

            List<FeedbackHistory> feedbackList = mongoTemplate.find(feedbackQuery, FeedbackHistory.class);

            Query meetingQuery = new Query();
            meetingQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
            List<MeetingTimeTracker> meetingList = mongoTemplate.find(meetingQuery, MeetingTimeTracker.class);

            List<Subtask> subtaskList = subtaskService.getAllSubtasks();

            Predicate<String> validString = s -> s != null && !s.trim().isEmpty() && !s.equalsIgnoreCase("unknown");

            Set<String> allKnownOwners = dropdownManagementRepository.findByDomainNameAndDeleteFlag("Task Owner", false)
                .stream().map(DropdownManagement::getValue).filter(validString).map(String::trim)
                .collect(Collectors.toCollection(TreeSet::new));

            Set<String> activeOwners = new HashSet<>();

            feedbackList.stream().map(FeedbackHistory::getTaskOwner).filter(validString).map(String::trim)
                .forEach(activeOwners::add);

            subtaskList.stream().map(Subtask::getTaskOwner).filter(validString).map(String::trim)
                .forEach(activeOwners::add);

            meetingList.stream().map(MeetingTimeTracker::getMeetingBy).filter(validString).map(String::trim)
                .forEach(activeOwners::add);

            Set<String> inactiveOwners = allKnownOwners.stream()
                .filter(owner -> !activeOwners.contains(owner))
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

            Query emailQuery = new Query();
            emailQuery.addCriteria(Criteria.where("operation").is("leavereport").and(Constants.DELETE_FLAG).is(false));

            EmailRecipients emailRecipients = mongoOperation.findOne(emailQuery, EmailRecipients.class);

            if (emailRecipients == null || emailRecipients.getRecipients() == null || emailRecipients.getRecipients().isEmpty()) {
                return;
            }

            File file = new File(UPLOAD_FOLDER + "/InActiveTaskOwner.xlsx");
            if (file.exists()) {
                file.delete();
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Inactive Owners");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("S.No");
                header.createCell(1).setCellValue("Task Owner");
                header.createCell(2).setCellValue("Week Start");
                header.createCell(3).setCellValue("Week End");

                int rowNum = 1;
                for (String owner : inactiveOwners) {
                    Row row = sheet.createRow(rowNum);
                    row.createCell(0).setCellValue(rowNum);
                    row.createCell(1).setCellValue(owner);
                    row.createCell(2).setCellValue(weekStart.toString());
                    row.createCell(3).setCellValue(weekEnd.toString());
                    rowNum++;
                }

                for (int i = 0; i < 4; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
            }

            String emailContent = "<html><body style='font-family:Arial;color:#555;'>" + "<p>Hello Team,</p>"
                + "<p>Please find attached the list of <b>inactive task owners</b> " + "for the period <b>"
                + weekStart + "</b> to <b>" + weekEnd + "</b>.</p>" + "<p>Total Inactive Task Owners: <b>"
                + inactiveOwners.size() + "</b></p>"
                + "<p style='font-size:12px;color:#999;'>This is an automated email.</p>" + "</body></html>";

            EmailMV emailMV = new EmailMV();
            emailMV.setTo(emailRecipients.getRecipients());
            emailMV.setSubject("Weekly Inactive Task Owners Report (" + weekStart + " to " + weekEnd + ")");
            emailMV.setHtml(true);
            emailMV.setMultipart(true);
            emailMV.setContent(emailContent);
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put(file.getName(), file.getAbsolutePath());
            emailMV.setFileInfo(fileInfo);

            try {
                genericServicesImpl.sendEmailWithAttachmentWithUploadpath(emailMV);
            } catch (Exception mailEx) {
                log.error("Failed to send weekly inactive task owners mail ({} to {})", weekStart, weekEnd, mailEx);
            } finally {
                if (file.exists()) {
                    file.delete();
                }
            }

        } catch (Exception e) {
            log.error("Exception while sending weekly inactive task owners mail", e);
        }
    }

    public Object getDailyTaskTrackers(EffortRecordVM effortRecordVM, CustomUserDetails customUserDetails) {
        String methodName = "getDailyTaskTracker";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);

        Map<String, Object> response = new HashMap<>();

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

            Date filterStartDate = null;
            Date filterEndDate = null;

            if (StringUtil.haveValue(effortRecordVM.getActualStartDate())
                && StringUtil.haveValue(effortRecordVM.getActualEndDate())) {
                filterStartDate = StringUtil.getDayStartTimeFromSimpleDateString1(effortRecordVM.getActualStartDate());
                filterEndDate = StringUtil.getDayEndTimeFromSimpleDateString1(effortRecordVM.getActualEndDate());

                query.addCriteria(new Criteria().andOperator(
                    Criteria.where("actual_start_date").lte(filterEndDate),
                    new Criteria().orOperator(
                        Criteria.where("actual_close_date").gte(filterStartDate),
                        Criteria.where("actual_close_date").is(null)
                    )
                ));
            }

            query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
            List<FeedbackHistory> feedbackList = mongoTemplate.find(query, FeedbackHistory.class);
            List<Map<String, Object>> allLogs = new ArrayList<>();

            for (FeedbackHistory f : feedbackList) {
                if (f.getActualEffort() != null && f.getActualEffort() > 0 && f.getActualStartDate() != null
                    && !f.getActualStartDate().before(filterStartDate)
                    && !f.getActualStartDate().after(filterEndDate)) {
                    allLogs.add(buildLogMap(f.getActualEffort(), f.getTaskOwner(), f.getProject(), f.getTeam(), "task",
                        f.getActualStartDate()));
                }

                if (f.getWorkLogEntries() != null) {
                    for (WorkLogEntry entry : f.getWorkLogEntries()) {
                        if (entry != null && entry.getHours() != null && entry.getHours() > 0 && entry.getDate() != null
                            && !entry.getDate().before(filterStartDate) && !entry.getDate().after(filterEndDate)) {
                            allLogs.add(buildLogMap(entry.getHours(), f.getTaskOwner(), f.getProject(), f.getTeam(),
                                "task", entry.getDate()));
                        }
                    }
                }
            }

            List<Subtask> subtaskList = subtaskService.getAllSubtasks();
            for (Subtask subtask : subtaskList) {
                if (subtask.getActualEffort() != null && subtask.getActualEffort() > 0
                    && subtask.getActualStartDate() != null && !subtask.getActualStartDate().before(filterStartDate)
                    && !subtask.getActualStartDate().after(filterEndDate)) {
                    allLogs.add(buildLogMap(subtask.getActualEffort(), subtask.getTaskOwner(), subtask.getProject(),
                        subtask.getTeam(), "task", subtask.getActualStartDate()));
                }

                if (subtask.getWorkLogEntries() != null) {
                    for (WorkLogEntry entry : subtask.getWorkLogEntries()) {
                        if (entry != null && entry.getHours() != null && entry.getHours() > 0 && entry.getDate() != null
                            && !entry.getDate().before(filterStartDate) && !entry.getDate().after(filterEndDate)) {
                            allLogs.add(buildLogMap(entry.getHours(), subtask.getTaskOwner(), subtask.getProject(),
                                subtask.getTeam(), "task", entry.getDate()));
                        }
                    }
                }
            }

            Query meetingQuery = new Query();
            meetingQuery.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
            if (StringUtil.haveValue(effortRecordVM.getProject())) {
                meetingQuery.addCriteria(Criteria.where("project").is(effortRecordVM.getProject()));
            }
            if (StringUtil.haveValue(effortRecordVM.getTaskOwner())) {
                meetingQuery.addCriteria(Criteria.where("meeting_by").is(effortRecordVM.getTaskOwner()));
            }
            if (StringUtil.haveValue(effortRecordVM.getTeam())) {
                meetingQuery.addCriteria(Criteria.where("team").is(effortRecordVM.getTeam()));
            }
            if (filterStartDate != null && filterEndDate != null) {
                meetingQuery.addCriteria(Criteria.where("date").gte(filterStartDate).lte(filterEndDate));
            }

            List<MeetingTimeTracker> meetingList = mongoTemplate.find(meetingQuery, MeetingTimeTracker.class);
            for (MeetingTimeTracker m : meetingList) {
                if (m.getTotalTime() != null && m.getTotalTime() > 0 && m.getDate() != null) {
                    allLogs.add(buildLogMap(m.getTotalTime(), m.getMeetingBy(), m.getProject(), m.getTeam(), "meeting",
                        m.getDate()));
                }
            }

            Map<String, List<Map<String, Object>>> grouped = allLogs.stream().collect(Collectors.groupingBy(m -> {
                return (m.get("taskOwner") + "|" + m.get("project") + "|" + m.get("team") + "|" + m.get("date"));
            }));

            List<Map<String, Object>> groupedList = new ArrayList<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
                String[] parts = entry.getKey().split("\\|", -1);
                if ("Unknown".equalsIgnoreCase(parts[0]))
                    continue;

                List<Map<String, Object>> records = entry.getValue();
                double taskEffort = records.stream().filter(m -> "task".equals(m.get("type")))
                    .mapToDouble(m -> ((Number) m.get("hours")).doubleValue()).sum();

                double meetingTime = records.stream().filter(m -> "meeting".equals(m.get("type")))
                    .mapToDouble(m -> ((Number) m.get("hours")).doubleValue()).sum();

                Map<String, Object> map = new HashMap<>();
                map.put("taskOwner", parts[0]);
                map.put("project", parts[1]);
                map.put("team", parts[2]);
                map.put("date", parts[3]);
                map.put("totalEffort", taskEffort + meetingTime);
                map.put("totalTaskEffort", taskEffort);
                map.put("totalMeetingTime", meetingTime);

                groupedList.add(map);
            }

            groupedList.sort(Comparator.comparing(m -> m.get("taskOwner").toString(), String.CASE_INSENSITIVE_ORDER));

            double grandTotalEffort = groupedList.stream()
                .mapToDouble(m -> ((Number) m.get("totalEffort")).doubleValue()).sum();

            Map<String, Object> data = new HashMap<>();
            data.put("totalActualEffort", grandTotalEffort);
            data.put("list", groupedList);
            response.put("data", data);

            log.info(this.getClass() + " " + methodName + " " + Constants.END);
            return response;

        } catch (Exception e) {
            log.error(this.getClass() + " " + methodName + " Exception: " + e.getMessage(), e);
            throw new CustomParameterizedException(
                RegisteredException.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
                CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.DAILY_TASK_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
        }
    }

    /**
     * Fetch leave dates for all owners within the given date range
     */
    private Map<String, Set<LocalDate>> getLeaveDatesForOwners(Set<String> owners, String weekStartStr, String weekEndStr) {
        Map<String, Set<LocalDate>> leaveDatesByOwner = new HashMap<>();

        try {
            Date filterStartDate = StringUtil.getDayStartTimeFromSimpleDateString1(weekStartStr);
            Date filterEndDate = StringUtil.getDayEndTimeFromSimpleDateString1(weekEndStr);

            Query query = new Query();
            query.addCriteria(Criteria.where("name").in(owners));
            query.addCriteria(Criteria.where("start_date").lte(filterEndDate));
            query.addCriteria(Criteria.where("end_date").gte(filterStartDate));
            query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

            List<OptimaLeaveTracker> leaveRecords = mongoTemplate.find(query, OptimaLeaveTracker.class);

            for (OptimaLeaveTracker leave : leaveRecords) {
                String ownerName = leave.getName();
                if (ownerName == null || ownerName.trim().isEmpty()) {
                    continue;
                }

                Date leaveStart = leave.getStartDate();
                Date leaveEnd = leave.getEndDate();

                if (leaveStart == null || leaveEnd == null) {
                    continue;
                }

                LocalDate startLocal = leaveStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endLocal = leaveEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                LocalDate weekStart = LocalDate.parse(weekStartStr);
                LocalDate weekEnd = LocalDate.parse(weekEndStr);

                for (LocalDate date = startLocal; !date.isAfter(endLocal); date = date.plusDays(1)) {
                    if (!date.isBefore(weekStart) && !date.isAfter(weekEnd)) {
                        leaveDatesByOwner
                            .computeIfAbsent(ownerName, k -> new HashSet<>())
                            .add(date);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching leave records: {}", e.getMessage(), e);
        }

        return leaveDatesByOwner;
    }

	@Override
	public Object uploadFile(MultipartFile file, CustomUserDetails customUserDetails) {
		String methodName = "uploadFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		List<FeedbackHistory> list = new ArrayList<>();
		List<String> exceptions = new ArrayList<>();
		Map<String, List<FeedbackHistory>> assigneeTaskMap = new LinkedHashMap<>();
		Map<String, String> ownerEmailCache = new HashMap<>();
		try {
			String fileName = file.getOriginalFilename();
			if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
				throw new CustomParameterizedException(RegisteredException.FILE_UPLOAD_SERVICE_EXCEPTION.getException(),
						CustomExceptionCode.EXCEL_GENERATION_EXCEPTION.getErrMsg(),
						CustomExceptionCode.EXCEL_GENERATION_EXCEPTION.getErrCode());
			}

			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Set<String> projectNames = new HashSet<>();

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (int r = 0; r <= sheet.getLastRowNum(); r++) {
					Row row = sheet.getRow(r);
					if (row == null)
						continue;
					for (Cell cell : row) {
						String val = getCellString(cell);
						if (StringUtil.haveValue(val)) {
							projectNames.add(val.trim());
						}
					}
				}
			}

			Set<String> existingKeys = new HashSet<>();
			if (!projectNames.isEmpty()) {
				Query query = new Query();
				query.addCriteria(Criteria.where("project").in(projectNames));
				List<FeedbackHistory> existingRecords = mongoTemplate.find(query, FeedbackHistory.class);
				for (FeedbackHistory fh : existingRecords) {
					existingKeys.add(generateUniqueKey(fh.getProject(), fh.getSubject(), fh.getCategory()));
				}
			}

			Map<String, Integer> projectSequenceCache = new HashMap<>();
			for (String project : projectNames) {
				String projectKey = project.trim().toUpperCase().replaceAll("\\s+", "");
				Query query = new Query();
				query.addCriteria(Criteria.where("project").is(project.trim()));
				query.with(new Sort(Sort.Direction.DESC, Constants.CREATED_ON));
				query.limit(1);
				FeedbackHistory latestTicket = mongoTemplate.findOne(query, FeedbackHistory.class);
				int lastSequence = 0;
				if (latestTicket != null && StringUtil.haveValue(latestTicket.getTicketNumber())) {
					String[] parts = latestTicket.getTicketNumber().split("-");
					if (parts.length == 2 && StringUtil.isNumeric(parts[1])) {
						lastSequence = Integer.parseInt(parts[1]);
					}
				}
				projectSequenceCache.put(projectKey, lastSequence);
			}

			Set<String> fileKeys = new HashSet<>();

			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				Sheet sheet = workbook.getSheetAt(sheetIndex);
				String sheetName = workbook.getSheetName(sheetIndex);
				if (sheet == null)
					continue;

				Row headerRow = null;
				int headerRowIndex = -1;
				for (int r = 0; r <= Math.min(5, sheet.getLastRowNum()); r++) {
					Row row = sheet.getRow(r);
					if (row == null)
						continue;
					for (Cell cell : row) {
						String val = normalizeHeader(getCellString(cell));
						if ("PROJECTNAME".equals(val) || "PROJECT".equals(val)) {
							headerRow = row;
							headerRowIndex = r;
							break;
						}
					}
					if (headerRow != null)
						break;
				}

				if (headerRow == null) {
					exceptions.add("Sheet '" + sheetName + "': Header not found");
					continue;
				}

				Map<String, Integer> headerMap = new HashMap<>();
				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
					Cell cell = headerRow.getCell(i);
					if (cell != null) {
						headerMap.put(getCellString(cell), i);
					}
				}

				int projectNameCol = findColumnIndexAdvanced(headerMap, "PROJECT_NAME");
				int actionItemCol = findColumnIndexAdvanced(headerMap, "ACTION_ITEM");
				int categoryCol = findColumnIndexAdvanced(headerMap, "CATEGORY");
				int priorityCol = findColumnIndexAdvanced(headerMap, "PRIORITY");
				int recordedDateCol = findColumnIndexAdvanced(headerMap, "RECORDED_DATE");
				int deadlineCol = findColumnIndexAdvanced(headerMap, "DEADLINE_DATE");
				int statusCol = findColumnIndexAdvanced(headerMap, "STATUS");
				int instanceCol        = findColumnIndexAdvanced(headerMap, "INSTANCE");
				int projectCategoryCol = findColumnIndexAdvanced(headerMap, "PROJECT_CATEGORY");
				int assignedToCol = findColumnIndexAdvanced(headerMap, "ASSIGNED_TO");

				if (actionItemCol == -1) {
					exceptions.add("Sheet '" + sheetName + "': ACTION ITEM not detected");
					continue;
				}

				String lastProjectName = null;

				for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if (row == null)
						continue;

					String actionItemVal = getCellString(getSafeCell(row, actionItemCol));
					if (!StringUtil.haveValue(actionItemVal))
						continue;

					String projectName = getCellString(getSafeCell(row, projectNameCol));
					if (!StringUtil.haveValue(projectName))
						projectName = lastProjectName;
					else
						lastProjectName = projectName;

					List<String> categories = new ArrayList<>();
					String catVal = getCellString(getSafeCell(row, categoryCol));
					if (StringUtil.haveValue(catVal)) {
						for (String part : catVal.split("/")) {
							try {
								categories.add(mapCategory(part.trim()));
							} catch (Exception e) {
								exceptions.add(
										"Sheet '" + sheetName + "' Row " + rowIndex + ": Invalid Category " + part);
							}
						}
					}
					if (categories.isEmpty())
						categories.add(null);

					String priority = mapPriority(getCellString(getSafeCell(row, priorityCol)));
					Date createdOn = parseExcelDate(getSafeCell(row, recordedDateCol));
					Date closeDate = parseExcelDate(getSafeCell(row, deadlineCol));
					String status = mapStatus(getCellString(getSafeCell(row, statusCol)));
					String instanceVal        = getCellString(getSafeCell(row, instanceCol));
					String projectCategoryVal = getCellString(getSafeCell(row, projectCategoryCol));
					String assignedToVal      = getCellString(getSafeCell(row, assignedToCol));

					for (String category : categories) {
						String uniqueKey = generateUniqueKey(projectName, actionItemVal, category);
						if (existingKeys.contains(uniqueKey)) {
							exceptions.add("Sheet '" + sheetName + "' Row " + rowIndex + ": Duplicate in DB");
							continue;
						}
						if (fileKeys.contains(uniqueKey)) {
							exceptions.add("Sheet '" + sheetName + "' Row " + rowIndex + ": Duplicate in File");
							continue;
						}
						fileKeys.add(uniqueKey);

						FeedbackHistory item = new FeedbackHistory();
						item.setInstance(instanceVal);
						item.setProject(projectName);
						item.setProjectCategory(projectCategoryVal);
						item.setCustomer(projectName);
						item.setSubject(actionItemVal);
						item.setDescription(actionItemVal);
						item.setEpic(actionItemVal);
						item.setCategory(category);
						item.setPriority(priority);
						item.setTaskOwner(assignedToVal);
						item.setCreatedOn(createdOn);
						item.setEstimatedStartDate(createdOn);
						item.setEstimatedCloseDate(closeDate);
						if (StringUtil.haveValue(status)) {
							item.setStatus(status);
						} else {
							item.setStatus(Constants.FEEDBACK_STATUS_OPEN);
						}
						item.setUploaded(true);

						if (StringUtil.haveValue(projectName)) {
							String projectKey = projectName.trim().toUpperCase().replaceAll("\\s+", "");
							int nextSequence = projectSequenceCache.getOrDefault(projectKey, 0) + 1;
							projectSequenceCache.put(projectKey, nextSequence);
							item.setTicketNumber(String.format("%s-%03d", projectKey, nextSequence));
						}

						list.add(item);
						if (StringUtil.haveValue(assignedToVal)) {
						    assigneeTaskMap.computeIfAbsent(assignedToVal, k -> new ArrayList<>()).add(item);
						}
					}
				}
			}
			workbook.close();
			if (!list.isEmpty()) {
			    mongoTemplate.insertAll(list);
			}

			for (Map.Entry<String, List<FeedbackHistory>> entry : assigneeTaskMap.entrySet()) {
			    String assignedToName  = entry.getKey();
			    List<FeedbackHistory> tasks = entry.getValue();

			    try {
			        String resolvedEmail = ownerEmailCache.computeIfAbsent(assignedToName, ownerName -> {
			            Query ownerQuery = new Query();
			            ownerQuery.addCriteria(Criteria.where("task_owner").is(ownerName));
			            TaskOwnerEmail taskOwnerEmail = mongoTemplate.findOne(ownerQuery, TaskOwnerEmail.class);
			            return (taskOwnerEmail != null) ? taskOwnerEmail.getEmail() : null;
			        });

			        if (StringUtil.haveValue(resolvedEmail)) {
			            String emailContent = buildBulkTaskMailBody(assignedToName, tasks);

			            EmailMV emailMV = new EmailMV();
			            emailMV.setTo(Collections.singletonList(resolvedEmail));
			            emailMV.setSubject("You have " + tasks.size() + " task(s) assigned to you");
			            emailMV.setHtml(true);
			            emailMV.setMultipart(false);
			            emailMV.setContent(emailContent);

			            emailService.sendEmail(emailMV);
			            log.info("Assignment mail sent to '{}' ({} tasks)", resolvedEmail, tasks.size());
			        } else {
			            log.warn("No email found for task owner '{}'", assignedToName);
			        }
			    } catch (Exception mailEx) {
			        log.warn("Mail failed for assignee '{}': {}", assignedToName, mailEx.getMessage());
			    }
			}

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return list;

		} catch (Exception e) {
			log.error("Error: ", e);
			throw new CustomParameterizedException(RegisteredException.FILE_UPLOAD_SERVICE_EXCEPTION.getException(),
					CustomExceptionCode.FILE_EXTENSION_EXCEPTION.getErrMsg(),
					CustomExceptionCode.FILE_IS_EMPTY.getErrCode());
		}
	}

	private String buildBulkTaskMailBody(String assignedToName, List<FeedbackHistory> tasks) {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

	    StringBuilder rows = new StringBuilder();
	    for (FeedbackHistory item : tasks) {
	        String createdOn      = item.getCreatedOn()          != null ? sdf.format(item.getCreatedOn())          : "N/A";
	        String estimatedClose = item.getEstimatedCloseDate() != null ? sdf.format(item.getEstimatedCloseDate()) : "N/A";

	        rows.append("<tr>")
	            .append("<td>").append(nvl(item.getTicketNumber()))    .append("</td>")
	            .append("<td>").append(nvl(item.getInstance()))        .append("</td>")
	            .append("<td>").append(nvl(item.getProject()))         .append("</td>")
	            .append("<td>").append(nvl(item.getProjectCategory())) .append("</td>")
	            .append("<td>").append(nvl(item.getSubject()))         .append("</td>")
	            .append("<td>").append(nvl(item.getCategory()))        .append("</td>")
	            .append("<td>").append(nvl(item.getPriority()))        .append("</td>")
	            .append("<td>").append(nvl(item.getStatus()))          .append("</td>")
	            .append("<td>").append(createdOn)                      .append("</td>")
	            .append("<td>").append(estimatedClose)                 .append("</td>")
	            .append("</tr>");
	    }

	    return "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
	        + "<p>Hello " + assignedToName + ",</p>"
	        + "<p>The following <b>" + tasks.size() + " task(s)</b> have been assigned to you:</p>"
	        + "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; width: 100%; font-size: 13px;'>"
	        + "<thead style='background: #2c3e50; color: #fff;'>"
	        + "<tr>"
	        + "<th>Ticket No</th>"
	        + "<th>Project</th>"
	        + "<th>Project Category</th>"
	        + "<th>Subject</th>"
	        + "<th>Category</th>"
	        + "<th>Priority</th>"
	        + "<th>Status</th>"
	        + "<th>Recorded Date</th>"
	        + "<th>Deadline Date</th>"
	        + "</tr>"
	        + "</thead>"
	        + "<tbody>"
	        + rows
	        + "</tbody>"
	        + "</table>"
	        + "<br/><p style='color:#888;font-size:12px;'>This is an automated email. Please do not reply.</p>"
	        + "</body></html>";
	}

	private String nvl(String value) {
	    return (value != null && !value.trim().isEmpty()) ? value : "N/A";
	}

	@Override
	public Object getRecord(SearchVM searchVM, Pageable pageable) {
		String methodName = "getRecord";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		List<FeedbackHistory> list = null;
		Query query = new Query();
		PaginationDataMV paginationDataMV = null;
		long noOfDocs;
		try {
			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));

			if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
				Date startDate = Date.from(
						LocalDate.parse(searchVM.getStartDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
								.atStartOfDay().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
				Date endDate = Date
						.from(LocalDate.parse(searchVM.getEndDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
								.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
				query.addCriteria(new Criteria().andOperator(Criteria.where("actual_start_date").gte(startDate),
						Criteria.where("actual_close_date").lte(endDate)));
			}

			if (StringUtil.haveValue(searchVM.getStatus())) {
				query.addCriteria(Criteria.where("status").is(searchVM.getStatus()));
			}

			noOfDocs = mongoOperation.count(query, FeedbackHistory.class);
			query.with(new Sort(Sort.Direction.DESC, "actual_start_date"));
			query.with(pageable);
			list = mongoTemplate.find(query, FeedbackHistory.class);

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
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
					+ customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(
					RegisteredException.MEETING_TIME_TRACKER_DETAILS_FETCHING_EXCEPTION.getException(),
					CustomExceptionCode.MEETING_TIME_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrMsg(),
					CustomExceptionCode.MEETING_TIME_TRACKER_DETAILS_FETCHING_EXCEPTION.getErrCode());
		}
	}

	// Helper methods for Excel upload
	private Cell getSafeCell(Row row, int colIndex) {
		if (row == null || colIndex < 0)
			return null;
		return row.getCell(colIndex);
	}

	private String normalizeHeader(String header) {
		if (header == null)
			return "";
		return header.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
	}

	private int findColumnIndexAdvanced(Map<String, Integer> headerMap, String standardKey) {
		List<String> aliases = HEADER_ALIASES.get(standardKey);
		if (aliases == null)
			return -1;
		int bestMatchIndex = -1;
		int bestScore = Integer.MAX_VALUE;
		for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
			String normalizedHeader = normalizeHeader(entry.getKey());
			for (String alias : aliases) {
				if (normalizedHeader.equals(alias)) {
					return entry.getValue();
				}
			}
			for (String alias : aliases) {
				int distance = levenshteinDistance(normalizedHeader, alias);
				if (distance < bestScore && distance <= 3) {
					bestScore = distance;
					bestMatchIndex = entry.getValue();
				}
			}
		}
		return bestMatchIndex;
	}

	private int levenshteinDistance(String a, String b) {
		int[][] dp = new int[a.length() + 1][b.length() + 1];
		for (int i = 0; i <= a.length(); i++)
			dp[i][0] = i;
		for (int j = 0; j <= b.length(); j++)
			dp[0][j] = j;
		for (int i = 1; i <= a.length(); i++) {
			for (int j = 1; j <= b.length(); j++) {
				int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
				dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
			}
		}
		return dp[a.length()][b.length()];
	}

	private String generateUniqueKey(String project, String subject, String category) {
		return (project + "|" + subject + "|" + category).toUpperCase().replaceAll("\\s+", "");
	}

	private String getCellString(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellTypeEnum()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell))
				return null;
			double num = cell.getNumericCellValue();
			if (num == Math.floor(num))
				return String.valueOf((long) num);
			return String.valueOf(num);
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			try {
				return String.valueOf((long) cell.getNumericCellValue());
			} catch (Exception e) {
				return cell.getStringCellValue();
			}
		default:
			return null;
		}
	}

	private Date parseExcelDate(Cell cell) {
		if (cell == null)
			return null;
		if (cell.getCellTypeEnum() == CellType.NUMERIC
				&& org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
			return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue());
		}
		String val = getCellString(cell);
		if (val == null || val.trim().isEmpty())
			return null;
		val = val.trim();
		String normalized = val.replaceAll("(?i)(\\d+)(st|nd|rd|th)", "$1");
		if (!normalized.matches(".*\\d{4}.*")) {
			normalized = normalized + " " + Calendar.getInstance().get(Calendar.YEAR);
		}
		String[] patterns = new String[] { "d MMMM yyyy", "d MMM yyyy", "MMMM d yyyy", "MMM d yyyy", "dd-MMM-yyyy",
				"dd/MMM/yyyy", "dd-MMM-yy", "dd/MM/yyyy", "dd-MM-yyyy", "dd.MM.yyyy", "MM/dd/yyyy", "MM-dd-yyyy",
				"yyyy-MM-dd", "yyyy/MM/dd", "dd MMM yyyy", "dd MMMM yyyy", "MMMM dd, yyyy", "MMM dd, yyyy", "dd-MM-yy",
				"dd/MM/yy", "MM/dd/yy", "yyyy.MM.dd", "yyyyMMdd", "dd MMM yy", "d MMM yy", "d MMMM yy" };
		for (String pattern : patterns) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
				sdf.setLenient(false);
				return sdf.parse(normalized);
			} catch (java.text.ParseException ignored) {
			}
		}
		throw new IllegalArgumentException("Unrecognized date format: " + val);
	}

	private String mapPriority(String val) {
	    if (val == null || val.trim().isEmpty()) {
	        return null;
	    }
	    String trimmedVal = val.trim();
	    Map<String, String> priorityMap = new HashMap<>();

	    // Critical / P0 mappings
	    priorityMap.put("0", "Critical – P0");
	    priorityMap.put("P0", "Critical – P0");
	    priorityMap.put("CRITICAL", "Critical – P0");
	    priorityMap.put("CRITICAL – P0", "Critical – P0");
	    priorityMap.put("CRITICAL - P0", "Critical – P0");
	    priorityMap.put("CRITICAL P0", "Critical – P0");
	    priorityMap.put("BLOCKER", "Critical – P0");
	    priorityMap.put("BLOCKER – P0", "Critical – P0");

	    // Urgent / High / P1 mappings
	    priorityMap.put("1", "Urgent – P1");
	    priorityMap.put("P1", "Urgent – P1");
	    priorityMap.put("URGENT", "Urgent – P1");
	    priorityMap.put("URGENT – P1", "Urgent – P1");
	    priorityMap.put("URGENT - P1", "Urgent – P1");
	    priorityMap.put("URGENT P1", "Urgent – P1");
	    priorityMap.put("HIGH", "Urgent – P1");
	    priorityMap.put("HIGH – P1", "Urgent – P1");
	    priorityMap.put("HIGH - P1", "Urgent – P1");
	    priorityMap.put("HIGH P1", "Urgent – P1");

	    // Normal / Medium / P2 mappings
	    priorityMap.put("2", "Normal - P2");
	    priorityMap.put("P2", "Normal - P2");
	    priorityMap.put("NORMAL", "Normal - P2");
	    priorityMap.put("NORMAL - P2", "Normal - P2");
	    priorityMap.put("NORMAL – P2", "Normal - P2");
	    priorityMap.put("NORMAL P2", "Normal - P2");
	    priorityMap.put("MEDIUM", "Normal - P2");
	    priorityMap.put("MEDIUM – P2", "Normal - P2");
	    priorityMap.put("MEDIUM - P2", "Normal - P2");
	    priorityMap.put("MEDIUM P2", "Normal - P2");

	    // Low / P3 mappings
	    priorityMap.put("3", "Low - P3");
	    priorityMap.put("P3", "Low - P3");
	    priorityMap.put("LOW", "Low - P3");
	    priorityMap.put("LOW - P3", "Low - P3");
	    priorityMap.put("LOW – P3", "Low - P3");
	    priorityMap.put("LOW P3", "Low - P3");

	    String upperKey = trimmedVal.toUpperCase();
	    for (Map.Entry<String, String> entry : priorityMap.entrySet()) {
	        if (entry.getKey().equalsIgnoreCase(trimmedVal)) {
	            return entry.getValue();
	        }
	    }

	    for (Map.Entry<String, String> entry : priorityMap.entrySet()) {
	        if (upperKey.contains(entry.getKey().toUpperCase())) {
	            return entry.getValue();
	        }
	    }

	    String numberOnly = trimmedVal.replaceAll("[^0-9]", "");
	    if (!numberOnly.isEmpty()) {
	        try {
	            int priorityNum = Integer.parseInt(numberOnly);
	            switch (priorityNum) {
	                case 0: return "Critical – P0";
	                case 1: return "Urgent – P1";
	                case 2: return "Normal - P2";
	                case 3: return "Low - P3";
	                default: return "Normal - P2";
	            }
	        } catch (NumberFormatException e) {
	        }
	    }

	    return "Normal - P2";
	}

	private String mapStatus(String val) {
	    if (val == null || val.trim().isEmpty())
	        return null;

	    String normalized = val.trim().toUpperCase().replace("-", "_").replace(" ", "_");

	    switch (normalized) {
	        case "OPEN":
	        case "YET_TO_START":
	        case "YET TO START":
	            return "Open";
	        case "DONE":
	        case "CLOSED":
	        case "COMPLETED":
	        case "DONE/CLOSED":
	            return "Closed";
	        case "PENDING":
	        case "IN_PROGRESS":
	        case "INPROGRESS":
	        case "IN PROGRESS":
	            return "In_Progress";
	        case "NOT_YET_STARTED":
	        case "NOT YET STARTED":
	            return "Not Yet Started";
	        case "ACKNOWLEDGED":
	            return "Acknowledged";
	        case "FEATURE_RELEASED":
	        case "FEATURE RELEASED":
	            return "Feature Released";
	        case "RE_OPEN":
	        case "REOPEN":
	        case "RE OPEN":
	            return "Re Open";
	        default:
	            // If status not recognized, default to Open
	            return "Open";
	    }
	}

	private String mapCategory(String val) {
	    if (val == null || val.trim().isEmpty())
	        return null;

	    String upperVal = val.trim().toUpperCase();

	    Map<String, String> CATEGORY_MAP = new HashMap<String, String>() {
	        {
	            // BE Development
	            put("BE", "BE Development");
	            put("BE DEVELOPMENT", "BE Development");
	            put("BACKEND", "BE Development");
	            put("BACKEND DEVELOPMENT", "BE Development");

	            // FE Development
	            put("FE", "FE Development");
	            put("FE DEVELOPMENT", "FE Development");
	            put("FRONTEND", "FE Development");
	            put("FRONTEND DEVELOPMENT", "FE Development");

	            // Cloud / DevOps
	            put("CLOUD / DEVOPS", "Cloud / DevOps");
	            put("CLOUD/DEVOPS", "Cloud / DevOps");
	            put("CLOUD", "Cloud / DevOps");
	            put("DEVOPS", "Cloud / DevOps");

	            // UI / UX Design
	            put("UI / UX DESIGN", "UI / UX Design");
	            put("UI/UX", "UI / UX Design");
	            put("UI/UX DESIGN", "UI / UX Design");
	            put("UI", "UI / UX Design");
	            put("UX", "UI / UX Design");
	            put("DESIGN", "UI / UX Design");

	            // QA
	            put("QA", "QA");
	            put("TESTER", "QA");
	            put("TESTING", "QA");
	            put("QUALITY ASSURANCE", "QA");

	            // Mobile Development
	            put("MOBILE DEVELOPMENT", "Mobile Development");
	            put("MOBILE", "Mobile Development");
	            put("MOBILE APP", "Mobile Development");
	            put("IOS", "Mobile Development");
	            put("ANDROID", "Mobile Development");

	            // Python Development
	            put("PYTHON DEVELOPMENT", "Python Development");
	            put("PYTHON", "Python Development");

	            // IOT
	            put("IOT", "IOT");
	            put("INTERNET OF THINGS", "IOT");

	            // FE/BE (Both)
	            put("FE/BE", "FE Development");
	            put("BE/FE", "BE Development");
	        }
	    };

	    String normalized = CATEGORY_MAP.get(upperVal);
	    if (normalized == null) {
	        // If exact match not found, try partial match
	        for (Map.Entry<String, String> entry : CATEGORY_MAP.entrySet()) {
	            if (upperVal.contains(entry.getKey())) {
	                return entry.getValue();
	            }
	        }
	        // Default to FE Development if nothing matches
	        return "FE Development";
	    }
	    return normalized;
	}

	private static final Map<String, List<String>> HEADER_ALIASES = new HashMap<>();
	static {
		HEADER_ALIASES.put("PROJECT_NAME",
				Arrays.asList("PROJECTNAME", "PROJECT", "PROJNAME", "PROJ", "PROJECTTITLE", "PROJECTID"));
		HEADER_ALIASES.put("ACTION_ITEM",
				Arrays.asList("ACTIONITEM", "ACTION", "TASK", "WORKITEM", "ISSUE", "SUBJECT", "TITLE", "DESCRIPTION"));
		HEADER_ALIASES.put("CATEGORY", Arrays.asList("CATEGORY", "TYPE", "MODULE", "COMPONENT"));
		HEADER_ALIASES.put("PRIORITY", Arrays.asList("PRIORITY", "PRIO", "SEVERITY", "LEVEL"));
		HEADER_ALIASES.put("RECORDED_DATE",
				Arrays.asList("RECORDEDDATE", "CREATEDDATE", "DATE", "LOGDATE", "CREATEDON"));
		HEADER_ALIASES.put("DEADLINE_DATE", Arrays.asList("DEADLINE", "DUEDATE", "CLOSEDATE", "TARGETDATE", "ENDDATE"));
		HEADER_ALIASES.put("STATUS", Arrays.asList("STATUS", "STATE", "PROGRESS", "CURRENTSTATUS"));
		HEADER_ALIASES.put("INSTANCE", Arrays.asList("INSTANCE", "INST", "INSTANCENAME", "INSTANCEID"));
		HEADER_ALIASES.put("PROJECT_CATEGORY",
				Arrays.asList("PROJECTCATEGORY", "PROJCATEGORY", "PROJECTTYPE", "PROJTYPE", "CATEGORYOFPROJECT"));
		HEADER_ALIASES.put("ASSIGNED_TO",
			    Arrays.asList("ASSIGNEDTO", "ASSIGNED", "ASSIGNEE", "OWNER", "RESPONSIBLE", "ASSIGNEDUSER"));
	}

}
