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
@Entity(name = "FriendRequest")
@Table(name = "FriendRequest")
public class FriendRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private UserEntity targetUser;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public FriendRequestEntity(UserEntity user, UserEntity targetUser) {
        this.user = user;
        this.targetUser = targetUser;
    }
}
