package com.kelltontech.optima.api_service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kelltontech.optima.api_domain.TaskOwnerEmail;
import com.kelltontech.optima.api_errors.CustomExceptionCode;
import com.kelltontech.optima.api_errors.RegisteredException;
import com.kelltontech.optima.api_repository.TaskOwnerEmailRepository;
import com.kelltontech.optima.api_service.TaskOwnerEmailService;
import com.kelltontech.optima.api_util.StringUtil;
import com.kelltontech.optima.api_util.WellServiceAuditUtil;
import com.kelltontech.optima.config.Constants;
import com.kelltontech.optima.security.CustomUserDetails;
import com.kelltontech.optima.web.rest.errors.CustomParameterizedException;

@Service
public class TaskOwnerEmailServiceImpl implements TaskOwnerEmailService {
    private final Logger log = LoggerFactory.getLogger(TaskOwnerEmailServiceImpl.class);

    @Autowired
    private TaskOwnerEmailRepository taskOwnerEmailRepository;
    @Autowired
    private WellServiceAuditUtil wellServiceAuditUtil;
    @Override
    public Object save(TaskOwnerEmail taskOwnerEmail, CustomUserDetails customUserDetails) {
        String methodName = "saveTaskOwnerEmail";
        log.info(this.getClass() + " " + methodName + " " + Constants.START);
        TaskOwnerEmail taskOwnerEmailWithId = null;
        try {
            taskOwnerEmailWithId = taskOwnerEmailRepository.save(taskOwnerEmail);

            if (StringUtil.haveValue(taskOwnerEmailWithId.getId()))
                wellServiceAuditUtil.updateAuditLog(customUserDetails.getId(), customUserDetails.getUsername(),
                        taskOwnerEmailWithId.getId(), "Inserted a new record in TaskOwnerEmail : " + taskOwnerEmailWithId , "");

            log.info(this.getClass() + " " + methodName + " " + Constants.END);
        } catch (CustomParameterizedException customException) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
                + customException.getMessage() + " ]");
            throw customException;
        } catch (Exception exception) {
            log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
                + " ]");
            throw new CustomParameterizedException( RegisteredException.TASK_OWNER_EMAIL_DATA_SAVING_EXCEPTION.getException(),
                CustomExceptionCode.TASK_OWNER_EMAIL_DATA_SAVING_EXCEPTION.getErrMsg(),
                CustomExceptionCode.TASK_OWNER_EMAIL_DATA_SAVING_EXCEPTION.getErrCode());
        }
        return taskOwnerEmailWithId;
    }

}