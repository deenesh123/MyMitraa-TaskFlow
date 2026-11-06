package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Subtask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtaskRepository extends MongoRepository<Subtask, String> {

    Subtask findByIdAndDeleteFlag(String id, Boolean deleteFlag);

    Subtask findByTicketNumberAndDeleteFlag(String ticketNumber, Boolean deleteFlag);

    Page<Subtask> findByParentFeedbackHistoryIdAndDeleteFlag(String parentFeedbackHistoryId, Boolean deleteFlag, Pageable pageable);

    List<Subtask> findByParentFeedbackHistoryIdAndDeleteFlag(String parentFeedbackHistoryId, Boolean deleteFlag);

    long countByParentFeedbackHistoryIdAndDeleteFlag(String parentFeedbackHistoryId, Boolean deleteFlag);

    @Query(value = "{ 'parent_feedback_history_id': ?0, 'delete_flag': false, 'status': { $ne: 'Closed' } }", exists = true)
    boolean hasOpenSubtasks(String parentFeedbackHistoryId);
}
