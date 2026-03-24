package org.example.thuctapproject.repository;

import org.example.thuctapproject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("select ur.roleEntity.name from UserRoleEntity ur where ur.user.id = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Integer userId);
}
