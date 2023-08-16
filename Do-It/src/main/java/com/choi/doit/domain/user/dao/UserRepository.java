package com.choi.doit.domain.user.dao;

import com.choi.doit.domain.model.UserEntity;
import com.choi.doit.domain.user.domain.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserEntity u SET u.role = :role WHERE u.id = :id")
    void updateRole(@Param("role") Role role, @Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserEntity u SET u.email = :email WHERE u.id = :id")
    void updateEmail(@Param("email") String email, @Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserEntity u SET u.nickname = :nickname WHERE u.id = :id")
    void updateNickname(@Param("nickname") String nickname, @Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserEntity u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE UserEntity u SET u.profile_image_path = :imagePath WHERE u.id = :id")
    void updateProfileImagePath(@Param("imagePath") String imagePath, @Param("id") Long id);
}
