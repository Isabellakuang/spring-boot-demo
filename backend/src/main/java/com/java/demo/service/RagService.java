package com.java.demo.service;

import com.java.demo.dto.DocumentIndexRequest;
import com.java.demo.dto.RagQueryRequest;
import com.java.demo.dto.RagQueryResponse;
import com.java.demo.config.LlmConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final LlmConfig llmConfig;
    private final RestTemplate restTemplate;
    private final ConversationService conversationService;
    private final FaqService faqService;

    @CircuitBreaker(name = "rag-service", fallbackMethod = "queryFallback")
    public RagQueryResponse query(RagQueryRequest request) {
        String mlServiceUrl = llmConfig.getMlServiceUrl() + "/api/rag/query";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<RagQueryRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServiceUrl, entity, Map.class
            );
            
            Map<String, Object> body = response.getBody();
            
            return RagQueryResponse.builder()
                .answer((String) body.get("answer"))
                .sources((List<Map<String, Object>>) body.get("sources"))
                .confidence(((Number) body.get("confidence")).doubleValue())
                .usage((Map<String, Object>) body.get("usage"))
                .queriedAt(LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            log.error("Error calling RAG service: {}", e.getMessage());
            return queryFallback(request, e);
        }
    }

    public Map<String, Object> indexDocument(DocumentIndexRequest request) {
        String mlServiceUrl = llmConfig.getMlServiceUrl() + "/api/rag/index";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<DocumentIndexRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServiceUrl, entity, Map.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error indexing document: {}", e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    public Map<String, Object> batchIndex(List<DocumentIndexRequest> requests) {
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (DocumentIndexRequest request : requests) {
            try {
                Map<String, Object> result = indexDocument(request);
                if (result.get("indexed_chunks") != null) {
                    successCount++;
                } else {
                    failureCount++;
                    errors.add("Failed to index: " + request.getDocId());
                }
            } catch (Exception e) {
                failureCount++;
                errors.add("Error indexing " + request.getDocId() + ": " + e.getMessage());
            }
        }
        
        return Map.of(
            "total", requests.size(),
            "success", successCount,
            "failure", failureCount,
            "errors", errors
        );
    }

    public Map<String, Object> deleteDocument(String docId) {
        // 实际实现应该调用 ML Service 删除向量
        log.info("Deleting document from vector store: {}", docId);
        
        return Map.of(
            "success", true,
            "docId", docId,
            "deletedAt", LocalDateTime.now()
        );
    }

    public Map<String, Object> getStats() {
        // 实际实现应该查询实际统计
        return Map.of(
            "totalDocuments", 1500,
            "totalVectors", 15000,
            "averageChunksPerDoc", 10,
            "totalQueries", 5000,
            "averageConfidence", 0.85,
            "lastIndexedAt", LocalDateTime.now()
        );
    }

    public Map<String, Object> refreshIndex() {
        log.info("Starting full RAG index refresh");
        
        try {
            // 1. 索引所有对话
            var conversations = conversationService.findAll();
            List<DocumentIndexRequest> convRequests = conversations.stream()
                .map(conv -> DocumentIndexRequest.builder()
                    .document(String.format(
                        "Conversation: %s\nCustomer: %s\nStatus: %s",
                        conv.getSubject(),
                        conv.getCustomerEmail(),
                        conv.getStatus()
                    ))
                    .metadata(Map.of(
                        "type", "conversation",
                        "id", conv.getId(),
                        "customer_email", conv.getCustomerEmail()
                    ))
                    .docId("conv_" + conv.getId())
                    .build()
                )
                .collect(Collectors.toList());
            
            Map<String, Object> convResult = batchIndex(convRequests);
            
            // 2. 索引所有 FAQ
            var faqs = faqService.findAll();
            List<DocumentIndexRequest> faqRequests = faqs.stream()
                .map(faq -> DocumentIndexRequest.builder()
                    .document(String.format(
                        "Question: %s\nAnswer: %s\nCategory: %s",
                        faq.getQuestion(),
                        faq.getAnswer(),
                        faq.getCategory()
                    ))
                    .metadata(Map.of(
                        "type", "faq",
                        "id", faq.getId(),
                        "category", faq.getCategory()
                    ))
                    .docId("faq_" + faq.getId())
                    .build()
                )
                .collect(Collectors.toList());
            
            Map<String, Object> faqResult = batchIndex(faqRequests);
            
            return Map.of(
                "success", true,
                "conversations", convResult,
                "faqs", faqResult,
                "refreshedAt", LocalDateTime.now()
            );
            
        } catch (Exception e) {
            log.error("Error refreshing RAG index: {}", e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    // 降级方法
    private RagQueryResponse queryFallback(RagQueryRequest request, Throwable t) {
        log.warn("RAG service fallback triggered: {}", t.getMessage());
        
        // 降级到简单的关键词搜索
        var faqs = faqService.searchByKeyword(request.getQuestion());
        
        String fallbackAnswer = faqs.isEmpty() 
            ? "抱歉，我暂时无法回答您的问题，请稍后再试。"
            : "根据关键词搜索，我找到了以下相关信息：\n" + 
              faqs.get(0).getAnswer();
        
        return RagQueryResponse.builder()
            .answer(fallbackAnswer)
            .sources(List.of())
            .confidence(0.3)
            .usage(Map.of())
            .queriedAt(LocalDateTime.now())
            .build();
    }
}