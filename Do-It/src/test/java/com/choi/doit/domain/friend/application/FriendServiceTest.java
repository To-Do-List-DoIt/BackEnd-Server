package com.choi.doit.domain.friend.application;

import com.choi.doit.domain.friend.dao.FriendRepository;
import com.choi.doit.domain.friend.dao.FriendRequestRepository;
import com.choi.doit.domain.friend.domain.FriendEntity;
import com.choi.doit.domain.friend.domain.FriendRequestEntity;
import com.choi.doit.domain.friend.dto.FriendItemDto;
import com.choi.doit.domain.friend.dto.FriendListDto;
import com.choi.doit.domain.friend.dto.FriendRequestDto;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.domain.user.domain.UserEntity;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Friend API Test")
@Transactional
@Slf4j
@SpringBootTest
class FriendServiceTest {
    private final FriendService friendService;
    final UserRepository userRepository;
    final FriendRequestRepository friendRequestRepository;
    final FriendRepository friendRepository;
    UserEntity user;
    final String email = "abc@abc.com";
    final String password = "password1234";
    UserEntity friend1;
    UserEntity friend2;
    final String friend1Email = "friend1@abc.com";
    final String friend2Email = "friend2@abc.com";

    @Autowired
    FriendServiceTest(FriendService friendService, UserRepository userRepository, FriendRequestRepository friendRequestRepository, FriendRepository friendRepository) {
        this.friendService = friendService;
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendRepository = friendRepository;
    }

    @BeforeEach
    void addMockData() {
        // user
        user = userRepository.save(new EmailJoinRequestDto(email, password, null).toEntity(null));

        // friends
        friend1 = userRepository.save(new EmailJoinRequestDto(friend1Email, password, null).toEntity(null));
        friend2 = userRepository.save(new EmailJoinRequestDto(friend2Email, password, null).toEntity(null));
    }

    @DisplayName("친구 신청")
    @WithMockUser(username = email)
    @Test
    void requestFriend() {
        // given
        FriendRequestDto friendRequestDto = new FriendRequestDto(friend1Email);

        // when
        friendService.requestFriend(friendRequestDto);

        // then
        FriendRequestEntity friendRequestEntity = friendRequestRepository.findByUserAndTargetUser(user, friend1).orElse(null);
        assertThat(friendRequestEntity).isNotNull();
    }

    @DisplayName("친구 수락")
    @WithMockUser(username = email)
    @Test
    void acceptFriend() {
        // given
        friendRequestRepository.save(new FriendRequestEntity(friend1, user));
        FriendRequestDto friendRequestDto = new FriendRequestDto(friend1Email);

        // when
        friendService.acceptFriend(friendRequestDto);

        // then
        assertThat(friendRequestRepository.findByUserAndTargetUser(friend1, user).orElse(null)).isNull();
        assertThat(friendRepository.findAllByUser(user).get(0).getEmail()).isEqualTo(friend1Email);
        assertThat(friendRepository.findAllByUser(friend1).get(0).getEmail()).isEqualTo(email);
    }

    @DisplayName("친구 신청 송신 리스트 조회")
    @WithMockUser(username = email)
    @Test
    void getSentRequestList() {
        // given
        friendRequestRepository.save(new FriendRequestEntity(user, friend1));

        // when
        FriendListDto friendListDto = friendService.getSentRequestList();
        ArrayList<FriendItemDto> list = friendListDto.getData();

        // then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getEmail()).isEqualTo(friend1Email);
    }

    @DisplayName("친구 신청 수신 리스트 조회")
    @WithMockUser(username = email)
    @Test
    void getReceivedRequestList() {
        // given
        friendRequestRepository.save(new FriendRequestEntity(friend1, user));

        // when
        FriendListDto friendListDto = friendService.getReceivedRequestList();
        ArrayList<FriendItemDto> list = friendListDto.getData();

        // then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getEmail()).isEqualTo(friend1Email);
    }

    @DisplayName("친구 리스트 조회")
    @WithMockUser(username = email)
    @Test
    void getList() {
        // given
        friendRepository.save(new FriendEntity(user, friend1));

        // when
        FriendListDto friendListDto = friendService.getList();
        ArrayList<FriendItemDto> list = friendListDto.getData();

        // then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getEmail()).isEqualTo(friend1Email);
    }
}