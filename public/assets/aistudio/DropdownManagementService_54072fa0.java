package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.DropdownManagement;
import com.mymitraa.taskflow.api_vm.DropdownManagementVM;
import com.mymitraa.taskflow.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import com.kelltontech.optima.api_successcode.CustomResponse;
import com.kelltontech.optima.api_successcode.SuccessCode;

public interface DropdownManagementService {

    Object save(DropdownManagement obj, CustomUserDetails customUserDetails);

    Object getById(String id, CustomUserDetails customUserDetails);

    Object fetchAll(String username, Object searchVM );

    Object getAllWithPagination(String username, Object searchVM , Pageable pageable);

    Object delete(String id, CustomUserDetails customUserDetails);

    Object download(DropdownManagementVM obj, CustomUserDetails customUserDetails);

    Object update(String id, DropdownManagementVM obj, CustomUserDetails customUserDetails);

    Object getDropdown(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails);

    Object getDropdownSubValue(DropdownManagement dropdownManagement, CustomUserDetails customUserDetails);

    Object getDropdownSubValueModuleSubModule(DropdownManagement dropdownManagement,
            CustomUserDetails customUserDetails);

    Object getDropdownSubValueModule(DropdownManagement dropdownManagement,
            CustomUserDetails customUserDetails);
}