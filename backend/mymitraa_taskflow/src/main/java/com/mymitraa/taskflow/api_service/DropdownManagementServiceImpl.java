package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.DropdownManagement;
import com.mymitraa.taskflow.api_repository.DropdownManagementRepository;
import com.mymitraa.taskflow.api_vm.DropdownManagementVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DropdownManagementServiceImpl implements DropdownManagementService {

    private static final Logger log = LoggerFactory.getLogger(DropdownManagementServiceImpl.class);

    @Autowired
    private DropdownManagementRepository dropdownManagementRepository;

    @Override
    public Object save(DropdownManagement obj, CustomUserDetails customUserDetails) {
        log.info("Saving DropdownManagement item: {}", obj.getDomainName());
        obj.setCreatedOn(new Date());
        obj.setDeleteFlag(false);
        if (customUserDetails != null) {
            obj.setCreatedBy(customUserDetails.getUsername());
        }
        return dropdownManagementRepository.save(obj);
    }

    @Override
    public Object getById(String id, CustomUserDetails customUserDetails) {
        log.info("Getting DropdownManagement by id: {}", id);
        return dropdownManagementRepository.findByDeleteFlagAndId(false, id);
    }

    @Override
    public Object fetchAll(String username, Object searchVM) {
        log.info("Fetching all active DropdownManagement items");
        return dropdownManagementRepository.findAll();
    }

    @Override
    public Object getAllWithPagination(String username, Object searchVM, Pageable pageable) {
        log.info("Fetching DropdownManagement items with pagination");
        return dropdownManagementRepository.findAll(pageable);
    }

    @Override
    public Object delete(String id, CustomUserDetails customUserDetails) {
        log.info("Soft-deleting DropdownManagement item: {}", id);
        DropdownManagement item = dropdownManagementRepository.findById(id).orElse(null);
        if (item != null) {
            item.setDeleteFlag(true);
            item.setUpdatedOn(new Date());
            if (customUserDetails != null) {
                item.setUpdatedBy(customUserDetails.getUsername());
            }
            dropdownManagementRepository.save(item);
        }
        return id;
    }

    @Override
    public Object download(DropdownManagementVM obj, CustomUserDetails customUserDetails) {
        log.info("Downloading DropdownManagement data");
        return dropdownManagementRepository.findAll();
    }

    @Override
    public Object update(String id, DropdownManagementVM obj, CustomUserDetails customUserDetails) {
        log.info("Updating DropdownManagement item: {}", id);
        DropdownManagement item = dropdownManagementRepository.findById(id).orElse(null);
        if (item != null) {
            item.setDomainName(obj.getDomainName());
            item.setValue(obj.getValue());
            item.setSubValue(obj.getSubValue());
            item.setModule(obj.getModule());
            item.setSubModule(obj.getSubModule());
            item.setUpdatedOn(new Date());
            if (customUserDetails != null) {
                item.setUpdatedBy(customUserDetails.getUsername());
            }
            return dropdownManagementRepository.save(item);
        }
        return null;
    }

    @Override
    public Object getDropdown(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        log.info("Querying Dropdown by domain and value");
        return dropdownManagementRepository.findByDeleteFlagAndDomainNameAndValueOrderByCreatedOnDesc(
                false, dropdownManagement.getDomainName(), dropdownManagement.getValue());
    }

    @Override
    public Object getDropdownSubValue(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        log.info("Querying Dropdown by domain, value, and subValue");
        return dropdownManagementRepository.findByDeleteFlagAndDomainNameAndValueAndSubValueOrderByCreatedOnDesc(
                false, dropdownManagement.getDomainName(), dropdownManagement.getValue(), dropdownManagement.getSubValue());
    }

    @Override
    public Object getDropdownSubValueModule(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        log.info("Querying Dropdown by domain, value, subValue, and module");
        return dropdownManagementRepository.findByDeleteFlagAndDomainNameAndValueAndSubValueAndModuleOrderByCreatedOnDesc(
                false, dropdownManagement.getDomainName(), dropdownManagement.getValue(),
                dropdownManagement.getSubValue(), dropdownManagement.getModule());
    }

    @Override
    public Object getDropdownSubValueModuleSubModule(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails) {
        log.info("Querying Dropdown by domain, value, subValue, module, and subModule");
        return dropdownManagementRepository.findByDeleteFlagAndDomainNameAndValueAndSubValueAndModuleAndSubModuleOrderByCreatedOnDesc(
                false, dropdownManagement.getDomainName(), dropdownManagement.getValue(),
                dropdownManagement.getSubValue(), dropdownManagement.getModule(), dropdownManagement.getSubModule());
    }

    @Override
    public com.mymitraa.taskflow.api_domain.AdvanceSearchList getParamterList(CustomUserDetails customUserDetails) {
        log.info("Getting dropdown parameter list");
        return new com.mymitraa.taskflow.api_domain.AdvanceSearchList();
    }
}
