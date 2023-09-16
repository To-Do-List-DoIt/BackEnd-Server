package com.choi.doit.domain.user.domain;

import com.choi.doit.domain.model.Provider;
import com.choi.doit.domain.model.Role;
import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
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
        this.password = emailJoinRequestDto.getPassword();
        this.profile_image_path = profile_image_path;
    }

    // For oauth2 user
    @Builder
    public UserEntity(Provider provider, String email, String password) {
        role = Role.MEMBER;
        this.provider = provider;
        this.email = email;
        this.password = password;
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
    public void updateEmail(String email) {
        this.email = email;
    }

    @Transactional
    public void updatePassword(String password) {
        this.password = password;
    }

    @Transactional
    public void updateProfileImage(String profile_image_path) {
        this.profile_image_path = profile_image_path;
    }
}
