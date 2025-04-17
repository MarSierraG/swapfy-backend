package com.swapfy.backend.services;

import com.swapfy.backend.models.Rating;
import com.swapfy.backend.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    public Rating createRating(Rating rating) {
        // Validar que el score esté entre 1 y 5
        int score = rating.getScore();
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsGivenByUser(Long userId) {
        return ratingRepository.findByRater_UserId(userId);
    }

    public List<Rating> getRatingsReceivedByUser(Long userId) {
        return ratingRepository.findByRated_UserId(userId);
    }
}