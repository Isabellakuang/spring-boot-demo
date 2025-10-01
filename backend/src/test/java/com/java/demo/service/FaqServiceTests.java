package com.java.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.java.demo.dto.FaqDto;
import com.java.demo.model.FaqEntry;
import com.java.demo.repository.FaqRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FaqServiceTests {

    @Autowired
    private FaqService faqService;

    @Autowired
    private FaqRepository faqRepository;

    @BeforeEach
    void setUp() {
        faqRepository.deleteAll();
        FaqEntry entry = new FaqEntry(
            "How to reset password?",
            "Click on 'Forgot password' on the login page and follow the instructions.",
            new HashSet<>(Set.of("password", "reset", "login"))
        );
        faqRepository.save(entry);
    }

    @Test
    void shouldReturnFaqForMatchingQuery() {
        Optional<FaqDto> result = faqService.searchByQuery("I need to reset my login password");
        assertThat(result).isPresent();
        assertThat(result.get().getQuestion()).contains("reset password");
    }

    @Test
    void shouldReturnEmptyForUnrelatedQuery() {
        Optional<FaqDto> result = faqService.searchByQuery("How do I upgrade my seat?");
        assertThat(result).isEmpty();
    }
}