package com.choi.doit.domain.model;

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

    public UserEntity(String email, String nickname, String password, String profile_image_path) {
        isGuest = false;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profile_image_path = profile_image_path;
    }

    public UserEntity() {
        isGuest = true;
    }
}
