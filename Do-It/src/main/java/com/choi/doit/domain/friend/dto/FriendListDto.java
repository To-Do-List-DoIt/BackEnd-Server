package com.choi.doit.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class FriendListDto {
    private ArrayList<FriendItemDto> data;
}
