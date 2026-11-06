package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends MongoRepository<Meeting, String> {

    Meeting findByTitle(String title);
}