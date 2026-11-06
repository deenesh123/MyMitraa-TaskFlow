package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Document findByFilename(String filename);
    Document findByUploadedBy(String uploadedBy);
}