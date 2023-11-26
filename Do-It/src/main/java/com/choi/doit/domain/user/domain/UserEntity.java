package com.choi.doit.domain.user.domain;

import com.choi.doit.domain.model.Provider;
import com.choi.doit.domain.model.Role;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "User")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Size(max = 100)
    private String nickname;

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
        this.nickname = email;
    }

    public UserEntity(EmailJoinRequestDto emailJoinRequestDto) {
        role = Role.MEMBER;
        this.email = emailJoinRequestDto.getEmail();
        this.password = emailJoinRequestDto.getPassword();
        this.nickname = emailJoinRequestDto.getNickname();
    }

    // For oauth2 user
    @Builder
    public UserEntity(Provider provider, String email, String password, String nickname) {
        role = Role.MEMBER;
        this.provider = provider;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserEntity userEntity)) return false;

        return Objects.equals(this.id, userEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Transactional
    public void updateRole(Role role) {
        this.role = role;
    }

    @Transactional
    public void updateEmail(String email) {
        this.email = email;
    }

    @Transactional
    public void updatePassword(String password) {
        this.password = password;
    }

    @Transactional
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    @Transactional
    public void updateProfileImage(String profile_image_path) {
        this.profile_image_path = profile_image_path;
    }
}
