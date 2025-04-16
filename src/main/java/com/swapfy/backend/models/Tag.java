package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

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

    // Relación con Item (bidireccional)
    @ManyToMany(mappedBy = "tags")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Item> items;

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(Long tagId) {
        this.tagId = tagId;
    }
}
