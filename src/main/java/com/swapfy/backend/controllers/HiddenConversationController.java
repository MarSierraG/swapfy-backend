package com.swapfy.backend.controllers;

import com.swapfy.backend.services.HiddenConversationService;
import com.swapfy.backend.services.SecurityService;
import com.swapfy.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hidden-conversations")
public class HiddenConversationController {

    @Autowired
    private HiddenConversationService hiddenService;

    @Autowired
    private SecurityService securityService;

    @PostMapping("/hide/{otherUserId}")
    public ResponseEntity<?> hideConversation(@PathVariable Long otherUserId) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        hiddenService.hideConversation(authUser.getUserId(), otherUserId);
        return ResponseEntity.ok(Map.of("message", "Conversación ocultada"));

    }

    @DeleteMapping("/unhide/{otherUserId}")
    public ResponseEntity<?> unhideConversation(@PathVariable Long otherUserId) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        hiddenService.unhideConversation(authUser.getUserId(), otherUserId);
        return ResponseEntity.ok().body("Conversación mostrada de nuevo");
    }
}
