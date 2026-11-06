package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.FeedbackHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FeedbackHistoryRepository extends MongoRepository<FeedbackHistory, String> {

    FeedbackHistory findByTicketNumber(String ticketNumber);

    FeedbackHistory findByTicketNumberAndDeleteFlag(String ticketNumber, Boolean deleteFlag);

    FeedbackHistory findByFeedbackHistoryIdAndDeleteFlag(String feedbackHistoryId, Boolean deleteFlag);

    List<FeedbackHistory> findByDeleteFlag(Boolean deleteFlag);

    Page<FeedbackHistory> findByDeleteFlag(Boolean deleteFlag, Pageable pageable);

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
}