package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_vm.DocumentUploadVM;
import com.mymitraa.taskflow.api_vm.MailRequestVM;
import com.mymitraa.taskflow.api_vm.OptimaWeeklyUpdateTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;

public interface OptimaWeeklyUpdateTrackerService {

    Object save(OptimaWeeklyUpdateTrackerVM vm, CustomUserDetails customUserDetails);

    Object getById(String id, CustomUserDetails customUserDetails);

    Object fetchAll(String username, SearchVM searchVM);

    Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable);

    Object delete(String id, CustomUserDetails customUserDetails);

    Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails);

    AdvanceSearchList getParamterList(CustomUserDetails customUserDetails);

    Object update(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails);

    Object download(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails);

    Object dashboard(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVm, CustomUserDetails customUserDetails);

    Object uploadDocuments(String id, DocumentUploadVM documentVM, CustomUserDetails customUserDetails);

    Object getFetchAll(String projectName);

    Object sendWeeklyMail(MailRequestVM mailRequestVM, CustomUserDetails customUserDetails);
}
