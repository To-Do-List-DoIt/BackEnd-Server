package com.choi.doit.domain.friend.dao;

import com.choi.doit.domain.friend.domain.FriendEntity;
import com.choi.doit.domain.friend.dto.FriendItemDto;
import com.choi.doit.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    @Query("select new com.choi.doit.domain.friend.dto.FriendItemDto(f.friendUser) " +
            "from Friend f " +
            "where f.user = :user")
    ArrayList<FriendItemDto> findAllByUser(UserEntity user);

    Boolean existsByUserAndFriendUser(UserEntity user, UserEntity friendUser);

    void deleteAllByUser(UserEntity user);
}
