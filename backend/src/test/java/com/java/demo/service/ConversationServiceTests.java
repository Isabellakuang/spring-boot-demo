package com.java.demo.service;

import com.java.demo.dto.CreateConversationRequest;
import com.java.demo.dto.CreateConversationResponse;
import com.java.demo.dto.MessageRequest;
import com.java.demo.exception.ResourceNotFoundException;
import com.java.demo.model.Conversation;
import com.java.demo.model.ConversationStatus;
import com.java.demo.repository.ConversationMessageRepository;
import com.java.demo.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ConversationServiceTests {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        conversationRepository.deleteAll();
    }

    @Test
    void shouldCreateConversation() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest(
            "Technical Support",
            "customer@example.com",
            "I need help with my account"
        );

        // When
        CreateConversationResponse response = conversationService.createConversation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getConversationId()).isNotNull();

        Conversation conversation = conversationRepository.findById(response.getConversationId()).orElseThrow();
        assertThat(conversation.getSubject()).isEqualTo("Technical Support");
        assertThat(conversation.getCustomerEmail()).isEqualTo("customer@example.com");
        assertThat(conversation.getStatus()).isEqualTo(ConversationStatus.OPEN);
        assertThat(conversation.getMessages()).hasSize(1);
        assertThat(conversation.getMessages().get(0).getContent()).isEqualTo("I need help with my account");
    }

    @Test
    void shouldGetConversationById() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest(
            "Billing Question",
            "user@test.com",
            "Question about my bill"
        );
        CreateConversationResponse created = conversationService.createConversation(request);

        // When
        var conversationView = conversationService.getConversation(created.getConversationId());

        // Then
        assertThat(conversationView).isNotNull();
        assertThat(conversationView.getConversationId()).isEqualTo(created.getConversationId());
        assertThat(conversationView.getSubject()).isEqualTo("Billing Question");
        assertThat(conversationView.getStatus()).isEqualTo("OPEN");
        assertThat(conversationView.getMessages()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenConversationNotFound() {
        // When/Then
        assertThatThrownBy(() -> conversationService.getConversation(99999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Conversation")
            .hasMessageContaining("not found");
    }

    @Test
    void shouldAddMessageToConversation() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest(
            "Support Request",
            "customer@example.com",
            "Initial message"
        );
        CreateConversationResponse created = conversationService.createConversation(request);

        MessageRequest messageRequest = new MessageRequest("AGENT", "Thank you for contacting us");

        // When
        var updatedConversation = conversationService.appendMessage(created.getConversationId(), messageRequest);

        // Then
        assertThat(updatedConversation.getMessages()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(updatedConversation.getMessages())
            .extracting(msg -> msg.getContent())
            .contains("Thank you for contacting us");
    }

    @Test
    void shouldThrowExceptionWhenAddingMessageToNonExistentConversation() {
        // Given
        MessageRequest messageRequest = new MessageRequest("AGENT", "Test message");

        // When/Then
        assertThatThrownBy(() -> conversationService.appendMessage(99999L, messageRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Conversation")
            .hasMessageContaining("not found");
    }

    @Test
    void shouldMaintainMessageOrder() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest(
            "Test Conversation",
            "test@example.com",
            "First message"
        );
        CreateConversationResponse created = conversationService.createConversation(request);

        // When
        conversationService.appendMessage(created.getConversationId(), new MessageRequest("AGENT", "Second message"));
        conversationService.appendMessage(created.getConversationId(), new MessageRequest("CUSTOMER", "Third message"));

        var conversation = conversationService.getConversation(created.getConversationId());

        // Then
        assertThat(conversation.getMessages()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(conversation.getMessages().get(0).getContent()).isEqualTo("First message");
    }

    @Test
    void shouldSetTimestampsOnMessages() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest(
            "Test",
            "test@example.com",
            "Test message"
        );

        // When
        CreateConversationResponse created = conversationService.createConversation(request);
        var conversation = conversationService.getConversation(created.getConversationId());

        // Then
        assertThat(conversation.getMessages().get(0).getTimestamp()).isNotNull();
        assertThat(conversation.getStartedAt()).isNotNull();
    }
}
