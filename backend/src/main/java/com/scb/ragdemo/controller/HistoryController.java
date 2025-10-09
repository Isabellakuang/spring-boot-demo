package com.scb.ragdemo.controller;

import com.scb.ragdemo.model.dto.response.ApiResponse;
import com.scb.ragdemo.model.entity.QueryHistory;
import com.scb.ragdemo.model.enums.QueryMode;
import com.scb.ragdemo.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Query History Controller
 * Provides query history related APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Tag(name = "Query History", description = "Query history and statistics")
public class HistoryController {

    private final QueryService queryService;

    /**
     * Get query history with pagination
     */
    @GetMapping
    @Operation(summary = "Get query history", description = "Get all query history records with pagination support")
    public ApiResponse<Page<QueryHistory>> getHistory(
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Get query history request, page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QueryHistory> history = queryService.getQueryHistory(pageable);
        
        log.info("Query history retrieved, total: {}, current page: {}", history.getTotalElements(), page);
        
        return ApiResponse.success(history);
    }

    /**
     * Get query history by mode
     */
    @GetMapping("/mode/{mode}")
    @Operation(summary = "Query by mode", description = "Get query history filtered by query mode")
    public ApiResponse<Page<QueryHistory>> getHistoryByMode(
            @Parameter(description = "Query mode", required = true)
            @PathVariable QueryMode mode,
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Get query history by mode, mode={}, page={}, size={}", mode, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<QueryHistory> history = queryService.getQueryHistoryByMode(mode.name(), pageable);
        
        log.info("Query history retrieved, total: {}, current page: {}", history.getTotalElements(), page);
        
        return ApiResponse.success(history);
    }

    /**
     * Delete specific query history
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete query history", description = "Delete query history record by ID")
    public ApiResponse<Void> deleteHistory(
            @Parameter(description = "Query history ID", required = true)
            @PathVariable Long id) {
        
        log.info("Delete query history request, id={}", id);
        
        queryService.deleteQueryHistory(id);
        
        log.info("Query history deleted successfully, id={}", id);
        
        return ApiResponse.success(null, "Query history deleted successfully");
    }

    /**
     * Clear all query history
     */
    @DeleteMapping
    @Operation(summary = "Clear all query history", description = "Delete all query history records")
    public ApiResponse<Void> clearAllHistory() {
        
        log.info("Clear all query history request");
        
        queryService.clearAllHistory();
        
        log.info("All query history cleared successfully");
        
        return ApiResponse.success(null, "All query history cleared successfully");
    }

    /**
     * Get query statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get query statistics", description = "Get query statistics including total count, mode distribution, average response time and other information")
    public ApiResponse<Map<String, Object>> getStatistics() {
        
        log.info("Get query statistics request");
        
        Map<String, Object> stats = queryService.getQueryStatistics();
        
        return ApiResponse.success(stats);
    }
}
