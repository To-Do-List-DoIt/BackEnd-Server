package com.choi.doit.domain.friend.dto;

import com.choi.doit.domain.user.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FriendItemDto {
    private String email;

    public FriendItemDto(UserEntity targetUser) {
        this.email = targetUser.getEmail();
    }
}
