package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.TaskOwnerEmail;
import com.mymitraa.taskflow.api_repository.TaskOwnerEmailRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskOwnerEmailServiceImpl implements TaskOwnerEmailService {

    private static final Logger log = LoggerFactory.getLogger(TaskOwnerEmailServiceImpl.class);

    @Autowired
    private TaskOwnerEmailRepository taskOwnerEmailRepository;

    @Override
    public Object save(TaskOwnerEmail taskOwnerEmail, CustomUserDetails customUserDetails) {
        log.info("Saving TaskOwnerEmail for user: {}", customUserDetails.getUsername());
        return taskOwnerEmailRepository.save(taskOwnerEmail);
    }

}