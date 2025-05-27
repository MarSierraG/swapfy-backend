package com.swapfy.backend.repositories;

import com.swapfy.backend.models.HiddenConversation;
import com.swapfy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HiddenConversationRepository extends JpaRepository<HiddenConversation, Long> {
    Optional<HiddenConversation> findByUserAndOtherUser(User user, User otherUser);
    void deleteByUserAndOtherUser(User user, User otherUser);
    boolean existsByUser_UserIdAndOtherUser_UserId(Long userId, Long otherUserId);
    boolean existsByUserAndOtherUser(User user, User otherUser);
}
