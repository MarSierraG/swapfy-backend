package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Message;
import com.swapfy.backend.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/sender/{senderId}")
    public List<Message> getMessagesBySender(@PathVariable Long senderId) {
        return messageService.getMessagesBySender(senderId);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<Message> getMessagesByReceiver(@PathVariable Long receiverId) {
        return messageService.getMessagesByReceiver(receiverId);
    }

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long user1, @RequestParam Long user2) {
        return messageService.getConversation(user1, user2);
    }
}
