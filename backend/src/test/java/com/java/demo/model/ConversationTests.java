package com.java.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationTests {

    @Test
    void shouldCreateConversationWithConstructor() {
        // Given
        String subject = "Test Subject";
        String email = "test@example.com";
        LocalDateTime startedAt = LocalDateTime.now();
        ConversationStatus status = ConversationStatus.OPEN;

        // When
        Conversation conversation = new Conversation(subject, email, startedAt, status);

        // Then
        assertThat(conversation.getSubject()).isEqualTo(subject);
        assertThat(conversation.getCustomerEmail()).isEqualTo(email);
        assertThat(conversation.getStartedAt()).isEqualTo(startedAt);
        assertThat(conversation.getStatus()).isEqualTo(status);
        assertThat(conversation.getMessages()).isEmpty();
    }

    @Test
    void shouldAddMessageToConversation() {
        // Given
        Conversation conversation = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );
        ConversationMessage message = new ConversationMessage("CUSTOMER", "Hello");

        // When
        conversation.addMessage(message);

        // Then
        assertThat(conversation.getMessages()).hasSize(1);
        assertThat(conversation.getMessages().get(0)).isEqualTo(message);
        assertThat(message.getConversation()).isEqualTo(conversation);
    }

    @Test
    void shouldAddMultipleMessages() {
        // Given
        Conversation conversation = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );
        ConversationMessage message1 = new ConversationMessage("CUSTOMER", "Hello");
        ConversationMessage message2 = new ConversationMessage("AGENT", "Hi there");

        // When
        conversation.addMessage(message1);
        conversation.addMessage(message2);

        // Then
        assertThat(conversation.getMessages()).hasSize(2);
        assertThat(conversation.getMessages()).containsExactly(message1, message2);
    }

    @Test
    void shouldUpdateStatus() {
        // Given
        Conversation conversation = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );

        // When
        conversation.setStatus(ConversationStatus.CLOSED);

        // Then
        assertThat(conversation.getStatus()).isEqualTo(ConversationStatus.CLOSED);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        // Given - entities without id are equal if both ids are null
        Conversation conversation1 = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );
        Conversation conversation2 = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );

        // Then - same reference is equal
        assertThat(conversation1).isEqualTo(conversation1);
        // Both have null id, so they are equal based on id comparison
        assertThat(conversation1).isEqualTo(conversation2);
        assertThat(conversation1).isNotEqualTo(null);
        assertThat(conversation1).isNotEqualTo("string");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        // Given
        Conversation conversation = new Conversation(
            "Test", 
            "test@example.com", 
            LocalDateTime.now(), 
            ConversationStatus.OPEN
        );

        // When
        int hashCode1 = conversation.hashCode();
        int hashCode2 = conversation.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }
}
