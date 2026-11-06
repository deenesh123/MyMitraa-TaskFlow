package com.mymitraa.taskflow.api_repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_domain.FeedbackHistory;
import com.mymitraa.taskflow.api_domain.WorkLogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackHistoryRepository extends MongoRepository<FeedbackHistory, String> {

    FeedbackHistory findByTicketNumber(String ticketNumber);

    List<FeedbackHistory> findByTicketNumberContainingOrSubjectContainingOrCommentsContaining(String ticketNumber, String subject, String comments);

    List<FeedbackHistory> findByTicketNumberContainingOrSubjectContaining(String ticketNumber, String subject);

    List<FeedbackHistory> findByEpicContainingOrSubModuleContaining(String epic, String subModule);

    List<FeedbackHistory> findByFileNameContainingAndUploadedTrue(String fileName);

    Page<FeedbackHistory> findByUploadedTrue(Pageable pageable);

    List<FeedbackHistory> findByProjectContaining(String project);

    List<FeedbackHistory> findByStatus(String status);

    List<FeedbackHistory> findByProject(String project);

    List<FeedbackHistory> findByModule(String module);

    List<FeedbackHistory> findByTaskOwner(String taskOwner);

    List<FeedbackHistory> findByClassification(String classification);

    List<FeedbackHistory> findByCategory(String category);

    List<FeedbackHistory> findByEstimatedEffortBetween(Double min, Double max);

    List<FeedbackHistory> findByActualEffortBetween(Double min, Double max);

    List<FeedbackHistory> findByProductivitySavingsBetween(Double min, Double max);

    List<FeedbackHistory> findByWhoReported(String whoReported);

    List<FeedbackHistory> findByReviewerCommentsContaining(String reviewerComments);

    List<FeedbackHistory> findBySubtaskCountGreaterThan(Integer count);

    List<FeedbackHistory> findByLastSubtaskUpdateAfter(Date date);

    List<FeedbackHistory> findByInstance(String instance);

    List<FeedbackHistory> findByProjectCategory(String projectCategory);

    List<FeedbackHistory> findByCodingBetween(Double min, Double max);

    List<FeedbackHistory> findByAiToolBetween(Double min, Double max);

    List<FeedbackHistory> findByInternalMeetingBetween(Double min, Double max);
}