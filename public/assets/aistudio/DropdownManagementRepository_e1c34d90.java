package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.DropdownManagement;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DropdownManagementRepository extends MongoRepository<DropdownManagement, String> {

    DropdownManagement findByDeleteFlagAndId(Boolean false1, String id);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueAndSubValueAndModuleAndSubModuleOrderByCreatedOnDesc(
            Boolean false1, String domainName, String value, String subValue, String module, String subModule);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueAndSubValueAndModuleOrderByCreatedOnDesc(
            Boolean false1, String domainName, String value, String subValue, String module);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueAndSubValueOrderByCreatedOnDesc(
            Boolean false1, String domainName, String value, String subValue);

    DropdownManagement findByDeleteFlagAndDomainNameAndValueOrderByCreatedOnDesc(
            Boolean false1, String domainName, String value);

    DropdownManagement findByDomainNameAndDeleteFlag(String domainName, boolean b);
}