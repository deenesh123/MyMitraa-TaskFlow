package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Lead;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends MongoRepository<Lead, String> {
    List<Lead> findByStage(String stage);
    List<Lead> findByAssignedTo(String assignedTo);
}
