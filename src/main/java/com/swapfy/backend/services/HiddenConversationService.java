package com.swapfy.backend.services;

import com.swapfy.backend.models.HiddenConversation;
import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.HiddenConversationRepository;
import com.swapfy.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HiddenConversationService {

    @Autowired
    private HiddenConversationRepository hiddenRepo;

    @Autowired
    private UserRepository userRepo;

    public void hideConversation(Long userId, Long otherUserId) {
        User user = userRepo.findById(userId).orElseThrow();
        User other = userRepo.findById(otherUserId).orElseThrow();

        if (!hiddenRepo.existsByUserAndOtherUser(user, other)) {
            HiddenConversation hc = new HiddenConversation();
            hc.setUser(user);
            hc.setOtherUser(other);
            hiddenRepo.save(hc);
        }
    }

    public boolean isHidden(Long userId, Long otherUserId) {
        User user = userRepo.findById(userId).orElseThrow();
        User other = userRepo.findById(otherUserId).orElseThrow();
        return hiddenRepo.existsByUserAndOtherUser(user, other);
    }

    public void unhideConversation(Long userId, Long otherUserId) {
        User user = userRepo.findById(userId).orElseThrow();
        User other = userRepo.findById(otherUserId).orElseThrow();
        hiddenRepo.deleteByUserAndOtherUser(user, other);
    }
}
