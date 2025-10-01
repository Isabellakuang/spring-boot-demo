package com.java.demo.service;

import com.java.demo.dto.FaqDto;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class TextSimilarityTests {

    @Test
    void shouldScoreExactTagMatch() {
        // Given
        String query = "password reset help";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Click on forgot password link",
            Set.of("password", "reset", "login")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should have high score due to tag matches (password, reset) and question matches
        assertThat(score).isGreaterThan(5.0);
    }

    @Test
    void shouldScoreQuestionMatch() {
        // Given
        String query = "how to reset my password";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Click on forgot password link",
            Set.of("account", "security")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should have score from question matches (how, to, reset, password)
        assertThat(score).isGreaterThan(3.0);
    }

    @Test
    void shouldScoreAnswerMatch() {
        // Given
        String query = "forgot password link";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Click on forgot password link on the login page",
            Set.of("account")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should have score from answer matches (forgot, password, link) with 0.5 weight
        assertThat(score).isGreaterThan(1.0);
    }

    @Test
    void shouldReturnZeroForNoMatch() {
        // Given
        String query = "billing information";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Click on forgot password link",
            Set.of("password", "reset", "login")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    void shouldBeCaseInsensitive() {
        // Given
        String query = "PASSWORD RESET";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Instructions here",
            Set.of("PASSWORD", "RESET")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should match despite different cases
        assertThat(score).isGreaterThan(5.0);
    }

    @Test
    void shouldHandleEmptyQuery() {
        // Given
        String query = "";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Instructions here",
            Set.of("password", "reset")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    void shouldHandleNullQuery() {
        // Given
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Instructions here",
            Set.of("password", "reset")
        );

        // When
        double score = TextSimilarity.score(null, faq);

        // Then
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    void shouldIgnorePunctuation() {
        // Given
        String query = "how-to-reset-password?!";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password",
            "Instructions",
            Set.of("password")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should match after removing punctuation
        assertThat(score).isGreaterThan(3.0);
    }

    @Test
    void shouldWeightTagMatchesHigher() {
        // Given
        String query = "password";
        FaqDto faqWithTag = new FaqDto(
            1L,
            "Some question",
            "Some answer",
            Set.of("password")
        );
        FaqDto faqWithoutTag = new FaqDto(
            2L,
            "password question",
            "Some answer",
            Set.of("other")
        );

        // When
        double scoreWithTag = TextSimilarity.score(query, faqWithTag);
        double scoreWithoutTag = TextSimilarity.score(query, faqWithoutTag);

        // Then
        // Tag match (2.5 weight) should score higher than question match (1.0 weight)
        assertThat(scoreWithTag).isGreaterThan(scoreWithoutTag);
    }

    @Test
    void shouldHandleMultipleWordQuery() {
        // Given
        String query = "I forgot my password and need to reset it";
        FaqDto faq = new FaqDto(
            1L,
            "How to reset password?",
            "Click on forgot password link",
            Set.of("password", "reset", "account")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should have high score from multiple matches
        assertThat(score).isGreaterThan(7.0);
    }

    @Test
    void shouldHandleSpecialCharactersInQuery() {
        // Given
        String query = "user@example.com password";
        FaqDto faq = new FaqDto(
            1L,
            "Password reset for email users",
            "Instructions",
            Set.of("password", "email")
        );

        // When
        double score = TextSimilarity.score(query, faq);

        // Then
        // Should match "password" after removing special characters
        assertThat(score).isGreaterThan(2.0);
    }
}
