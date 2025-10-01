package com.java.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.dto.CreateConversationRequest;
import com.java.demo.dto.MessageRequest;
import com.java.demo.model.ConversationStatus;
import com.java.demo.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ConversationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversationRepository conversationRepository;

    @BeforeEach
    void setUp() {
        conversationRepository.deleteAll();
    }

    @Test
    void shouldCreateConversation() throws Exception {
        CreateConversationRequest request = new CreateConversationRequest(
            "Technical Support",
            "customer@example.com",
            "I need help with my account"
        );

        mockMvc.perform(post("/api/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conversationId").isNumber());
    }

    @Test
    void shouldGetConversationById() throws Exception {
        // Create a conversation first
        CreateConversationRequest request = new CreateConversationRequest(
            "Billing Question",
            "user@test.com",
            "Question about my bill"
        );

        String createResponse = mockMvc.perform(post("/api/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long conversationId = objectMapper.readTree(createResponse).get("conversationId").asLong();

        // Get the conversation
        mockMvc.perform(get("/api/conversations/" + conversationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(conversationId))
                .andExpect(jsonPath("$.subject").value("Billing Question"))
                .andExpect(jsonPath("$.customerEmail").value("user@test.com"))
                .andExpect(jsonPath("$.status").value(ConversationStatus.OPEN.toString()))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void shouldAddMessageToConversation() throws Exception {
        // Create a conversation
        CreateConversationRequest createRequest = new CreateConversationRequest(
            "Support Request",
            "customer@example.com",
            "Initial message"
        );

        String createResponse = mockMvc.perform(post("/api/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long conversationId = objectMapper.readTree(createResponse).get("conversationId").asLong();

        // Add a message
        MessageRequest messageRequest = new MessageRequest("AGENT", "Thank you for contacting us");

        mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.messages[*].content", hasItem("Thank you for contacting us")));
    }

    @Test
    void shouldReturnNotFoundForInvalidConversationId() throws Exception {
        mockMvc.perform(get("/api/conversations/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateCreateConversationRequest() throws Exception {
        CreateConversationRequest invalidRequest = new CreateConversationRequest(
            "",  // Empty subject
            "invalid-email",  // Invalid email
            ""   // Empty message
        );

        mockMvc.perform(post("/api/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
