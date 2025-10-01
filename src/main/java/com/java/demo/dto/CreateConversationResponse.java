package com.java.demo.dto;

public class CreateConversationResponse {

    private final Long conversationId;

    public CreateConversationResponse(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getConversationId() {
        return conversationId;
    }
}
