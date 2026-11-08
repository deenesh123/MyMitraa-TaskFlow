package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
