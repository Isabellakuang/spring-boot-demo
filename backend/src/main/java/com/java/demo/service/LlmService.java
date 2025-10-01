package com.java.demo.service;

import com.java.demo.config.LlmConfig;
import com.java.demo.dto.LlmRequest;
import com.java.demo.dto.LlmResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

    private final LlmConfig llmConfig;
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "llm-service", fallbackMethod = "generateFallback")
    @Retry(name = "llm-service")
    public LlmResponse generate(LlmRequest request) {
        String mlServiceUrl = llmConfig.getMlServiceUrl() + "/api/llm/generate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<LlmRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServiceUrl, entity, Map.class
            );
            
            Map<String, Object> body = response.getBody();
            
            return LlmResponse.builder()
                .success((Boolean) body.get("success"))
                .content((String) body.get("content"))
                .model((String) body.get("model"))
                .usage((Map<String, Object>) body.get("usage"))
                .generatedAt(LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            log.error("Error calling LLM service: {}", e.getMessage());
            throw new RuntimeException("LLM service error: " + e.getMessage());
        }
    }

    public LlmResponse generateWithFallback(LlmRequest request) {
        String mlServiceUrl = llmConfig.getMlServiceUrl() + "/api/llm/generate-with-fallback";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<LlmRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServiceUrl, entity, Map.class
            );
            
            Map<String, Object> body = response.getBody();
            
            return LlmResponse.builder()
                .success((Boolean) body.get("success"))
                .content((String) body.get("content"))
                .model((String) body.get("model"))
                .usage((Map<String, Object>) body.get("usage"))
                .generatedAt(LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            log.error("Error calling LLM fallback service: {}", e.getMessage());
            return generateFallback(request, e);
        }
    }

    public LlmResponse chat(String conversationId, LlmRequest request) {
        // 为对话添加上下文
        String contextualPrompt = enrichPromptWithContext(conversationId, request.getPrompt());
        request.setPrompt(contextualPrompt);
        
        return generate(request);
    }

    @Cacheable(value = "llm-models", unless = "#result == null")
    public Map<String, Object> getAvailableModels() {
        return Map.of(
            "openai", List.of(
                "gpt-4-turbo-preview",
                "gpt-4",
                "gpt-3.5-turbo"
            ),
            "anthropic", List.of(
                "claude-3-opus-20240229",
                "claude-3-sonnet-20240229",
                "claude-3-haiku-20240307"
            )
        );
    }

    public Map<String, Object> getUsageStats(String provider, String startDate, String endDate) {
        // 实际实现应该从数据库查询
        return Map.of(
            "provider", provider != null ? provider : "all",
            "period", Map.of("start", startDate, "end", endDate),
            "totalRequests", 1250,
            "totalTokens", 125000,
            "averageResponseTime", "2.5s",
            "errorRate", "0.5%"
        );
    }

    // 降级方法
    private LlmResponse generateFallback(LlmRequest request, Throwable t) {
        log.warn("LLM service fallback triggered: {}", t.getMessage());
        
        return LlmResponse.builder()
            .success(false)
            .content("抱歉，AI 服务暂时不可用，请稍后再试。")
            .model("fallback")
            .error(t.getMessage())
            .generatedAt(LocalDateTime.now())
            .build();
    }

    private String enrichPromptWithContext(String conversationId, String prompt) {
        // 从对话历史中获取上下文
        // 实际实现应该查询数据库
        return String.format("对话 ID: %s\n用户问题: %s", conversationId, prompt);
    }
}