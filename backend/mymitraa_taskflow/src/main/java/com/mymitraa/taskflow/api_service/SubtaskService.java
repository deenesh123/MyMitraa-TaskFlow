package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.Subtask;
import com.mymitraa.taskflow.api_domain.WorkLogEntry;
import com.mymitraa.taskflow.api_vm.AssignTaskVM;
import com.mymitraa.taskflow.api_vm.FeedbackHistoryVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubtaskService {

    Subtask createSubtask(String parentFeedbackHistoryId, FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    Subtask getSubtaskById(String subtaskId);

    Page<Subtask> getSubtasksByParent(String parentFeedbackHistoryId, Pageable pageable);

    Subtask getSubtaskByTicketNumber(String ticketNumber);

    List<Subtask> getAllSubtasks();

    Subtask updateSubtask(String subtaskId, FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    Subtask assignSubtask(String subtaskId, AssignTaskVM assignTaskVM, CustomUserDetails customUserDetails);

    Subtask closeSubtask(String subtaskId, FeedbackHistoryVM feedbackHistoryVM, CustomUserDetails customUserDetails);

    Subtask addWorkLog(String subtaskId, List<WorkLogEntry> workLogEntries, CustomUserDetails customUserDetails);

    Subtask deleteSubtask(String subtaskId, CustomUserDetails customUserDetails);

    long getSubtaskCountByParent(String parentFeedbackHistoryId);

    boolean hasOpenSubtasks(String parentFeedbackHistoryId);

    Object advanceSearchSubtasks(SearchVM searchVM, Pageable pageable);
}
