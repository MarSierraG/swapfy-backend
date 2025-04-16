package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Message;
import com.swapfy.backend.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
    public ResponseEntity<?> sendMessage(@Valid @RequestBody Message message, BindingResult result) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El contenido del mensaje no puede estar vacío");
        }
        if (result.hasErrors()) {
            System.out.println("Errores de validación: " + result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Message savedMessage = messageService.sendMessage(message);
        return ResponseEntity.ok(savedMessage);
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
