package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DocumentRepository extends MongoRepository<Document, String> {

    Document findByFilename(String filename);
    List<Document> findByUploadedBy(String uploadedBy);
}