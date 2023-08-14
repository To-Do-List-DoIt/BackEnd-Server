package com.choi.doit.domain.user.dao;

import com.choi.doit.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);
}
