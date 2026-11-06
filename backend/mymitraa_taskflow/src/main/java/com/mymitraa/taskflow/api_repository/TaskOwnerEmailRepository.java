package com.mymitraa.taskflow.api_repository;

import com.mymitraa.taskflow.api_domain.TaskOwnerEmail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskOwnerEmailRepository extends MongoRepository<TaskOwnerEmail, String> {

    TaskOwnerEmail findByTaskOwnerAndDeleteFlag(String taskOwner, Boolean deleteFlag);

    List<TaskOwnerEmail> findByTaskOwnerInAndDeleteFlag(Collection<String> taskOwners, Boolean deleteFlag);

    List<TaskOwnerEmail> findByDeleteFlag(Boolean deleteFlag);
}
