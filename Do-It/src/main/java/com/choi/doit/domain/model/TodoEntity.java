package com.choi.doit.domain.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity(name = "Todo")
@Table(name = "Todo")
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @Column(length = 50)
    private String content;
    @ManyToOne
    private CategoryEntity category;
    private LocalDate date;
    private LocalTime time;
    private Boolean checkStatus;
    @CreationTimestamp
    private LocalDateTime created_at;
    @UpdateTimestamp
    private LocalDateTime updated_at;

    public TodoEntity(UserEntity user, String content, CategoryEntity category, LocalDate date, LocalTime time) {
        this.user = user;
        this.content = content;
        this.category = category;
        this.date = date;
        this.time = time;
        checkStatus = false;
    }

    @Transactional
    public boolean updateIsChecked() {
        this.checkStatus = !checkStatus;

        return checkStatus;
    }

    @Transactional
    public void update(String content, CategoryEntity category, LocalDate date, LocalTime time) {
        this.content = content;
        this.category = category;
        this.date = date;
        this.time = time;
    }
}
