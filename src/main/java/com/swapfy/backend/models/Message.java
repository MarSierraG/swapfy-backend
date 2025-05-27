package com.swapfy.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @NotBlank(message = "El contenido del mensaje no puede estar vacío")
    @Column(nullable = false, length = 500)
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = Instant.now(); // Hora UTC actual
    }

    @Column(name = "is_read")
    private boolean isRead = false;
}
