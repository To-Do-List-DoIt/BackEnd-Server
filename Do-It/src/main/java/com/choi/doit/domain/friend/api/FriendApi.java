package com.choi.doit.domain.friend.api;

import com.choi.doit.domain.friend.application.FriendService;
import com.choi.doit.domain.friend.dto.FriendListDto;
import com.choi.doit.domain.friend.dto.FriendRequestDto;
import com.choi.doit.domain.friend.dto.FriendResponseDto;
import com.choi.doit.global.common.response.DataResponseDto;
import com.choi.doit.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendApi {
    private final FriendService friendService;

    // 친구 신청
    @PostMapping("/request")
    public ResponseEntity<ResponseDto> requestFriend(@RequestBody @Valid FriendRequestDto friendRequestDto) {
        FriendResponseDto friendResponseDto = friendService.requestFriend(friendRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(friendResponseDto, 201));
    }

    // 친구 수락
    @PostMapping
    public ResponseEntity<ResponseDto> acceptFriend(@RequestBody @Valid FriendRequestDto friendRequestDto) {
        FriendResponseDto friendResponseDto = friendService.acceptFriend(friendRequestDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(friendResponseDto, 201));
    }

    // 친구 신청 송신 내역 조회
    @GetMapping("/sent-request")
    public ResponseEntity<ResponseDto> getSentRequestList() {
        FriendListDto friendListDto = friendService.getSentRequestList();

        return ResponseEntity.ok(DataResponseDto.of(friendListDto, 200));
    }

    // 친구 신청 수신 내역 조회
    @GetMapping("/received-request")
    public ResponseEntity<ResponseDto> getReceivedRequestList() {
        FriendListDto friendListDto = friendService.getReceivedRequestList();

        return ResponseEntity.ok(DataResponseDto.of(friendListDto, 200));
    }

    // 친구 내역 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getList() {
        FriendListDto friendListDto = friendService.getList();

        return ResponseEntity.ok(DataResponseDto.of(friendListDto, 200));
    }
}
