package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
    User findByEmail(String email);
}