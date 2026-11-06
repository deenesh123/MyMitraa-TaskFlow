package com.mymitraa.taskflow.api_repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_domain.GlobalFileUploads;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalFileUploadsRepository extends MongoRepository<GlobalFileUploads, String> {

    GlobalFileUploads findByCategory(String category);

    GlobalFileUploads findByFileName(String fileName);

    List<GlobalFileUploads> findByCategoryContainingAndStatus(String category, String status);

    Page<GlobalFileUploads> findByStatus(String status, Pageable pageable);

    void deleteByOperationDateTimeBefore(Date date);

    List<GlobalFileUploads> findByStatus(String status);

    List<GlobalFileUploads> findByUploadedBy(String uploadedBy);

    List<GlobalFileUploads> findByOperationDateTimeBetween(Date start, Date end);

    List<GlobalFileUploads> findByApprovedDateBetween(Date start, Date end);

    List<GlobalFileUploads> findByRejectionDateBetween(Date start, Date end);

    List<GlobalFileUploads> findByCategoryAndStatus(String category, String status);

    List<GlobalFileUploads> findByUploadedByAndStatus(String uploadedBy, String status);
}