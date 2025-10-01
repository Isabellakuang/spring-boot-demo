package com.java.demo.service;

import com.java.demo.dto.FaqDto;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class TextSimilarity {

    private TextSimilarity() {
    }

    public static double score(String query, FaqDto faq) {
        Set<String> queryTokens = tokenize(query);
        double tagMatches = faq.getTags().stream()
            .map(tag -> tag.toLowerCase(Locale.ROOT))
            .filter(queryTokens::contains)
            .count();

        double questionMatches = countMatches(faq.getQuestion(), queryTokens);
        double answerMatches = countMatches(faq.getAnswer(), queryTokens) * 0.5;

        return (tagMatches * 2.5) + questionMatches + answerMatches;
    }

    private static double countMatches(String source, Set<String> targets) {
        Set<String> sourceTokens = tokenize(source);
        long matches = sourceTokens.stream()
            .filter(targets::contains)
            .count();
        return matches;
    }

    private static Set<String> tokenize(String input) {
        if (input == null || input.isBlank()) {
            return Set.of();
        }
        String[] tokens = input.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9\\s]", " ")
            .split("\\s+");

        return new HashSet<>(Arrays.asList(tokens));
    }
}
