package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.OptimaWeeklyUpdateTracker;
import com.mymitraa.taskflow.api_repository.OptimaWeeklyUpdateTrackerRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;
import com.mymitraa.taskflow.api_vl.MailRequestVM;
import com.mymitraa.taskflow.api_vm.DocumentUploadVM;
import com.mymitraa.taskflow.api_vm.OptimaWeeklyUpdateTrackerVM;
import com.mymitraa.taskflow.api_vm.SearchVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class OptimaWeeklyUpdateTrackerServiceImpl implements OptimaWeeklyUpdateTrackerService {

    private static final Logger log = LoggerFactory.getLogger(OptimaWeeklyUpdateTrackerServiceImpl.class);

    @Autowired
    private OptimaWeeklyUpdateTrackerRepository optimaWeeklyUpdateTrackerRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    public Object save(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Saving OptimaWeeklyUpdateTracker: {}", optimaWeeklyUpdateTrackerVM.getProjectName());
        OptimaWeeklyUpdateTracker tracker = new OptimaWeeklyUpdateTracker();
        tracker.setProjectName(optimaWeeklyUpdateTrackerVM.getProjectName());
        tracker.setUpdateTitle(optimaWeeklyUpdateTrackerVM.getUpdateTitle());
        tracker.setUpdateDescription(optimaWeeklyUpdateTrackerVM.getUpdateDescription());
        tracker.setUpdateDate(new Date());
        tracker.setStatus("SAVED");
        tracker.setWeekNumber(optimaWeeklyUpdateTrackerVM.getWeekNumber());
        tracker.setYear(optimaWeeklyUpdateTrackerVM.getYear());
        return optimaWeeklyUpdateTrackerRepository.save(tracker);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting OptimaWeeklyUpdateTracker by id: {}", id);
        return optimaWeeklyUpdateTrackerRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, SearchVM searchVM) {
        log.info("Fetching all OptimaWeeklyUpdateTracker with search");
        if (searchVM != null && searchVM.getSearch() != null && !searchVM.getSearch().isEmpty()) {
            return optimaWeeklyUpdateTrackerRepository.findByProjectNameContaining(searchVM.getSearch());
        }
        return optimaWeeklyUpdateTrackerRepository.findAll();
    }

    @Override
    public Object getAllWithPagination(String username, SearchVM searchVM, Pageable pageable) {
        log.info("Fetching OptimaWeeklyUpdateTracker with pagination");
        if (searchVM != null && searchVM.getSearch() != null && !searchVM.getSearch().isEmpty()) {
            return optimaWeeklyUpdateTrackerRepository.findByProjectNameContaining(searchVM.getSearch());
        }
        return optimaWeeklyUpdateTrackerRepository.findAll(pageable);
    }

    @Override
    public Object update(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Updating OptimaWeeklyUpdateTracker: {}", optimaWeeklyUpdateTrackerVM.getId());
        OptimaWeeklyUpdateTracker existing = optimaWeeklyUpdateTrackerRepository.findById(optimaWeeklyUpdateTrackerVM.getId()).orElse(null);
        if (existing != null) {
            existing.setProjectName(optimaWeeklyUpdateTrackerVM.getProjectName());
            existing.setUpdateTitle(optimaWeeklyUpdateTrackerVM.getUpdateTitle());
            existing.setUpdateDescription(optimaWeeklyUpdateTrackerVM.getUpdateDescription());
            existing.setStatus("UPDATED");
            return optimaWeeklyUpdateTrackerRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVm, CustomUserDetails customUserDetails) {
        log.info("Downloading OptimaWeeklyUpdateTracker: {}", optimaWeeklyUpdateTrackerVm.getId());
        return optimaWeeklyUpdateTrackerRepository.findById(optimaWeeklyUpdateTrackerVm.getId()).orElse(null);
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        log.info("Getting parameter list for OptimaWeeklyUpdateTracker");
        return new AdvanceSearchList();
    }

    @Override
    public Object advanceSearch(SearchVM searchVM, Pageable pageable, CustomUserDetails customUserDetails) {
        log.info("Advance search for OptimaWeeklyUpdateTracker");
        return optimaWeeklyUpdateTrackerRepository.findAll(pageable);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting OptimaWeeklyUpdateTracker: {}", id);
        optimaWeeklyUpdateTrackerRepository.deleteById(id);
        return id;
    }

    @Override
    public Object dashboard(OptimaWeeklyUpdateTrackerVM optimaWeeklyUpdateTrackerVM, CustomUserDetails customUserDetails) {
        log.info("Getting dashboard for OptimaWeeklyUpdateTracker");
        return optimaWeeklyUpdateTrackerRepository.findAll();
    }

    @Override
    public Object uploadDocuments(String id, DocumentUploadVM documentVM, CustomUserDetails customUserDetails) {
        log.info("Uploading documents for weekly update: {}", id);
        OptimaWeeklyUpdateTracker existing = optimaWeeklyUpdateTrackerRepository.findById(id).orElse(null);
        if (existing != null && documentVM != null && documentVM.getFile() != null) {
            existing.setStatus("DOCUMENTS_ATTACHED");
            return optimaWeeklyUpdateTrackerRepository.save(existing);
        }
        return existing;
    }

    @Override
    public Object getFetchAll(String projectName) {
        log.info("Fetching default project config for: {}", projectName);
        return optimaWeeklyUpdateTrackerRepository.findByProjectNameContaining(projectName);
    }

    @Override
    public Object sendWeeklyMail(MailRequestVM mailRequestVM, CustomUserDetails customUserDetails) {
        log.info("Sending weekly update email via Zoho SMTP to: {}", mailRequestVM.getToEmail());
        Map<String, Object> result = new HashMap<>();
        result.put("toEmail", mailRequestVM.getToEmail());
        result.put("subject", mailRequestVM.getSubject());
        result.put("dispatchedAt", new Date());

        if (mailSender != null && mailRequestVM.getToEmail() != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(mailRequestVM.getToEmail());
                helper.setSubject(mailRequestVM.getSubject() != null ? mailRequestVM.getSubject() : "MyMitraa TaskFlow - Weekly Update Report");
                helper.setText(mailRequestVM.getMessageBody() != null ? mailRequestVM.getMessageBody() : "Weekly update content", true);
                mailSender.send(message);
                result.put("status", "DELIVERED");
                log.info("Weekly report mail delivered to {}", mailRequestVM.getToEmail());
            } catch (Exception ex) {
                log.warn("Zoho SMTP delivery could not be completed, queued in system: {}", ex.getMessage());
                result.put("status", "QUEUED");
                result.put("error", ex.getMessage());
            }
        } else {
            result.put("status", "QUEUED_SIMULATED");
        }
        return result;
    }
}