package com.java.demo.service;

import com.java.demo.dto.ConversationView;
import com.java.demo.dto.CreateConversationRequest;
import com.java.demo.dto.CreateConversationResponse;
import com.java.demo.dto.MessageRequest;
import com.java.demo.dto.MessageView;
import com.java.demo.exception.ResourceNotFoundException;
import com.java.demo.model.Conversation;
import com.java.demo.model.ConversationMessage;
import com.java.demo.model.ConversationStatus;
import com.java.demo.repository.ConversationMessageRepository;
import com.java.demo.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;
    private final FaqService faqService;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationMessageRepository conversationMessageRepository,
                               FaqService faqService) {
        this.conversationRepository = conversationRepository;
        this.conversationMessageRepository = conversationMessageRepository;
        this.faqService = faqService;
    }

    public CreateConversationResponse createConversation(CreateConversationRequest request) {
        Conversation conversation = new Conversation(
            request.getSubject(),
            request.getSubject(),
            request.getCustomerEmail(),
            LocalDateTime.now(),
            ConversationStatus.OPEN
        );

        ConversationMessage initial = new ConversationMessage("CUSTOMER", request.getInitialMessage());
        conversation.addMessage(initial);

        Conversation saved = conversationRepository.save(conversation);

        generateAutomatedReply(saved, request.getInitialMessage());

        return new CreateConversationResponse(saved.getId());
    }

    public ConversationView getConversation(Long id) {
        Conversation conversation = conversationRepository.findWithMessagesById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation %d not found".formatted(id)));
        return toView(conversation);
    }

    public ConversationView appendMessage(Long conversationId, MessageRequest request) {
        Conversation conversation = conversationRepository.findWithMessagesById(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation %d not found".formatted(conversationId)));

        ConversationMessage message = new ConversationMessage(
            request.getSender().toUpperCase(Locale.ROOT),
            request.getContent()
        );
        conversation.addMessage(message);
        conversationRepository.save(conversation);

        generateAutomatedReply(conversation, request.getContent());

        return toView(conversation);
    }

    private void generateAutomatedReply(Conversation conversation, String userContent) {
        Optional.ofNullable(userContent)
            .filter(content -> !content.isBlank())
            .flatMap(faqService::searchByQuery)
            .ifPresent(faq -> {
                ConversationMessage botReply = new ConversationMessage(
                    "SYSTEM",
                    """
                    Based on your query, you might find this information useful:
                    Q: %s
                    A: %s
                    """.formatted(faq.getQuestion(), faq.getAnswer())
                );
                conversation.addMessage(botReply);
                conversationMessageRepository.save(botReply);
            });
    }

    public List<Conversation> findAll() {
        return conversationRepository.findAll();
    }

    private ConversationView toView(Conversation conversation) {
        List<MessageView> views = conversation.getMessages()
            .stream()
            .map(message -> new MessageView(message.getSender(), message.getContent(), message.getCreatedAt()))
            .toList();

        return new ConversationView(
            conversation.getId(),
            conversation.getSubject(),
            conversation.getStatus().name(),
            conversation.getStartedAt(),
            views
        );
    }
}
