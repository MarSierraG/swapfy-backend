package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Rating;
import com.swapfy.backend.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable Long id) {
        Optional<Rating> rating = ratingService.getRatingById(id);
        return rating.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Rating createRating(@RequestBody Rating rating) {
        return ratingService.createRating(rating);
    }

    @GetMapping("/given/{userId}")
    public List<Rating> getRatingsGivenByUser(@PathVariable Long userId) {
        return ratingService.getRatingsGivenByUser(userId);
    }

    @GetMapping("/received/{userId}")
    public List<Rating> getRatingsReceivedByUser(@PathVariable Long userId) {
        return ratingService.getRatingsReceivedByUser(userId);
    }
}
