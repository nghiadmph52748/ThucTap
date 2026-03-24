package org.example.thuctapproject.repository;

import org.example.thuctapproject.entity.UserRoleEntity;
import org.example.thuctapproject.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
}
