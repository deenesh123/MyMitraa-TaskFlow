package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.AdvanceSearchList;
import com.mymitraa.taskflow.api_domain.DropdownManagement;
import com.mymitraa.taskflow.api_repository.DropdownManagementRepository;
import com.mymitraa.taskflow.api_vm.DropdownManagementVM;
import com.mymitraa.taskflow.config.Constants;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DropdownManagementServiceImpl implements DropdownManagementService {

    private static final Logger log = LoggerFactory.getLogger(DropdownManagementServiceImpl.class);

    @Autowired
    private DropdownManagementRepository dropdownManagementRepository;

    @Autowired
    private MongoOperations mongoOperation;

    @Override
    public Object save(DropdownManagement obj, CustomUserDetails customUserDetails) {
        log.info("Saving DropdownManagement: {}", obj.getDomainName());
        obj.setDeleteFlag(false);
        obj.setCreatedOn(new Date());
        if (customUserDetails != null) {
            obj.setCreatedBy(customUserDetails.getUsername());
        }
        return dropdownManagementRepository.save(obj);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Fetching DropdownManagement by ID: {}", id);
        return dropdownManagementRepository.findById(id).orElse(null);
    }

    @Override
    public Object fetchAll(String username, Object searchVM) {
        log.info("Fetching all DropdownManagement items");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        query.with(Sort.by(Sort.Direction.ASC, "domain_name", "value"));
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public Object getAllWithPagination(String username, Object searchVM, Pageable pageable) {
        log.info("Fetching DropdownManagement items with pagination");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        query.with(pageable);
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Deleting DropdownManagement ID: {}", id);
        DropdownManagement existing = dropdownManagementRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDeleteFlag(true);
            existing.setDeletedOn(new Date());
            if (customUserDetails != null) {
                existing.setDeletedBy(customUserDetails.getUsername());
            }
            dropdownManagementRepository.save(existing);
            return true;
        }
        return false;
    }

    @Override
    public Object update(String id, DropdownManagementVM obj, CustomUserDetails customUserDetails) {
        log.info("Updating DropdownManagement ID: {}", id);
        DropdownManagement existing = dropdownManagementRepository.findById(id).orElse(null);
        if (existing != null) {
            if (obj.getDomainName() != null) existing.setDomainName(obj.getDomainName());
            if (obj.getValue() != null) existing.setValue(obj.getValue());
            if (obj.getSubValue() != null) existing.setSubValue(obj.getSubValue());
            if (obj.getModule() != null) existing.setModule(obj.getModule());
            if (obj.getSubModule() != null) existing.setSubModule(obj.getSubModule());
            if (obj.getComments() != null) existing.setComments(obj.getComments());
            existing.setModifiedDateTime(new Date());
            if (customUserDetails != null) {
                existing.setModifiedBy(customUserDetails.getUsername());
            }
            return dropdownManagementRepository.save(existing);
        }
        return null;
    }

    @Override
    public Object download(DropdownManagementVM obj, CustomUserDetails customUserDetails) {
        log.info("Exporting DropdownManagement data");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (obj != null && obj.getDomainName() != null && !obj.getDomainName().isEmpty()) {
            query.addCriteria(Criteria.where("domain_name").is(obj.getDomainName()));
        }
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public Object getDropdown(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (dropdownManagement.getDomainName() != null) {
            query.addCriteria(Criteria.where("domain_name").is(dropdownManagement.getDomainName()));
        }
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public Object getDropdownSubValue(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (dropdownManagement.getDomainName() != null) {
            query.addCriteria(Criteria.where("domain_name").is(dropdownManagement.getDomainName()));
        }
        if (dropdownManagement.getValue() != null) {
            query.addCriteria(Criteria.where("value").is(dropdownManagement.getValue()));
        }
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public Object getDropdownSubValueModuleSubModule(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (dropdownManagement.getDomainName() != null) query.addCriteria(Criteria.where("domain_name").is(dropdownManagement.getDomainName()));
        if (dropdownManagement.getModule() != null) query.addCriteria(Criteria.where("module").is(dropdownManagement.getModule()));
        if (dropdownManagement.getSubModule() != null) query.addCriteria(Criteria.where("sub_module").is(dropdownManagement.getSubModule()));
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public Object getDropdownSubValueModule(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
        if (dropdownManagement.getDomainName() != null) query.addCriteria(Criteria.where("domain_name").is(dropdownManagement.getDomainName()));
        if (dropdownManagement.getModule() != null) query.addCriteria(Criteria.where("module").is(dropdownManagement.getModule()));
        return mongoOperation.find(query, DropdownManagement.class);
    }

    @Override
    public AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        log.info("Fetching DropdownManagement parameters");
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false).and("tableName").is("DropdownManagement"));
        AdvanceSearchList list = mongoOperation.findOne(query, AdvanceSearchList.class);
        return list;
    }
}
