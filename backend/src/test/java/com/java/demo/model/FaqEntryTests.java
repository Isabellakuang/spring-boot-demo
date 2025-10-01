package com.java.demo.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FaqEntryTests {

    @Test
    void shouldCreateFaqEntryWithConstructor() {
        // Given
        String question = "How to reset password?";
        String answer = "Click on forgot password link";
        Set<String> tags = new HashSet<>(Set.of("password", "reset", "login"));

        // When
        FaqEntry faqEntry = new FaqEntry(question, answer, tags);

        // Then
        assertThat(faqEntry.getQuestion()).isEqualTo(question);
        assertThat(faqEntry.getAnswer()).isEqualTo(answer);
        assertThat(faqEntry.getTags()).containsExactlyInAnyOrder("password", "reset", "login");
    }

    @Test
    void shouldCreateFaqEntryWithEmptyTags() {
        // Given
        String question = "Test question";
        String answer = "Test answer";
        Set<String> tags = new HashSet<>();

        // When
        FaqEntry faqEntry = new FaqEntry(question, answer, tags);

        // Then
        assertThat(faqEntry.getTags()).isEmpty();
    }

    @Test
    void shouldUpdateQuestion() {
        // Given
        FaqEntry faqEntry = new FaqEntry("Old question", "Answer", new HashSet<>());

        // When
        faqEntry.setQuestion("New question");

        // Then
        assertThat(faqEntry.getQuestion()).isEqualTo("New question");
    }

    @Test
    void shouldUpdateAnswer() {
        // Given
        FaqEntry faqEntry = new FaqEntry("Question", "Old answer", new HashSet<>());

        // When
        faqEntry.setAnswer("New answer");

        // Then
        assertThat(faqEntry.getAnswer()).isEqualTo("New answer");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        // Given - entities without id are equal if both ids are null
        FaqEntry faqEntry1 = new FaqEntry("Question", "Answer", new HashSet<>());
        FaqEntry faqEntry2 = new FaqEntry("Question", "Answer", new HashSet<>());

        // Then - same reference is equal
        assertThat(faqEntry1).isEqualTo(faqEntry1);
        // Both have null id, so they are equal based on id comparison
        assertThat(faqEntry1).isEqualTo(faqEntry2);
        assertThat(faqEntry1).isNotEqualTo(null);
        assertThat(faqEntry1).isNotEqualTo("string");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        // Given
        FaqEntry faqEntry = new FaqEntry("Question", "Answer", new HashSet<>());

        // When
        int hashCode1 = faqEntry.hashCode();
        int hashCode2 = faqEntry.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void shouldHandleMultipleTags() {
        // Given
        Set<String> tags = new HashSet<>(Set.of("tag1", "tag2", "tag3", "tag4"));
        FaqEntry faqEntry = new FaqEntry("Question", "Answer", tags);

        // Then
        assertThat(faqEntry.getTags()).hasSize(4);
        assertThat(faqEntry.getTags()).containsExactlyInAnyOrder("tag1", "tag2", "tag3", "tag4");
    }
}
