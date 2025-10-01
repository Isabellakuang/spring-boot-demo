package com.java.demo.controller;

import com.java.demo.dto.ConversationView;
import com.java.demo.dto.CreateConversationRequest;
import com.java.demo.dto.CreateConversationResponse;
import com.java.demo.dto.MessageRequest;
import com.java.demo.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation Management", description = "APIs for managing customer conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create conversation", description = "Create a new customer conversation with initial message")
    public CreateConversationResponse create(@Valid @RequestBody CreateConversationRequest request) {
        return conversationService.createConversation(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conversation", description = "Retrieve a conversation by ID with all messages")
    public ConversationView get(@PathVariable Long id) {
        return conversationService.getConversation(id);
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Add message", description = "Append a new message to an existing conversation")
    public ConversationView append(@PathVariable Long id, @Valid @RequestBody MessageRequest request) {
        return conversationService.appendMessage(id, request);
    }
}
