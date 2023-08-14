package com.choi.doit.domain.model;

import com.choi.doit.domain.user.dto.request.EmailJoinRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "User")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isGuest;
    private String email;
    private String nickname;
    private String password;
    private String provider;
    @Column(columnDefinition = "TEXT")
    private String profile_image_path;
    @CreationTimestamp
    private LocalDateTime created_at;

    public UserEntity() {
        isGuest = true;
    }

    public UserEntity(EmailJoinRequestDto emailJoinRequestDto, String profile_image_path) {
        isGuest = false;
        this.email = emailJoinRequestDto.getEmail();
        this.nickname = emailJoinRequestDto.getNickname();
        this.password = emailJoinRequestDto.getPassword();
        this.profile_image_path = profile_image_path;
    }
}
