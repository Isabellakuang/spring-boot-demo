package com.java.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConversationView {

    private final Long conversationId;
    private final String subject;
    private final String status;
    private final LocalDateTime startedAt;
    private final List<MessageView> messages;

    public ConversationView(Long conversationId,
                            String subject,
                            String status,
                            LocalDateTime startedAt,
                            List<MessageView> messages) {
        this.conversationId = conversationId;
        this.subject = subject;
        this.status = status;
        this.startedAt = startedAt;
        this.messages = messages;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getSubject() {
        return subject;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public List<MessageView> getMessages() {
        return messages;
    }
}
