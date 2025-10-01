package com.java.demo.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTests {

    @Test
    void shouldCreateConversationView() {
        // Given
        Long id = 1L;
        String subject = "Test Subject";
        String status = "OPEN";
        LocalDateTime startedAt = LocalDateTime.now();
        MessageView message = new MessageView("CUSTOMER", "Hello", LocalDateTime.now());
        List<MessageView> messages = List.of(message);

        // When
        ConversationView view = new ConversationView(id, subject, status, startedAt, messages);

        // Then
        assertThat(view.getConversationId()).isEqualTo(id);
        assertThat(view.getSubject()).isEqualTo(subject);
        assertThat(view.getStatus()).isEqualTo(status);
        assertThat(view.getStartedAt()).isEqualTo(startedAt);
        assertThat(view.getMessages()).hasSize(1);
        assertThat(view.getMessages().get(0)).isEqualTo(message);
    }

    @Test
    void shouldCreateCreateConversationRequest() {
        // Given
        String subject = "Support Request";
        String email = "test@example.com";
        String message = "I need help";

        // When
        CreateConversationRequest request = new CreateConversationRequest(subject, email, message);

        // Then
        assertThat(request.getSubject()).isEqualTo(subject);
        assertThat(request.getCustomerEmail()).isEqualTo(email);
        assertThat(request.getInitialMessage()).isEqualTo(message);
    }

    @Test
    void shouldUpdateCreateConversationRequest() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest("Old", "old@test.com", "Old message");

        // When
        request.setSubject("New Subject");
        request.setCustomerEmail("new@test.com");
        request.setInitialMessage("New message");

        // Then
        assertThat(request.getSubject()).isEqualTo("New Subject");
        assertThat(request.getCustomerEmail()).isEqualTo("new@test.com");
        assertThat(request.getInitialMessage()).isEqualTo("New message");
    }

    @Test
    void shouldCreateCreateConversationResponse() {
        // Given
        Long conversationId = 123L;

        // When
        CreateConversationResponse response = new CreateConversationResponse(conversationId);

        // Then
        assertThat(response.getConversationId()).isEqualTo(conversationId);
    }

    @Test
    void shouldCreateFaqDto() {
        // Given
        Long id = 1L;
        String question = "How to reset password?";
        String answer = "Click forgot password";
        Set<String> tags = new HashSet<>(Set.of("password", "reset"));

        // When
        FaqDto dto = new FaqDto(id, question, answer, tags);

        // Then
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getQuestion()).isEqualTo(question);
        assertThat(dto.getAnswer()).isEqualTo(answer);
        assertThat(dto.getTags()).containsExactlyInAnyOrder("password", "reset");
    }

    @Test
    void shouldCreateMessageRequest() {
        // Given
        String sender = "AGENT";
        String content = "How can I help?";

        // When
        MessageRequest request = new MessageRequest(sender, content);

        // Then
        assertThat(request.getSender()).isEqualTo(sender);
        assertThat(request.getContent()).isEqualTo(content);
    }

    @Test
    void shouldUpdateMessageRequest() {
        // Given
        MessageRequest request = new MessageRequest("OLD", "Old content");

        // When
        request.setSender("NEW");
        request.setContent("New content");

        // Then
        assertThat(request.getSender()).isEqualTo("NEW");
        assertThat(request.getContent()).isEqualTo("New content");
    }

    @Test
    void shouldCreateMessageView() {
        // Given
        String sender = "CUSTOMER";
        String content = "Hello";
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        MessageView view = new MessageView(sender, content, timestamp);

        // Then
        assertThat(view.getSender()).isEqualTo(sender);
        assertThat(view.getContent()).isEqualTo(content);
        assertThat(view.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldHandleEmptyMessagesList() {
        // Given
        List<MessageView> emptyMessages = List.of();

        // When
        ConversationView view = new ConversationView(
            1L, 
            "Test", 
            "OPEN", 
            LocalDateTime.now(), 
            emptyMessages
        );

        // Then
        assertThat(view.getMessages()).isEmpty();
    }

    @Test
    void shouldHandleMultipleMessages() {
        // Given
        MessageView msg1 = new MessageView("CUSTOMER", "First", LocalDateTime.now());
        MessageView msg2 = new MessageView("AGENT", "Second", LocalDateTime.now());
        List<MessageView> messages = List.of(msg1, msg2);

        // When
        ConversationView view = new ConversationView(
            1L, 
            "Test", 
            "OPEN", 
            LocalDateTime.now(), 
            messages
        );

        // Then
        assertThat(view.getMessages()).hasSize(2);
        assertThat(view.getMessages()).containsExactly(msg1, msg2);
    }
}
