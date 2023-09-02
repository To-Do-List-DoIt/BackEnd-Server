package com.choi.doit.domain.mypage.dto.response;

import com.choi.doit.domain.mypage.dto.UnfinishedTodoListItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@AllArgsConstructor
@Getter
@Setter
public class ReadUnfinishedTodoListResponse {
    private LinkedList<UnfinishedTodoListItemDto> list;
}
