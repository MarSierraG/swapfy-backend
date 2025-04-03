package com.swapfy.backend.controllers;

import com.swapfy.backend.models.AchievementUser;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.AchievementUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-achievements")
public class AchievementUserController {

    private final AchievementUserService achievementUserService;

    @Autowired
    public AchievementUserController(AchievementUserService achievementUserService) {
        this.achievementUserService = achievementUserService;
    }

    @PostMapping
    public AchievementUser unlockAchievement(@RequestBody AchievementUser achievementUser) {
        return achievementUserService.unlockAchievement(achievementUser);
    }

    @PostMapping("/user")
    public List<AchievementUser> getAchievementsByUser(@RequestBody User user) {
        return achievementUserService.getAchievementsByUser(user);
    }
}
