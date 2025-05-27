package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "ratings")
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @ManyToOne
    @JoinColumn(name = "rater_user_id", nullable = false)
    private User rater;

    @ManyToOne
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User rated;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 300)
    private String comment;

    @Column(name = "date", nullable = false, updatable = false)
    private Instant ratedAt;

    @PrePersist
    protected void onCreate() {
        this.ratedAt = Instant.now();
    }
}
