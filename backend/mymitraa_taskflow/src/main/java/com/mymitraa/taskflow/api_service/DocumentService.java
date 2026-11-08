package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.Document;
import org.springframework.data.domain.Pageable;

public interface DocumentService {

    Object save(Document obj);

    Object fetchAll(Pageable pageable);

    Object getById(String id);

    Object update(Document obj);

    Object delete(String id);

    Object getDocument(String filename);
}