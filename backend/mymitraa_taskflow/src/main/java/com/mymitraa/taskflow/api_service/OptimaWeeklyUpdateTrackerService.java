package com.mymitraa.taskflow.api_service;

import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_vl.MailRequestVM;
import com.mymitraa.taskflow.api_vm.DocumentUploadVM;
import com.mymitraa.taskflow.api_vm.OptimaWeeklyUpdateTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;

public interface OptimaWeeklyUpdateTrackerService {

    Object save(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails);

    Object getById(String id, CustomUserDetails customUserDetails);

    Object fetchAll(String username, SearchVM searchVM);

    Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable);

    Object update(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails);

    Object download(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails);

    AdvanceSearchList getParamterList(CustomUserDetails customUserDetails);

    Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails);

    Object delete(String id, CustomUserDetails customUserDetails);

    Object dashboard(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails);

    Object uploadDocuments(String id, DocumentUploadVM documentVM, CustomUserDetails customUserDetails);

    Object getFetchAll(String projectName);

    Object sendWeeklyMail(MailRequestVM mailRequestVM, CustomUserDetails customUserDetails);
}
