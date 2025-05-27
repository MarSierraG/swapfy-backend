package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hidden_conversations", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "other_user_id"}))
@Data
public class HiddenConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "other_user_id")
    private User otherUser;
}
