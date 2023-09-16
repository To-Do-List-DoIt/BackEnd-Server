package com.choi.doit.domain.friend.domain;

import com.choi.doit.domain.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@Entity(name = "Friend")
@Table(name = "Friend")
public class FriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private UserEntity friendUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public FriendEntity(UserEntity user, UserEntity friendUser) {
        this.user = user;
        this.friendUser = friendUser;
    }
}
