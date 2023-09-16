package com.choi.doit.domain.friend.application;

import com.choi.doit.domain.friend.dao.FriendRepository;
import com.choi.doit.domain.friend.dao.FriendRequestRepository;
import com.choi.doit.domain.friend.domain.FriendEntity;
import com.choi.doit.domain.friend.domain.FriendRequestEntity;
import com.choi.doit.domain.friend.dto.FriendItemDto;
import com.choi.doit.domain.friend.dto.FriendListDto;
import com.choi.doit.domain.friend.dto.FriendRequestDto;
import com.choi.doit.domain.friend.dto.FriendResponseDto;
import com.choi.doit.domain.friend.exception.FriendErrorCode;
import com.choi.doit.domain.model.Role;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.global.error.exception.RestApiException;
import com.choi.doit.global.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final SecurityContextUtil securityContextUtil;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;

    // 친구 신청
    @Transactional
    public FriendResponseDto requestFriend(FriendRequestDto friendRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        String targetEmail = friendRequestDto.getEmail();

        // 게스트 회원 여부 검사
        if (user.getRole().equals(Role.GUEST))
            throw new RestApiException(FriendErrorCode.GUEST_ACCESS_FORBIDDEN);

        // 이메일 유효성 검사
        UserEntity targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new RestApiException(FriendErrorCode.TARGET_EMAIL_NOT_FOUND));

        // 타겟 이메일 게스트 여부 검사
        if (targetUser.getRole().equals(Role.GUEST))
            throw new RestApiException(FriendErrorCode.TARGET_FORBIDDEN);

        // 친구 신청 데이터 생성
        FriendRequestEntity friendRequestEntity = new FriendRequestEntity(user, targetUser);
        friendRequestRepository.save(friendRequestEntity);

        return new FriendResponseDto(targetEmail);
    }

    // 친구 수락
    @Transactional
    public FriendResponseDto acceptFriend(FriendRequestDto friendRequestDto) throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();
        String targetEmail = friendRequestDto.getEmail();

        // 게스트 회원 여부 검사
        if (user.getRole().equals(Role.GUEST))
            throw new RestApiException(FriendErrorCode.GUEST_ACCESS_FORBIDDEN);

        // 이메일 유효성 검사
        UserEntity targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new RestApiException(FriendErrorCode.TARGET_EMAIL_NOT_FOUND));

        // 친구 신청 여부 확인
        FriendRequestEntity friendRequestEntity = friendRequestRepository.findByUserAndTargetUser(targetUser, user)
                .orElseThrow(() -> new RestApiException(FriendErrorCode.REQUEST_NOT_FOUND));

        // 친구 요청 데이터 삭제
        friendRequestRepository.delete(friendRequestEntity);

        // 친구 데이터 저장
        ArrayList<FriendEntity> friendEntities = new ArrayList<>(Arrays.asList(new FriendEntity(user, targetUser), new FriendEntity(targetUser, user)));
        friendRepository.saveAll(friendEntities);

        return new FriendResponseDto(targetEmail);
    }

    // 친구 신청 송신 내역 조회
    public FriendListDto getSentRequestList() throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        // 게스트 회원 여부 검사
        if (user.getRole().equals(Role.GUEST))
            throw new RestApiException(FriendErrorCode.GUEST_ACCESS_FORBIDDEN);

        // 데이터 조회
        ArrayList<FriendItemDto> friendRequestList = friendRequestRepository.findAllByUser(user);

        return new FriendListDto(friendRequestList);
    }

    // 친구 신청 수신 내역 조회
    public FriendListDto getReceivedRequestList() throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        // 게스트 회원 여부 검사
        if (user.getRole().equals(Role.GUEST))
            throw new RestApiException(FriendErrorCode.GUEST_ACCESS_FORBIDDEN);

        // 데이터 조회
        ArrayList<FriendItemDto> friendRequestList = friendRequestRepository.findAllByTargetUser(user);

        return new FriendListDto(friendRequestList);
    }

    // 친구 내역 조회
    public FriendListDto getList() throws RestApiException {
        UserEntity user = securityContextUtil.getUserEntity();

        // 게스트 회원 여부 검사
        if (user.getRole().equals(Role.GUEST))
            throw new RestApiException(FriendErrorCode.GUEST_ACCESS_FORBIDDEN);

        // 데이터 조회
        ArrayList<FriendItemDto> friendList = friendRepository.findAllByUser(user);

        return new FriendListDto(friendList);
    }
}
