package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id1", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id2", nullable = false)
    private User user2;

    @ManyToOne
    @JoinColumn(name = "item_id1", nullable = false)
    private Item item1;

    @ManyToOne
    @JoinColumn(name = "item_id2", nullable = false)
    private Item item2;

    @Column(nullable = false)
    private Integer creditsTransferred;

    @Column(nullable = false)
    private String status; // 'Completed' o 'Cancelled'

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private Instant transactionDate;

    // Constructor vacío (necesario para JPA)
    public Transaction() {
        this.transactionDate = Instant.now(); // Fecha de transacción automática
    }
}
