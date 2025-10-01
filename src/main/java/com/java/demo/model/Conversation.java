package com.java.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_customer_email", columnList = "customer_email"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_started_at", columnList = "started_at")
})
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<ConversationMessage> messages = new ArrayList<>();

    public Conversation() {
    }

    public Conversation(String subject, String customerEmail, LocalDateTime startedAt, ConversationStatus status) {
        this.subject = subject;
        this.customerEmail = customerEmail;
        this.startedAt = startedAt;
        this.status = status;
    }

    public void addMessage(ConversationMessage message) {
        messages.add(message);
        message.setConversation(this);
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public List<ConversationMessage> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
