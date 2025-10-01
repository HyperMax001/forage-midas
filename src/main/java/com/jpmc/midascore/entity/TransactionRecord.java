package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "transaction_records",
        indexes = {
                @Index(name = "idx+tx_sender", columnList = "sender_id"),
                @Index(name = "idx_tx_recipient", columnList = "recipient_id"),
                @Index(name = "idx_tx_created_at", columnList = "created_at")

        }
)
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private Float amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, Float amount){
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
    public Float getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}
