package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
@Data
public class Item {

    public Item() {
        // Constructor vacío para JPA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type; // 'offer' o 'demand'

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "item_tags",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @Column(nullable = false)
    private Integer creditValue;

    @Column(nullable = false)
    private String status = "Available";  // Usar 'status' en lugar de 'available'

    @Column(name = "publication_date", nullable = false, updatable = false)
    private LocalDateTime publicationDate;  // Usar publication_date en lugar de created_at


    @PrePersist
    protected void onCreate() {
        this.publicationDate = LocalDateTime.now();  // Asignamos la fecha a publicationDate
    }

}
