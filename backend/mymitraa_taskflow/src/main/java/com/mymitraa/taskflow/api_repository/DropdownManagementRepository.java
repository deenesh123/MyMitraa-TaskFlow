package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.DropdownManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropdownManagementRepository extends MongoRepository<DropdownManagement, String> {

    DropdownManagement findByDeleteFlagAndId(Boolean deleteFlag, String id);

    List<DropdownManagement> findByDomainNameAndDeleteFlag(String domainName, Boolean deleteFlag);

    List<DropdownManagement> findByDeleteFlagOrderByCreatedOnDesc(Boolean deleteFlag);

    Page<DropdownManagement> findByDeleteFlagOrderByCreatedOnDesc(Boolean deleteFlag, Pageable pageable);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueAndSubValueAndModuleAndSubModuleOrderByCreatedOnDesc(
            Boolean deleteFlag, String domainName, String value, String subValue, String module, String subModule);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueAndSubValueAndModuleOrderByCreatedOnDesc(
            Boolean deleteFlag, String domainName, String value, String subValue, String module);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueAndSubValueOrderByCreatedOnDesc(
            Boolean deleteFlag, String domainName, String value, String subValue);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueOrderByCreatedOnDesc(
            Boolean deleteFlag, String domainName, String value);
}