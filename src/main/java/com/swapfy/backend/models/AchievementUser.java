package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "achievements_users")
@Data
public class AchievementUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_user_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "date_achieved", nullable = false)
    private Instant unlockedAt;

    @PrePersist
    protected void onCreate() {
        this.unlockedAt = Instant.now();
    }
}
