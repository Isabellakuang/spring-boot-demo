package com.java.demo.controller;

import com.java.demo.model.FaqEntry;
import com.java.demo.repository.FaqRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class KnowledgeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FaqRepository faqRepository;

    @BeforeEach
    void setUp() {
        faqRepository.deleteAll();
        
        // Add test FAQs
        FaqEntry faq1 = new FaqEntry(
            "How to reset password?",
            "Click on 'Forgot password' on the login page and follow the instructions.",
            new HashSet<>(Set.of("password", "reset", "login"))
        );
        
        FaqEntry faq2 = new FaqEntry(
            "How to update billing information?",
            "Go to Settings > Billing and update your payment method.",
            new HashSet<>(Set.of("billing", "payment", "update"))
        );
        
        FaqEntry faq3 = new FaqEntry(
            "How to contact support?",
            "You can reach our support team at support@example.com or call 1-800-SUPPORT.",
            new HashSet<>(Set.of("support", "contact", "help"))
        );
        
        faqRepository.save(faq1);
        faqRepository.save(faq2);
        faqRepository.save(faq3);
    }

    @Test
    void shouldGetAllFaqs() throws Exception {
        mockMvc.perform(get("/api/knowledge/faq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].question", hasItem(containsString("password"))))
                .andExpect(jsonPath("$[*].question", hasItem(containsString("billing"))))
                .andExpect(jsonPath("$[*].question", hasItem(containsString("support"))));
    }

    @Test
    void shouldGetFaqById() throws Exception {
        FaqEntry faq = faqRepository.findAll().get(0);
        
        mockMvc.perform(get("/api/knowledge/faq/" + faq.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faq.getId()))
                .andExpect(jsonPath("$.question").value(faq.getQuestion()))
                .andExpect(jsonPath("$.answer").value(faq.getAnswer()));
    }

    @Test
    void shouldSearchFaqsByQuery() throws Exception {
        mockMvc.perform(get("/api/knowledge/faq/search")
                .param("query", "I forgot my password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", containsString("password")));
    }

    @Test
    void shouldReturnEmptyForUnrelatedQuery() throws Exception {
        mockMvc.perform(get("/api/knowledge/faq/search")
                .param("query", "completely unrelated topic xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldReturnNotFoundForInvalidFaqId() throws Exception {
        mockMvc.perform(get("/api/knowledge/faq/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleMissingQueryParameter() throws Exception {
        mockMvc.perform(get("/api/knowledge/faq/search"))
                .andExpect(status().isBadRequest());
    }
}
