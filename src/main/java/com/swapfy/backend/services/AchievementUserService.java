package com.swapfy.backend.services;

import com.swapfy.backend.models.AchievementUser;
import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.AchievementUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementUserService {

    private final AchievementUserRepository achievementUserRepository;

    @Autowired
    public AchievementUserService(AchievementUserRepository achievementUserRepository) {
        this.achievementUserRepository = achievementUserRepository;
    }

    public List<AchievementUser> getAchievementsByUser(User user) {
        return achievementUserRepository.findByUser(user);
    }

    public AchievementUser unlockAchievement(AchievementUser achievementUser) {
        return achievementUserRepository.save(achievementUser);
    }
}
