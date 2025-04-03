package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Ver todas las valoraciones recibidas por un usuario
    List<Rating> findByRated_UserId(Long userId);

    // Ver todas las valoraciones hechas por un usuario
    List<Rating> findByRater_UserId(Long userId);
}
