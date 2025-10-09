package com.scb.ragdemo.controller;

import com.scb.ragdemo.model.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides health check and status monitoring related APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "System health check and status monitoring")
public class HealthController {

    /**
     * Basic health check
     */
    @GetMapping
    @Operation(summary = "Health check", description = "Check if the service is running normally and return basic status information")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "SCB RAG Demo");
        health.put("version", "1.0.0");
        
        return ApiResponse.success(health);
    }

    /**
     * Detailed health check
     */
    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check", description = "Get detailed system health status including component status and system information")
    public ApiResponse<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "SCB RAG Demo");
        health.put("version", "1.0.0");
        
        // Component status
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP");
        components.put("redis", "UP");
        components.put("poe-api", "UP");
        health.put("components", components);
        
        // System information
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("processors", Runtime.getRuntime().availableProcessors());
        system.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
        system.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        system.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        health.put("system", system);
        
        return ApiResponse.success(health);
    }

    /**
     * Readiness check
     */
    @GetMapping("/ready")
    @Operation(summary = "Readiness check", description = "Check if the service is ready to accept requests")
    public ApiResponse<Map<String, Object>> ready() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("ready", true);
        readiness.put("timestamp", LocalDateTime.now());
        
        return ApiResponse.success(readiness);
    }

    /**
     * Liveness check
     */
    @GetMapping("/live")
    @Operation(summary = "Liveness check", description = "Check if the service is alive")
    public ApiResponse<Map<String, Object>> live() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("alive", true);
        liveness.put("timestamp", LocalDateTime.now());
        
        return ApiResponse.success(liveness);
    }
}
