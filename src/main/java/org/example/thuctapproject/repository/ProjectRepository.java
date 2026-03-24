package org.example.thuctapproject.repository;

import org.example.thuctapproject.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {
    boolean existsByIdAndUsers_Id(Integer projectId, Integer userId);
}
