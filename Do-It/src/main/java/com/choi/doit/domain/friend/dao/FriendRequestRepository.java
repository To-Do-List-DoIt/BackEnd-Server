package com.choi.doit.domain.friend.dao;

import com.choi.doit.domain.friend.domain.FriendRequestEntity;
import com.choi.doit.domain.friend.dto.FriendItemDto;
import com.choi.doit.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {
    Optional<FriendRequestEntity> findByUserAndTargetUser(UserEntity user, UserEntity targetUser);

    @Query("select new com.choi.doit.domain.friend.dto.FriendItemDto(f.targetUser) " +
            "from FriendRequest f " +
            "where f.user = :user")
    ArrayList<FriendItemDto> findAllByUser(UserEntity user);

    @Query("select new com.choi.doit.domain.friend.dto.FriendItemDto(f.user) " +
            "from FriendRequest f " +
            "where f.targetUser = :targetUser")
    ArrayList<FriendItemDto> findAllByTargetUser(UserEntity targetUser);
}
