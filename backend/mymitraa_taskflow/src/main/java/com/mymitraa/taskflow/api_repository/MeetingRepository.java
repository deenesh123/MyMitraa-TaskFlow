package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MeetingRepository extends MongoRepository<Meeting, String> {

    List<Meeting> findByTitle(String title);
    List<Meeting> findByOwner(String owner);
}