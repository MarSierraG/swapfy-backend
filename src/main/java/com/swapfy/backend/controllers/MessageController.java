package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.MessageRequestDTO;
import com.swapfy.backend.dto.MessageResponseDTO;
import com.swapfy.backend.models.Message;
import com.swapfy.backend.services.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

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



}
