package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.DCompletionFileInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompletionFilesRepository extends MongoRepository<DCompletionFileInfo, String> {

    DCompletionFileInfo findByFileNameAndWellIdAndDeleteFlag(String fileName, String wellId, Boolean deleteFlag);

    List<DCompletionFileInfo> findByWellIdAndDeleteFlag(String wellId, Boolean deleteFlag);
}
