package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Achievement;
import com.swapfy.backend.models.AchievementUser;
import com.swapfy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementUserRepository extends JpaRepository<AchievementUser, Long> {
    List<AchievementUser> findByUser(User user);
    boolean existsByUserAndAchievement(User user, Achievement achievement);
}
