package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.TaskOwnerEmail;
import com.mymitraa.taskflow.security.CustomUserDetails;

/**
 * Service interface for Task Owner Email notifications.
 */
public interface TaskOwnerEmailService {

    Object save(TaskOwnerEmail taskOwnerEmail, CustomUserDetails customUserDetails);
}
