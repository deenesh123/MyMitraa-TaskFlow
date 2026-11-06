package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Meeting findByTitle(String title);
}