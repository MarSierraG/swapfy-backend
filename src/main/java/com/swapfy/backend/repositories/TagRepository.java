package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Métodos adicionales si los necesitas, por ejemplo:
    Tag findByName(String name);
}
