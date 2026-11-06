package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.GlobalFileUploads;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface GlobalFileUploadsRepository extends MongoRepository<GlobalFileUploads, String> {

    GlobalFileUploads findByGlobalFileUploadsIdAndDeleteFlag(String id, Boolean deleteFlag);

    GlobalFileUploads findByIdAndDeleteFlag(String id, Boolean deleteFlag);

    Page<GlobalFileUploads> findByCategoryInAndDeleteFlagAndOperationDateTimeBetweenOrderByIndexAsc(
            Collection<String> categories, Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    Page<GlobalFileUploads> findByCategoryInAndDeleteFlagOrderByIndexAsc(
            Collection<String> categories, Boolean deleteFlag, Pageable pageable);

    Page<GlobalFileUploads> findByCategoryAndDeleteFlagAndOperationDateTimeBetweenOrderByIndexAsc(
            String category, Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    Page<GlobalFileUploads> findByCategoryAndDeleteFlagOrderByIndexAsc(
            String category, Boolean deleteFlag, Pageable pageable);

    Page<GlobalFileUploads> findByCategoryAndDeleteFlagAndOperationDateTimeBetweenOrderByModifiedDateTimeDesc(
            String category, Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    Page<GlobalFileUploads> findByCategoryAndDeleteFlagOrderByModifiedDateTimeDesc(
            String category, Boolean deleteFlag, Pageable pageable);

    Page<GlobalFileUploads> findByDeleteFlagAndOperationDateTimeBetweenOrderByIndexAsc(
            Boolean deleteFlag, Date startDate, Date endDate, Pageable pageable);

    Page<GlobalFileUploads> findByDeleteFlagOrderByIndexAsc(
            String category, Boolean deleteFlag, Pageable pageable);

    List<GlobalFileUploads> findByDeleteFlag(Boolean deleteFlag);
}