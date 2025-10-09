package com.scb.ragdemo.controller;

import com.scb.ragdemo.model.dto.request.QueryRequest;
import com.scb.ragdemo.model.dto.response.ApiResponse;
import com.scb.ragdemo.model.dto.response.QueryResponse;
import com.scb.ragdemo.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Query Processing Controller
 * Provides query API supporting intelligent Q&A based on NLP and RAG modes
 */
@Slf4j
@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
@Tag(name = "Intelligent Q&A", description = "Intelligent Q&A based on NLP and RAG modes")
public class QueryController {

    private final QueryService queryService;

    /**
     * Intelligent query interface
     * Automatically selects the appropriate mode based on query content
     */
    @PostMapping
    @Operation(summary = "Intelligent Q&A", description = "Intelligent query interface supporting automatic mode selection and intelligent Q&A based on NLP/RAG modes")
    public ApiResponse<QueryResponse> query(
            @Parameter(description = "Query request", required = true)
            @Valid @RequestBody QueryRequest request) {
        
        log.info("Received query request, question={}, mode={}", request.getQuestion(), request.getMode());
        
        QueryResponse response = queryService.processQuery(request);
        
        log.info("Query completed, mode={}, responseTime={}ms, fromCache={}", 
                response.getMode(), response.getResponseTime(), response.getFromCache());
        
        return ApiResponse.success(response);
    }

    /**
     * Pure NLP mode query
     */
    @PostMapping("/nlp")
    @Operation(summary = "NLP mode query", description = "Use pure NLP mode for query")
    public ApiResponse<QueryResponse> queryWithNlp(
            @Parameter(description = "Question content", required = true)
            @RequestParam String question) {
        
        log.info("NLP mode query, question={}", question);
        
        QueryResponse response = queryService.queryWithNlp(question);
        
        log.info("NLP query completed, responseTime={}ms", response.getResponseTime());
        
        return ApiResponse.success(response);
    }

    /**
     * Pure RAG mode query
     */
    @PostMapping("/rag")
    @Operation(summary = "RAG mode query", description = "Use RAG mode for query and return relevant document sources")
    public ApiResponse<QueryResponse> queryWithRag(
            @Parameter(description = "Question content", required = true)
            @RequestParam String question,
            @Parameter(description = "Maximum number of source documents to return")
            @RequestParam(defaultValue = "3") int maxSources) {
        
        log.info("RAG mode query, question={}, maxSources={}", question, maxSources);
        
        QueryResponse response = queryService.queryWithRag(question, maxSources);
        
        log.info("RAG query completed, responseTime={}ms, sources={}", 
                response.getResponseTime(), response.getSources().size());
        
        return ApiResponse.success(response);
    }

    /**
     * Clear query cache
     */
    @DeleteMapping("/cache")
    @Operation(summary = "Clear query cache", description = "Clear all query result caches")
    public ApiResponse<Void> clearCache() {
        
        log.info("Clear query cache request");
        
        queryService.clearAllHistory();
        
        log.info("Query cache cleared successfully");
        
        return ApiResponse.success(null, "Cache cleared successfully");
    }
}
