package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.GlobalFileUploads;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalFileUploadsRepository extends MongoRepository<GlobalFileUploads, String> {

    GlobalFileUploads findByCategory(String category);

    GlobalFileUploads findByFileName(String fileName);

    List<GlobalFileUploads> findByStatus(String status);

    List<GlobalFileUploads> findByUploadedBy(String uploadedBy);

    List<GlobalFileUploads> findByOperationDateTimeBetween(Date start, Date end);

    List<GlobalFileUploads> findByApprovedDateBetween(Date start, Date end);

    List<GlobalFileUploads> findByRejectionDateBetween(Date start, Date end);

    List<GlobalFileUploads> findByCategoryAndStatus(String category, String status);

    List<GlobalFileUploads> findByUploadedByAndStatus(String uploadedBy, String status);
}