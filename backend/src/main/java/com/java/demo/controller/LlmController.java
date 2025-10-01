package com.java.demo.controller;

import com.java.demo.dto.LlmRequest;
import com.java.demo.dto.LlmResponse;
import com.java.demo.service.LlmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "LLM", description = "大语言模型集成 API")
@SecurityRequirement(name = "bearer-jwt")
public class LlmController {

    private final LlmService llmService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "生成文本", description = "使用 OpenAI 或 Claude 生成文本")
    public ResponseEntity<LlmResponse> generateText(@Valid @RequestBody LlmRequest request) {
        log.info("Generating text with provider: {}, model: {}", 
            request.getProvider(), request.getModel());
        
        LlmResponse response = llmService.generate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-with-fallback")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "带降级的文本生成", description = "主 LLM 失败时自动降级到备用")
    public ResponseEntity<LlmResponse> generateWithFallback(
            @Valid @RequestBody LlmRequest request) {
        log.info("Generating text with fallback, primary: {}", request.getProvider());
        
        LlmResponse response = llmService.generateWithFallback(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "对话式生成", description = "支持多轮对话的文本生成")
    public ResponseEntity<LlmResponse> chat(
            @RequestParam String conversationId,
            @Valid @RequestBody LlmRequest request) {
        log.info("Chat for conversation: {}", conversationId);
        
        LlmResponse response = llmService.chat(conversationId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/models")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "获取可用模型列表")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        Map<String, Object> models = llmService.getAvailableModels();
        return ResponseEntity.ok(models);
    }

    @GetMapping("/usage")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取 LLM 使用统计")
    public ResponseEntity<Map<String, Object>> getUsageStats(
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> stats = llmService.getUsageStats(provider, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}