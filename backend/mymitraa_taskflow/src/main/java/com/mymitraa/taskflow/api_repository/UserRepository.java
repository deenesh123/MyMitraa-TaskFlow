package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
    Optional<User> findByEmail(String email);
}