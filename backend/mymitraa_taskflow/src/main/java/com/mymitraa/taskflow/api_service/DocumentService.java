package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.Document;
import com.mymitraa.taskflow.api_repository.DocumentRepository;
import org.springframework.data.domain.Pageable;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import com.mymitraa.taskflow.api_successcode.SuccessCode;

public interface DocumentService {

    Object save(Document obj);

    Object fetchAll(Pageable pageable);

    Object getById(Long id);

    Object update(Document obj);

    Object delete(Long id);

    Object getDocument(String filename);
}