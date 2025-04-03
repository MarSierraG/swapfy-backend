package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tags")
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Constructor vacío (necesario para JPA)
    public Tag() {
    }

    // Constructor con parámetro para facilitar la creación de tags con nombre
    public Tag(String name) {
        this.name = name;
    }
}
