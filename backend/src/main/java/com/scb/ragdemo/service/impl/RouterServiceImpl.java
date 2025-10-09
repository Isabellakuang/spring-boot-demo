package com.scb.ragdemo.service.impl;

import com.scb.ragdemo.model.enums.QueryMode;
import com.scb.ragdemo.service.RouterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Router Service Implementation
 * Intelligently determines whether to use NLP or RAG mode based on query content
 * 
 * Strategy: Default to RAG mode (search documents first), only use NLP for casual conversation
 */
@Slf4j
@Service
public class RouterServiceImpl implements RouterService {
    
    // NLP-only keywords (casual conversation, greetings, general questions that don't need documents)
    private static final Set<String> NLP_ONLY_KEYWORDS = new HashSet<>(Arrays.asList(
        // Greetings and casual conversation
        "hello", "hi", "hey", "good morning", "good afternoon", "good evening",
        "how are you", "what's up", "thanks", "thank you", "bye", "goodbye",
        
        // General questions that don't need documents
        "who are you", "what can you do", "help me", "introduce yourself",
        "your name", "what is your", "tell me about yourself",
        
        // Weather, time, news (general knowledge)
        "weather", "temperature", "forecast", "time now", "current time",
        "latest news", "today's news", "what happened",
        
        // Math and calculations (simple ones)
        "calculate", "compute",
        
        // Jokes and entertainment
        "tell me a joke", "funny", "story", "sing"
    ));
    
    // NLP-only query patterns (questions that clearly don't need documents)
    private static final List<Pattern> NLP_ONLY_PATTERNS = Arrays.asList(
        Pattern.compile("^(hi|hello|hey|good morning|good afternoon|good evening).*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*how are you.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*who are you.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*what('s| is) your name.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*tell me (a )?joke.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*what('s| is) the weather.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*what time is it.*", Pattern.CASE_INSENSITIVE)
    );
    
    @Override
    public QueryMode determineQueryMode(String question) {
        if (question == null || question.trim().isEmpty()) {
            log.warn("Empty query, defaulting to NLP mode");
            return QueryMode.NLP;
        }
        
        String normalizedQuestion = question.toLowerCase().trim();
        
        // Check if this is clearly a casual conversation that doesn't need documents
        boolean isNlpOnly = containsNlpOnlyKeywords(normalizedQuestion);
        
        // Check if matches NLP-only patterns
        boolean matchesNlpPattern = NLP_ONLY_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(normalizedQuestion).matches());
        
        // Default to RAG mode unless it's clearly a casual conversation
        QueryMode mode = (isNlpOnly || matchesNlpPattern) ? QueryMode.NLP : QueryMode.RAG;
        
        log.info("Query '{}' -> Mode: {} (Is NLP-only: {}, Matches NLP pattern: {})",
            question, mode, isNlpOnly, matchesNlpPattern);
        
        return mode;
    }
    
    @Override
    public boolean containsDocumentKeywords(String question) {
        // This method is deprecated in favor of the new strategy
        // Now we check for NLP-only keywords instead
        return !containsNlpOnlyKeywords(question);
    }
    
    /**
     * Check if query contains NLP-only keywords (casual conversation)
     */
    private boolean containsNlpOnlyKeywords(String question) {
        if (question == null || question.trim().isEmpty()) {
            return false;
        }
        
        String normalizedQuestion = question.toLowerCase();
        
        return NLP_ONLY_KEYWORDS.stream()
            .anyMatch(normalizedQuestion::contains);
    }
    
    @Override
    public Map<String, Object> getRoutingDecision(String question) {
        Map<String, Object> decision = new HashMap<>();
        
        if (question == null || question.trim().isEmpty()) {
            decision.put("mode", QueryMode.NLP);
            decision.put("confidence", 0.0);
            decision.put("reason", "Empty query");
            decision.put("matchedKeywords", Collections.emptyList());
            return decision;
        }
        
        String normalizedQuestion = question.toLowerCase().trim();
        
        // Collect matched NLP-only keywords
        List<String> matchedNlpKeywords = new ArrayList<>();
        for (String keyword : NLP_ONLY_KEYWORDS) {
            if (normalizedQuestion.contains(keyword.toLowerCase())) {
                matchedNlpKeywords.add(keyword);
            }
        }
        
        // Collect matched NLP-only patterns
        List<String> matchedNlpPatterns = new ArrayList<>();
        for (int i = 0; i < NLP_ONLY_PATTERNS.size(); i++) {
            if (NLP_ONLY_PATTERNS.get(i).matcher(normalizedQuestion).matches()) {
                matchedNlpPatterns.add("NLP-Pattern-" + (i + 1));
            }
        }
        
        // Calculate confidence score for NLP mode
        double nlpConfidence = calculateNlpConfidence(matchedNlpKeywords.size(), matchedNlpPatterns.size());
        
        // Determine mode: Use NLP only if confidence is high, otherwise default to RAG
        QueryMode mode = (nlpConfidence > 0.5) ? QueryMode.NLP : QueryMode.RAG;
        
        // Build decision result
        decision.put("mode", mode);
        decision.put("nlpConfidence", nlpConfidence);
        decision.put("ragConfidence", 1.0 - nlpConfidence);
        decision.put("matchedNlpKeywords", matchedNlpKeywords);
        decision.put("matchedNlpPatterns", matchedNlpPatterns);
        decision.put("nlpKeywordCount", matchedNlpKeywords.size());
        decision.put("nlpPatternCount", matchedNlpPatterns.size());
        
        String reason;
        if (mode == QueryMode.NLP) {
            reason = String.format("Casual conversation detected: %d NLP keywords and %d NLP patterns matched",
                matchedNlpKeywords.size(), matchedNlpPatterns.size());
        } else {
            reason = "Default to RAG mode for document search";
        }
        decision.put("reason", reason);
        
        log.debug("Routing decision: {}", decision);
        
        return decision;
    }
    
    /**
     * Calculate confidence score for NLP mode
     * 
     * @param nlpKeywordCount Number of matched NLP-only keywords
     * @param nlpPatternCount Number of matched NLP-only patterns
     * @return Confidence score (0.0 - 1.0)
     */
    private double calculateNlpConfidence(int nlpKeywordCount, int nlpPatternCount) {
        // Keyword score: each keyword contributes 0.3, max 0.6
        double keywordScore = Math.min(nlpKeywordCount * 0.3, 0.6);
        
        // Pattern score: each pattern contributes 0.4, max 0.8
        double patternScore = Math.min(nlpPatternCount * 0.4, 0.8);
        
        return Math.min(keywordScore + patternScore, 1.0);
    }
}
