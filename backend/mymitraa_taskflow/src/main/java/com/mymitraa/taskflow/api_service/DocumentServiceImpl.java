package com.mymitraa.taskflow.api_service;

import com.mymitraa.taskflow.api_domain.Document;
import com.mymitraa.taskflow.api_repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public Object save(Document obj) {
        log.info("Saving document: {}", obj.getFilename());
        obj.setDeleteFlag(false);
        if (obj.getCreatedOn() == null) {
            obj.setCreatedOn(new Date());
        }
        return documentRepository.save(obj);
    }

    @Override
    public Object fetchAll(Pageable pageable) {
        log.info("Fetching documents");
        if (pageable != null) {
            return documentRepository.findAll(pageable);
        }
        return documentRepository.findAll();
    }

    @Override
    public Object getById(String id) {
        log.info("Fetching document by id: {}", id);
        return documentRepository.findById(id).orElse(null);
    }

    @Override
    public Object update(Document obj) {
        log.info("Updating document: {}", obj.getId());
        obj.setModifiedDateTime(new Date());
        return documentRepository.save(obj);
    }

    @Override
    public Object delete(String id) {
        log.info("Deleting document: {}", id);
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc != null) {
            doc.setDeleteFlag(true);
            doc.setDeletedOn(new Date());
            documentRepository.save(doc);
            return true;
        }
        return false;
    }

    @Override
    public Object getDocument(String filename) {
        log.info("Fetching document by filename: {}", filename);
        return documentRepository.findByFilename(filename);
    }
}
