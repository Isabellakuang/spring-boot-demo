package com.java.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationMessageTests {

    @Test
    void shouldCreateMessageWithConstructor() {
        // Given
        String sender = "CUSTOMER";
        String content = "Hello, I need help";

        // When
        ConversationMessage message = new ConversationMessage(sender, content);

        // Then
        assertThat(message.getSender()).isEqualTo(sender);
        assertThat(message.getContent()).isEqualTo(content);
        assertThat(message.getCreatedAt()).isNull();
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        // Given - entities without id are equal if both ids are null
        ConversationMessage message1 = new ConversationMessage("CUSTOMER", "Hello");
        ConversationMessage message2 = new ConversationMessage("CUSTOMER", "Hello");

        // Then - same reference is equal
        assertThat(message1).isEqualTo(message1);
        // Both have null id, so they are equal based on id comparison
        assertThat(message1).isEqualTo(message2);
        assertThat(message1).isNotEqualTo(null);
        assertThat(message1).isNotEqualTo("string");
    }

    @Test
    void shouldNotOverrideExistingCreatedAt() {
        // Given
        LocalDateTime existingTime = LocalDateTime.now().minusHours(1);
        ConversationMessage message = new ConversationMessage("CUSTOMER", "Test");
        message.setCreatedAt(existingTime);

        // When
        message.onCreate();

        // Then
        assertThat(message.getCreatedAt()).isEqualTo(existingTime);
    }

    @Test
    void shouldSetConversation() {
        // Given
        ConversationMessage message = new ConversationMessage("CUSTOMER", "Test");
        Conversation conversation = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );

        // When
        message.setConversation(conversation);

        // Then
        assertThat(message.getConversation()).isEqualTo(conversation);
    }

    @Test
    void shouldUpdateSenderAndContent() {
        // Given
        ConversationMessage message = new ConversationMessage("CUSTOMER", "Original");

        // When
        message.setSender("AGENT");
        message.setContent("Updated content");

        // Then
        assertThat(message.getSender()).isEqualTo("AGENT");
        assertThat(message.getContent()).isEqualTo("Updated content");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        // Given
        ConversationMessage message = new ConversationMessage("CUSTOMER", "Test");

        // When
        int hashCode1 = message.hashCode();
        int hashCode2 = message.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }
}
