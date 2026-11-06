package com.kelltontech.optima.api_service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.kelltontech.optima.api_domain.FeedbackHistory;
import com.kelltontech.optima.api_domain.Subtask;
import com.kelltontech.optima.api_domain.WorkLogEntry;
import com.kelltontech.optima.api_errors.CustomExceptionCode;
import com.kelltontech.optima.api_errors.RegisteredException;
import com.kelltontech.optima.api_mv.PaginationDataMV;
import com.kelltontech.optima.api_repository.SubtaskRepository;
import com.kelltontech.optima.api_service.SubtaskService;
import com.kelltontech.optima.api_util.StringUtil;
import com.kelltontech.optima.api_util.WellServiceAuditUtil;
import com.kelltontech.optima.api_vm.AssignTaskVM;
import com.kelltontech.optima.api_vm.FeedbackHistoryVM;
import com.kelltontech.optima.web.rest.vm.SearchVM;
import com.kelltontech.optima.config.Constants;
import com.kelltontech.optima.repository.FeedbackHistoryRepository;
import com.kelltontech.optima.security.CustomUserDetails;
import com.kelltontech.optima.web.rest.errors.CustomParameterizedException;

@Service
public class SubtaskServiceImpl implements SubtaskService {

	private final Logger log = LoggerFactory.getLogger(SubtaskServiceImpl.class);

	@Autowired
	private SubtaskRepository subtaskRepository;

	@Autowired
	private FeedbackHistoryRepository feedbackHistoryRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private WellServiceAuditUtil wellServiceAuditUtil;

	@Override
	public Subtask createSubtask(String parentFeedbackHistoryId, FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "createSubtask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
        Double coding = 0.0;
        Double aiTool = 0.0;
        Double meeting = 0.0;
		try {
			FeedbackHistory parentTask = feedbackHistoryRepository.findByFeedbackHistoryIdAndDeleteFlag(parentFeedbackHistoryId, false);

			if (parentTask == null) {
				throw new CustomParameterizedException(
					RegisteredException.SUBTASK_FEEDBACKHISTORY_ID_NOT_FOUND_EXCEPTION.getException(),
					CustomExceptionCode.SUBTASK_FEEDBACKHISTORY_ID_NOT_FOUND_EXCEPTION.getErrMsg(),
					CustomExceptionCode.SUBTASK_FEEDBACKHISTORY_ID_NOT_FOUND_EXCEPTION.getErrCode()
				);
			}

			String parentTicket = parentTask.getTicketNumber();
			long subtaskCount = subtaskRepository.countByParentFeedbackHistoryIdAndDeleteFlag(parentFeedbackHistoryId, false);
			String subtaskTicket = parentTicket + "-" + (subtaskCount + 1);

			Subtask subtask = new Subtask();
			subtask.setSubtaskId("SUB_" + System.currentTimeMillis());
			subtask.setTicketNumber(subtaskTicket);
			subtask.setParentTicketNumber(parentTicket);
			subtask.setParentFeedbackHistoryId(parentFeedbackHistoryId);
			subtask.setProject(parentTask.getProject());
			subtask.setEpic(parentTask.getEpic());
			subtask.setPriority(parentTask.getPriority());
			subtask.setCustomer(parentTask.getCustomer());

			// Set subject
			if (StringUtil.haveValue(feedbackHistoryVM.getSubject())) {
				subtask.setSubject(feedbackHistoryVM.getSubject());
			} else if (StringUtil.haveValue(parentTask.getSubject())) {
				subtask.setSubject(parentTask.getSubject());
			}

			// Set description
			if (StringUtil.haveValue(feedbackHistoryVM.getDescription())) {
				subtask.setDescription(feedbackHistoryVM.getDescription());
			}

            if (StringUtil.haveValue(feedbackHistoryVM.getProduct())) {
                subtask.setProduct(feedbackHistoryVM.getProduct());
            }

			// Set dates
			if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedStartDate())) {
				subtask.setEstimatedStartDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedStartDate()));
			} else {
				subtask.setEstimatedStartDate(new Date());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
				subtask.setEstimatedCloseDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedCloseDate()));
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getActualStartDate())) {
				subtask.setActualStartDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getActualStartDate()));
			} else {
				subtask.setActualStartDate(new Date());
			}

			// Set other fields
			if (StringUtil.haveValue(feedbackHistoryVM.getModule())) {
				subtask.setModule(feedbackHistoryVM.getModule());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getSubModule())) {
				subtask.setSubModule(feedbackHistoryVM.getSubModule());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getTeam())) {
				subtask.setTeam(feedbackHistoryVM.getTeam());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getTaskType())) {
				subtask.setTaskType(feedbackHistoryVM.getTaskType());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getClassification())) {
				subtask.setClassification(feedbackHistoryVM.getClassification());
			}

            if (feedbackHistoryVM.getCoding() != null) {
                subtask.setCoding(feedbackHistoryVM.getCoding());
                coding = feedbackHistoryVM.getCoding();
            }

            if (feedbackHistoryVM.getAiTool() != null) {
                subtask.setAiTool(feedbackHistoryVM.getAiTool());
                aiTool = feedbackHistoryVM.getAiTool();
            }

            if (feedbackHistoryVM.getInternalMeeting() != null) {
                subtask.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
                meeting = feedbackHistoryVM.getInternalMeeting();
            }

            Double effort = coding + aiTool + meeting;
            subtask.setEstimatedEffort(effort);

			if (StringUtil.haveValue(feedbackHistoryVM.getTaskOwner())) {
				subtask.setTaskOwner(feedbackHistoryVM.getTaskOwner());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getCategory())) {
				subtask.setCategory(feedbackHistoryVM.getCategory());
			}

			if (StringUtil.haveValue(feedbackHistoryVM.getUserStories())) {
				subtask.setUserStories(feedbackHistoryVM.getUserStories());
			}

			// Set status
			subtask.setStatus(Constants.FEEDBACK_STATUS_OPEN);

			Subtask savedSubtask = subtaskRepository.save(subtask);

			// Update parent task status
			parentTask.setStatus(Constants.FEEDBACK_STATUS_IN_PROGRESS);
			feedbackHistoryRepository.save(parentTask);

			wellServiceAuditUtil.updateAuditLog(
				customUserDetails.getId(),
				customUserDetails.getUsername(),
				parentFeedbackHistoryId,
				"Created sub-task: " + subtaskTicket,
				methodName
			);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return savedSubtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.SUBTASK_SAVING_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCURRED_WHILE_SAVING_SUBTASK.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCURRED_WHILE_SAVING_SUBTASK.getErrCode()
			);
		}
	}

	@Override
	public Subtask getSubtaskById(String subtaskId) {
		String methodName = "getSubtaskById";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Subtask subtask = subtaskRepository.findByIdAndDeleteFlag(subtaskId, false);
			if (subtask == null) {
				throw new CustomParameterizedException(
					RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
					CustomExceptionCode.DATA_NOT_FOUND.getErrCode()
				);
			}

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return subtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public Page<Subtask> getSubtasksByParent(String parentFeedbackHistoryId, Pageable pageable) {
		String methodName = "getSubtasksByParent";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Page<Subtask> subtasks = subtaskRepository.findByParentFeedbackHistoryIdAndDeleteFlag(parentFeedbackHistoryId, false, pageable);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return subtasks;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public Subtask getSubtaskByTicketNumber(String ticketNumber) {
		String methodName = "getSubtaskByTicketNumber";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Subtask subtask = subtaskRepository.findByTicketNumberAndDeleteFlag(ticketNumber, false);
			if (subtask == null) {
				throw new CustomParameterizedException(
					RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
					CustomExceptionCode.DATA_NOT_FOUND.getErrMsg(),
					CustomExceptionCode.DATA_NOT_FOUND.getErrCode()
				);
			}

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return subtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public List<Subtask> getAllSubtasks() {
		String methodName = "getAllSubtasks";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false));
			List<Subtask> subtasks = mongoTemplate.find(query, Subtask.class);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return subtasks;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public Subtask updateSubtask(String subtaskId, FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "updateSubtask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
        Double coding = 0.0;
        Double aiTool = 0.0;
        Double meeting = 0.0;
		try {
			Subtask subtask = getSubtaskById(subtaskId);

			if (StringUtil.haveValue(feedbackHistoryVM.getSubject())) {
				subtask.setSubject(feedbackHistoryVM.getSubject());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getDescription())) {
				subtask.setDescription(feedbackHistoryVM.getDescription());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getModule())) {
				subtask.setModule(feedbackHistoryVM.getModule());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getSubModule())) {
				subtask.setSubModule(feedbackHistoryVM.getSubModule());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getTeam())) {
				subtask.setTeam(feedbackHistoryVM.getTeam());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getTaskType())) {
				subtask.setTaskType(feedbackHistoryVM.getTaskType());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getClassification())) {
				subtask.setClassification(feedbackHistoryVM.getClassification());
			}
            if (feedbackHistoryVM.getCoding() != null) {
                subtask.setCoding(feedbackHistoryVM.getCoding());
                coding = feedbackHistoryVM.getCoding();
            }

            if (feedbackHistoryVM.getAiTool() != null) {
                subtask.setAiTool(feedbackHistoryVM.getAiTool());
                aiTool = feedbackHistoryVM.getAiTool();
            }

            if (feedbackHistoryVM.getInternalMeeting() != null) {
                subtask.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
                meeting = feedbackHistoryVM.getInternalMeeting();
            }

            Double effort = coding + aiTool + meeting;
            subtask.setEstimatedEffort(effort);
			if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedStartDate())) {
				subtask.setEstimatedStartDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedStartDate()));
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
				subtask.setEstimatedCloseDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedCloseDate()));
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getCategory())) {
				subtask.setCategory(feedbackHistoryVM.getCategory());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getUserStories())) {
				subtask.setUserStories(feedbackHistoryVM.getUserStories());
			}
			if (StringUtil.haveValue(feedbackHistoryVM.getPriority())) {
				subtask.setPriority(feedbackHistoryVM.getPriority());
			}

			Subtask updatedSubtask = subtaskRepository.save(subtask);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return updatedSubtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public Subtask assignSubtask(String subtaskId, AssignTaskVM assignTaskVM, CustomUserDetails customUserDetails) {
		String methodName = "assignSubtask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Subtask subtask = getSubtaskById(subtaskId);

			subtask.setStatus(Constants.FEEDBACK_STATUS_IN_PROGRESS);

			if (StringUtil.haveValue(assignTaskVM.getClassification())) {
				subtask.setClassification(assignTaskVM.getClassification());
			}
			if (StringUtil.haveValue(assignTaskVM.getCategory())) {
				subtask.setCategory(assignTaskVM.getCategory());
			}
			if (StringUtil.haveValue(assignTaskVM.getCategoryDescription())) {
				subtask.setCategoryDescription(assignTaskVM.getCategoryDescription());
			} else if (StringUtil.haveValue(subtask.getSubject())) {
				subtask.setCategoryDescription(subtask.getSubject());
			}
			if (StringUtil.haveValue(assignTaskVM.getTaskDescription())) {
				subtask.setTaskDescription(assignTaskVM.getTaskDescription());
			} else if (StringUtil.haveValue(subtask.getSubject())) {
				subtask.setTaskDescription(subtask.getSubject());
			}
			if (StringUtil.haveValue(assignTaskVM.getEstimatedEffort())) {
				subtask.setEstimatedEffort(assignTaskVM.getEstimatedEffort());
			}
			if (StringUtil.haveValue(assignTaskVM.getTaskOwner())) {
				subtask.setTaskOwner(assignTaskVM.getTaskOwner());
			}
			if (StringUtil.haveValue(assignTaskVM.getTeam())) {
				subtask.setTeam(assignTaskVM.getTeam());
			}
			if (StringUtil.haveValue(assignTaskVM.getTaskType())) {
				subtask.setTaskType(assignTaskVM.getTaskType());
			}
			if (StringUtil.haveValue(customUserDetails.getUsername())) {
				subtask.setAssignedBy(customUserDetails.getUsername());
			}

			subtask.setDateAssigned(new Date());

			if (StringUtil.haveValue(assignTaskVM.getEstimatedStartDate())) {
				subtask.setEstimatedStartDate(StringUtil.convertStringIntoDatePattern(assignTaskVM.getEstimatedStartDate()));
			}
			if (StringUtil.haveValue(assignTaskVM.getWhoReported())) {
				subtask.setWhoReported(assignTaskVM.getWhoReported());
			}
			if (StringUtil.haveValue(assignTaskVM.getActualStartDate())) {
				subtask.setActualStartDate(StringUtil.convertStringIntoDatePattern(assignTaskVM.getActualStartDate()));
			} else {
				subtask.setActualStartDate(new Date());
			}

			subtask.setModifiedBy(customUserDetails.getUsername());
			subtask.setModifiedDateTime(new Date());

			Subtask assignedSubtask = subtaskRepository.save(subtask);

			wellServiceAuditUtil.updateAuditLog(
				customUserDetails.getId(),
				customUserDetails.getUsername(),
				subtask.getParentFeedbackHistoryId(),
				"Subtask (" + subtask.getTicketNumber() + ") assigned to: " + assignTaskVM.getTaskOwner(),
				methodName
			);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return assignedSubtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.TASK_ASSIGN_EXCEPTION.getException(),
				CustomExceptionCode.TASK_ASSIGN_EXCEPTION.getErrMsg(),
				CustomExceptionCode.TASK_ASSIGN_EXCEPTION.getErrCode()
			);
		}
	}

	@Override
	public Subtask closeSubtask(String subtaskId, FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails) {
		String methodName = "closeSubtask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
        Double coding = 0.0;
        Double aiTool = 0.0;
        Double meeting = 0.0;
		try {
			Subtask subtask = getSubtaskById(subtaskId);

            if (feedbackHistoryVM.getCoding() != null) {
                subtask.setCoding(feedbackHistoryVM.getCoding());
                coding = feedbackHistoryVM.getCoding();
            }

            if (feedbackHistoryVM.getAiTool() != null) {
                subtask.setAiTool(feedbackHistoryVM.getAiTool());
                aiTool = feedbackHistoryVM.getAiTool();
            }

            if (feedbackHistoryVM.getInternalMeeting() != null) {
                subtask.setInternalMeeting(feedbackHistoryVM.getInternalMeeting());
                meeting = feedbackHistoryVM.getInternalMeeting();
            }

            Double effort = coding + aiTool + meeting;
			boolean hasWorkLogs = subtask.getWorkLogEntries() != null && !subtask.getWorkLogEntries().isEmpty();
			Double actualEffortToSet = hasWorkLogs ? null : effort;

			// Validate actual effort > estimated effort
			if (actualEffortToSet != null && subtask.getEstimatedEffort() != null) {
				if (actualEffortToSet <= subtask.getEstimatedEffort()) {
					throw new CustomParameterizedException(
						RegisteredException.TASK_CLOSE_EXCEPTION.getException(),
						"Actual Effort must be greater than Estimated Effort",
						CustomExceptionCode.TASK_CLOSE_EXCEPTION.getErrCode()
					);
				}
			}

			// Set closure fields
			if (StringUtil.haveValue(feedbackHistoryVM.getActualCloseDate())) {
				subtask.setActualCloseDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getActualCloseDate()));
			} else {
				subtask.setActualCloseDate(new Date());
			}

			subtask.setActualEffort(actualEffortToSet);
			subtask.setReviewerComments(feedbackHistoryVM.getReviewerComments());
			subtask.setReviewedBy(feedbackHistoryVM.getReviewedBy());

			if (StringUtil.haveValue(feedbackHistoryVM.getEstimatedCloseDate())) {
				subtask.setEstimatedCloseDate(StringUtil.convertStringIntoDatePattern(feedbackHistoryVM.getEstimatedCloseDate()));
			}

			subtask.setStatus(Constants.FEEDBACK_STATUS_CLOSED);
			Subtask closedSubtask = subtaskRepository.save(subtask);

			// Check if all subtasks of parent are closed
			boolean hasOpenSubtasks = subtaskRepository.hasOpenSubtasks(subtask.getParentFeedbackHistoryId());

			if (!hasOpenSubtasks) {
				FeedbackHistory parent = feedbackHistoryRepository.findByFeedbackHistoryIdAndDeleteFlag(
					subtask.getParentFeedbackHistoryId(), false
				);
				if (parent != null) {
					parent.setStatus(Constants.FEEDBACK_STATUS_CLOSED);
					parent.setActualCloseDate(new Date());
					feedbackHistoryRepository.save(parent);

					wellServiceAuditUtil.updateAuditLog(
						customUserDetails.getId(),
						customUserDetails.getUsername(),
						parent.getFeedbackHistoryId(),
						"All subtasks closed. Parent task auto-closed.",
						methodName
					);
				}
			}

			wellServiceAuditUtil.updateAuditLog(
				customUserDetails.getId(),
				customUserDetails.getUsername(),
				subtask.getParentFeedbackHistoryId(),
				"Subtask (" + subtask.getTicketNumber() + ") closed",
				methodName
			);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return closedSubtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.TASK_CLOSE_EXCEPTION.getException(),
				CustomExceptionCode.TASK_CLOSE_EXCEPTION.getErrMsg(),
				CustomExceptionCode.TASK_CLOSE_EXCEPTION.getErrCode()
			);
		}
	}

	@Override
	public Subtask addWorkLog(String subtaskId, List<WorkLogEntry> workLogEntries, CustomUserDetails customUserDetails) {
		String methodName = "addWorkLog";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Subtask subtask = getSubtaskById(subtaskId);

			Date allowedStartDate = subtask.getEstimatedStartDate();
			Date allowedEndDate = subtask.getEstimatedCloseDate();

			for (WorkLogEntry entry : workLogEntries) {
				if (entry.getDate().compareTo(allowedStartDate) < 0 || entry.getDate().compareTo(allowedEndDate) > 0) {
					throw new CustomParameterizedException(
						RegisteredException.INVALID_DATE.getException(),
						"Work log date must be between " + allowedStartDate + " and " + allowedEndDate,
						CustomExceptionCode.DATE_RANGE_EXCEPTION.getErrCode()
					);
				}

				if (entry.getHours() <= 0 || entry.getHours() > 24) {
					throw new CustomParameterizedException(
						RegisteredException.INVALID_DATE.getException(),
						"Work hours must be between 0 and 24",
						CustomExceptionCode.DATE_RANGE_EXCEPTION.getErrCode()
					);
				}

			}

			List<WorkLogEntry> existingLogs = subtask.getWorkLogEntries();
			if (existingLogs == null) {
				existingLogs = new ArrayList<>();
			}
			existingLogs.addAll(workLogEntries);
			subtask.setWorkLogEntries(existingLogs);

			double totalEffort = existingLogs.stream()
				.mapToDouble(WorkLogEntry::getHours)
				.sum();
			subtask.setActualEffort(totalEffort);

			subtask.setModifiedBy(customUserDetails.getUsername());
			subtask.setModifiedDateTime(new Date());

			Subtask updatedSubtask = subtaskRepository.save(subtask);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return updatedSubtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public Subtask deleteSubtask(String subtaskId, CustomUserDetails customUserDetails) {
		String methodName = "deleteSubtask";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Subtask subtask = getSubtaskById(subtaskId);

			subtask.setDeleteFlag(true);
			Subtask deletedSubtask = subtaskRepository.save(subtask);

			wellServiceAuditUtil.updateAuditLog(
				customUserDetails.getId(),
				customUserDetails.getUsername(),
				subtask.getParentFeedbackHistoryId(),
				"Deleted subtask: " + subtask.getTicketNumber(),
				methodName
			);

			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return deletedSubtask;

		} catch (CustomParameterizedException e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public long getSubtaskCountByParent(String parentFeedbackHistoryId) {
		String methodName = "getSubtaskCountByParent";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			long count = subtaskRepository.countByParentFeedbackHistoryIdAndDeleteFlag(parentFeedbackHistoryId, false);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return count;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public boolean hasOpenSubtasks(String parentFeedbackHistoryId) {
		String methodName = "hasOpenSubtasks";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			boolean hasOpen = subtaskRepository.hasOpenSubtasks(parentFeedbackHistoryId);
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return hasOpen;

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.FEEDBACK_HISTORY_EXCEPTION.getException(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrMsg(),
				CustomExceptionCode.EXCEPTION_OCCOR_WHILE_SAVING_FEEDBACK_HISTORY.getErrCode()
			);
		}
	}

	@Override
	public Object advanceSearchSubtasks(SearchVM searchVM, Pageable pageable) {
		String methodName = "advanceSearchSubtasks";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false));

			if (StringUtil.haveValue(searchVM.getParameter()) && StringUtil.haveValue(searchVM.getParametervalue())) {
				String fieldName = searchVM.getParameter();
				String fieldValue = searchVM.getParametervalue();

				if (Constants.DAILY_TASK_TRACKER_DATE.contains(fieldName)) {
					Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(fieldValue);
					Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(fieldValue);
					query.addCriteria(Criteria.where(fieldName).gte(startDate).lte(endDate));
				} else if (Constants.DAILY_TASK_TRACKER_DOUBLE.contains(fieldName)) {
					double value = Double.parseDouble(fieldValue);
					query.addCriteria(Criteria.where(fieldName).is(value));
				} else {
					query.addCriteria(Criteria.where(fieldName).regex(fieldValue, "i"));
				}
			}

			if (StringUtil.haveValue(searchVM.getStatus())) {
				query.addCriteria(Criteria.where("status").is(searchVM.getStatus()));
			}

			if (StringUtil.haveValue(searchVM.getStartDate()) && StringUtil.haveValue(searchVM.getEndDate())) {
				Date startDate = StringUtil.getDayStartTimeFromSimpleDateString(searchVM.getStartDate());
				Date endDate = StringUtil.getDayEndTimeFromSimpleDateString(searchVM.getEndDate());
				query.addCriteria(Criteria.where("created_on").gte(startDate).lte(endDate));
			}

			if (StringUtil.haveValue(searchVM.getSortName())) {
				Sort.Direction direction = "ASC".equalsIgnoreCase(searchVM.getSortType()) ?
					Sort.Direction.ASC : Sort.Direction.DESC;
				query.with(new Sort(direction, searchVM.getSortName()));
			} else {
				query.with(new Sort(Sort.Direction.DESC, Constants.MODIFIED_DATE_TIME));
			}

			if (searchVM.isEnablePagination()) {
				query.with(pageable);
				List<Subtask> subtasks = mongoTemplate.find(query, Subtask.class);
				long total = mongoTemplate.count(query, Subtask.class);

				PaginationDataMV paginationDataMV = new PaginationDataMV();
				paginationDataMV.setContent(subtasks);
				paginationDataMV.setNumber(pageable.getPageNumber());
				paginationDataMV.setNumberOfElements(subtasks.size());
				paginationDataMV.setSize(pageable.getPageSize());
				paginationDataMV.setTotalElements(total);
				paginationDataMV.setTotalPages((int) Math.ceil((double) total / pageable.getPageSize()));

				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return paginationDataMV;
			} else {
				List<Subtask> subtasks = mongoTemplate.find(query, Subtask.class);

				PaginationDataMV paginationDataMV = new PaginationDataMV();
				paginationDataMV.setContent(subtasks);
				paginationDataMV.setNumber(0);
				paginationDataMV.setNumberOfElements(subtasks.size());
				paginationDataMV.setSize(subtasks.size());
				paginationDataMV.setTotalElements(subtasks.size());
				paginationDataMV.setTotalPages(1);

				log.info(this.getClass() + " " + methodName + " " + Constants.END);
				return paginationDataMV;
			}

		} catch (Exception e) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
				RegisteredException.ADVACNE_SEARCH_EXCEPTION.getException(),
				CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrMsg(),
				CustomExceptionCode.ADVANCE_SEARCH_EXCEPTION.getErrCode()
			);
		}
	}
}
