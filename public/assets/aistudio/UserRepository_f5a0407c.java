package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}