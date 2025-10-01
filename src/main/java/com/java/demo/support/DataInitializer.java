package com.java.demo.support;

import com.java.demo.model.Conversation;
import com.java.demo.model.ConversationMessage;
import com.java.demo.model.ConversationStatus;
import com.java.demo.model.FaqEntry;
import com.java.demo.repository.ConversationRepository;
import com.java.demo.repository.FaqRepository;
import com.java.demo.service.FaqService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class DataInitializer {

    private final FaqRepository faqRepository;
    private final ConversationRepository conversationRepository;
    private final FaqService faqService;

    public DataInitializer(FaqRepository faqRepository,
                           ConversationRepository conversationRepository,
                           FaqService faqService) {
        this.faqRepository = faqRepository;
        this.conversationRepository = conversationRepository;
        this.faqService = faqService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (faqRepository.count() == 0) {
            FaqEntry booking = new FaqEntry(
                "How can I change my flight booking?",
                "You can change your booking via the Manage Booking section. Fees depend on your ticket type.",
                new HashSet<>(Set.of("flight", "booking", "change", "manage booking"))
            );
            FaqEntry luggage = new FaqEntry(
                "What is the baggage allowance for international flights?",
                "Most long-haul flights allow two checked bags up to 23kg each. Please verify on your itinerary.",
                new HashSet<>(Set.of("baggage", "allowance", "international", "luggage"))
            );
            FaqEntry refund = new FaqEntry(
                "How do I request a refund?",
                "Refund requests can be submitted through the Refund Center. Processing typically takes 7-10 business days.",
                new HashSet<>(Set.of("refund", "payment", "cancel", "cancellation"))
            );
            faqRepository.saveAll(List.of(booking, luggage, refund));
        }

        if (conversationRepository.count() == 0) {
            Conversation conversation = new Conversation(
                "Flight change inquiry",
                "customer@example.com",
                LocalDateTime.now().minusDays(1),
                ConversationStatus.OPEN
            );
            ConversationMessage message1 = new ConversationMessage(
                "CUSTOMER",
                "Hello, how do I change my booking for next week?"
            );
            ConversationMessage message2 = new ConversationMessage(
                "SYSTEM",
                "Please visit the Manage Booking section and follow the steps to change your flight."
            );
            conversation.addMessage(message1);
            conversation.addMessage(message2);
            conversationRepository.save(conversation);
        }

        faqService.warmUpCacheAsync();
    }
}
