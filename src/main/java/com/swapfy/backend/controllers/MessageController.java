package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.MessageRequestDTO;
import com.swapfy.backend.dto.MessageResponseDTO;
import com.swapfy.backend.models.Message;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.MessageService;

import com.swapfy.backend.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    private SecurityService securityService;


    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Optional<Message> message = messageService.getMessageById(id);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequestDTO dto) {
        if (dto.content() == null || dto.content().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El contenido del mensaje no puede estar vacío");
        }

        if (dto.senderUserId().equals(dto.receiverUserId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "No puedes enviarte mensajes a ti mism@."));
        }

        Message saved = messageService.sendMessage(
                dto.senderUserId(),
                dto.receiverUserId(),
                dto.content()
        );

        MessageResponseDTO response = new MessageResponseDTO(
                saved.getMessageId(),
                saved.getSender().getUserId(),
                saved.getReceiver().getUserId(),
                saved.getContent(),
                saved.getSentAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sender/{senderId}")
    public List<MessageResponseDTO> getMessagesBySender(@PathVariable Long senderId) {
        return messageService.getMessagesBySender(senderId)
                .stream()
                .map(msg -> new MessageResponseDTO(
                        msg.getMessageId(),
                        msg.getSender().getUserId(),
                        msg.getReceiver().getUserId(),
                        msg.getContent(),
                        msg.getSentAt()
                ))
                .toList();
    }

    @GetMapping("/receiver/{receiverId}")
    public List<MessageResponseDTO> getMessagesByReceiver(@PathVariable Long receiverId) {
        return messageService.getMessagesByReceiver(receiverId)
                .stream()
                .map(msg -> new MessageResponseDTO(
                        msg.getMessageId(),
                        msg.getSender().getUserId(),
                        msg.getReceiver().getUserId(),
                        msg.getContent(),
                        msg.getSentAt()
                ))
                .toList();
    }

    @GetMapping("/conversation")
    public List<MessageResponseDTO> getConversation(@RequestParam Long user1, @RequestParam Long user2) {
        return messageService.getConversation(user1, user2)
                .stream()
                .map(msg -> new MessageResponseDTO(
                        msg.getMessageId(),
                        msg.getSender().getUserId(),
                        msg.getReceiver().getUserId(),
                        msg.getContent(),
                        msg.getSentAt()
                ))
                .toList();
    }

    @PutMapping("/mark-as-read")
    public ResponseEntity<Map<String, String>> markMessagesAsRead(@RequestParam Long senderId, @RequestParam Long receiverId) {
        messageService.markMessagesAsRead(senderId, receiverId);
        return ResponseEntity.ok(Map.of("message", "Mensajes marcados como leídos."));
    }


    @GetMapping("/unread-summary/{receiverId}")
    public ResponseEntity<Map<Long, Long>> getUnreadSummary(@PathVariable Long receiverId) {
        Map<Long, Long> summary = messageService.getUnreadMessageSummary(receiverId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/unique-users/{userId}")
    public ResponseEntity<Integer> getUniqueConversations(@PathVariable Long userId) {
        int count = messageService.countUniqueConversationUsers(userId);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/conversation")
    public ResponseEntity<?> deleteConversation(
            @RequestParam Long user1,
            @RequestParam Long user2
    ) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(authUser.getRole().getName());
        boolean isParticipant = authUser.getUserId().equals(user1) || authUser.getUserId().equals(user2);

        if (!isAdmin && !isParticipant) {
            return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para borrar esta conversación"));
        }

        messageService.deleteConversation(user1, user2);
        return ResponseEntity.ok(Map.of("message", "Conversación eliminada correctamente"));
    }


    @GetMapping("/visible-conversations")
    public ResponseEntity<List<Map<String, Object>>> getVisibleConversationUsers() {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }

        List<User> users = messageService.getVisibleConversationUsers(authUser.getUserId());

        List<Map<String, Object>> response = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", u.getUserId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/visible-conversations/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getVisibleConversationUsersAsAdmin(@PathVariable Long userId) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null || !"ADMIN".equalsIgnoreCase(authUser.getRole().getName())) {
            return ResponseEntity.status(403).body(null);
        }

        List<User> users = messageService.getAllConversationUsers(userId);


        List<Map<String, Object>> response = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", u.getUserId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }


}
