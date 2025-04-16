package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "achievements")
@Data
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Long achievementId;

    @Column(name = "title", nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
}
