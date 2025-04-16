package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByTagIdIn(List<Long> tagIds);

    Tag findByName(String name);
}
