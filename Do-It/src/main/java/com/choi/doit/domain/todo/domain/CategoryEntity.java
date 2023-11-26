package com.choi.doit.domain.todo.domain;

import com.choi.doit.domain.user.domain.UserEntity;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "Category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @Column(length = 50)
    private String name;
    private String color;
    private Boolean isPrivate;

    public CategoryEntity(UserEntity user, String name, String color) {
        this.user = user;
        this.name = name;
        this.color = color;
        this.isPrivate = false;
    }

    public CategoryEntity(UserEntity user, String name, String color, Boolean isPrivate) {
        this.user = user;
        this.name = name;
        this.color = color;
        this.isPrivate = isPrivate;
    }

    @Transactional
    public void update(String name, String color, Boolean isPrivate) {
        this.name = name;
        this.color = color;
        this.isPrivate = isPrivate;
    }
}
