package org.example.thuctapproject.repository;

import org.example.thuctapproject.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    List<TaskEntity> findAllByProject_Id(Integer projectId);

    List<TaskEntity> findAllByAssignee_Id(Integer assigneeId);
}
