package com.scb.ragdemo.service.impl;

import com.scb.ragdemo.exception.custom.PoeApiException;
import com.scb.ragdemo.service.PoeClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Poe API Client Service Implementation
 * Uses OpenAI-compatible interface to call Poe API
 * Endpoint: https://api.poe.com/v1
 */
@Slf4j
@Service
public class PoeClientServiceImpl implements PoeClientService {
    
    private final RestTemplate restTemplate;
    
    @Value("${app.poe.base-url:https://api.poe.com/v1}")
    private String poeApiUrl;
    
    @Value("${app.poe.api-key}")
    private String poeApiKey;
    
    @Value("${app.poe.model:Claude-3.5-Sonnet}")
    private String model;
    
    @Value("${app.poe.timeout:30000}")
    private int timeout;
    
    @Value("${app.poe.max-tokens:4000}")
    private int maxTokens;
    
    @Value("${app.poe.temperature:0.7}")
    private double temperature;
    
    public PoeClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String sendMessage(String message) {
        log.info("Sending simple message to Poe API, model: {}", model);
        
        try {
            // Build OpenAI-format message list
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            
            // Send request
            String response = sendChatCompletionRequest(messages);
            
            log.info("Successfully received Poe API response, length: {}", response.length());
            return response;
            
        } catch (Exception e) {
            log.error("Failed to call Poe API: {}", e.getMessage(), e);
            throw new PoeApiException("Failed to call Poe API: " + e.getMessage(), 500);
        }
    }
    
    @Override
    public String sendMessageWithContext(String question, String context) {
        log.info("Sending message with context to Poe API, question: {}, context length: {}", 
            question, context.length());
        
        try {
            // Build OpenAI-format message list, add system message to inject context
            List<Map<String, String>> messages = new ArrayList<>();
            
            // System message used to provide context information
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemPromptWithContext(context));
            messages.add(systemMessage);
            
            // User message
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.add(userMessage);
            
            // Send request
            String response = sendChatCompletionRequest(messages);
            
            log.info("Successfully received Poe API response, length: {}", response.length());
            return response;
            
        } catch (Exception e) {
            log.error("Failed to call Poe API: {}", e.getMessage(), e);
            throw new PoeApiException("Failed to call Poe API: " + e.getMessage(), 500);
        }
    }
    
    @Override
    public boolean testConnection() {
        log.info("Testing Poe API connectivity");
        
        try {
            String testMessage = "Hello";
            sendMessage(testMessage);
            log.info("Poe API connectivity test successful");
            return true;
            
        } catch (Exception e) {
            log.error("Poe API connectivity test failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("apiUrl", poeApiUrl);
        info.put("model", model);
        info.put("timeout", timeout);
        info.put("maxTokens", maxTokens);
        info.put("temperature", temperature);
        info.put("configured", poeApiKey != null && !poeApiKey.isEmpty());
        
        return info;
    }
    
    /**
     * Send Chat Completion request to Poe API (OpenAI-compatible interface)
     * 
     * @param messages Message list
     * @return API response content
     */
    private String sendChatCompletionRequest(List<Map<String, String>> messages) {
        try {
            // Build OpenAI-format request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);
            
            // Build request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + poeApiKey);
            
            // Build request entity
            HttpEntity<Map<String, Object>> requestEntity = 
                new HttpEntity<>(requestBody, headers);
            
            // Send POST request to /chat/completions endpoint
            String chatCompletionUrl = poeApiUrl + "/chat/completions";
            log.debug("Sending request to: {}", chatCompletionUrl);
            
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                chatCompletionUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
            
            // Check response status code
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw new PoeApiException(
                    "Poe API returned unexpected status code: " + responseEntity.getStatusCode(),
                    responseEntity.getStatusCode().value()
                );
            }
            
            // Parse OpenAI-format response
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody == null) {
                throw new PoeApiException("Poe API response body is empty", 500);
            }
            
            // Extract response content from response.choices[0].message.content
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = 
                (List<Map<String, Object>>) responseBody.get("choices");
            
            if (choices == null || choices.isEmpty()) {
                throw new PoeApiException("Poe API response does not contain choices", 500);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> message = 
                (Map<String, Object>) choices.get(0).get("message");
            
            if (message == null || !message.containsKey("content")) {
                throw new PoeApiException("Poe API response format error", 500);
            }
            
            return message.get("content").toString();
            
        } catch (HttpClientErrorException e) {
            log.error("Poe API client error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PoeApiException(
                "Poe API client error: " + e.getMessage(),
                e.getStatusCode().value()
            );
            
        } catch (HttpServerErrorException e) {
            log.error("Poe API server error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PoeApiException(
                "Poe API server error: " + e.getMessage(),
                e.getStatusCode().value()
            );
            
        } catch (PoeApiException e) {
            throw e;
            
        } catch (Exception e) {
            log.error("Unexpected error calling Poe API: {}", e.getMessage(), e);
            throw new PoeApiException("Failed to call Poe API: " + e.getMessage(), 500);
        }
    }
    
    /**
     * Build system prompt with context information
     * 
     * @param context Document context
     * @return System prompt
     */
    private String buildSystemPromptWithContext(String context) {
        return String.format(
            "You are a helpful AI assistant. Please answer user questions based on the following document content. " +
            "Please follow these guidelines:\n" +
            "1. Answer based on the provided document content, do not fabricate information\n" +
            "2. If the document does not contain relevant information, clearly state that you cannot answer\n" +
            "3. Keep answers concise and clear, avoid overly lengthy responses\n" +
            "4. If appropriate, you may cite specific content from the document\n" +
            "Document content:\n%s",
            context
        );
    }
}
