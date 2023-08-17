package com.choi.doit.domain.model;

import com.choi.doit.domain.user.domain.Provider;
import com.choi.doit.domain.user.domain.Role;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "User")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String nickname;
    private String password;
    @Column(columnDefinition = "TEXT")
    private String profile_image_path;
    @CreationTimestamp
    private LocalDateTime created_at;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Provider provider;

    public UserEntity(String email, String password) {
        role = Role.GUEST;
        this.email = email;
        this.password = password;
    }

    public UserEntity(EmailJoinRequestDto emailJoinRequestDto, String profile_image_path) {
        role = Role.MEMBER;
        this.email = emailJoinRequestDto.getEmail();
        this.nickname = emailJoinRequestDto.getNickname();
        this.password = emailJoinRequestDto.getPassword();
        this.profile_image_path = profile_image_path;
    }

    // For oauth2 user
    @Builder
    public UserEntity(Provider provider, String email, String profile_image_path) {
        role = Role.MEMBER;
        this.provider = provider;
        this.email = email;
        this.profile_image_path = profile_image_path;
    }
}
