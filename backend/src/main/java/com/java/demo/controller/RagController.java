package com.java.demo.controller;

import com.java.demo.dto.DocumentIndexRequest;
import com.java.demo.dto.RagQueryRequest;
import com.java.demo.dto.RagQueryResponse;
import com.java.demo.service.SimplifiedRagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "RAG", description = "检索增强生成 API")
public class RagController {

    private final SimplifiedRagService ragService;

    @PostMapping("/index")
    @Operation(summary = "索引文档", description = "将文档索引到向量数据库")
    public ResponseEntity<Map<String, Object>> indexDocument(
            @Valid @RequestBody DocumentIndexRequest request) {
        log.info("Indexing document: {}", request.getDocId());
        
        Map<String, Object> result = ragService.indexDocument(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/query")
    @Operation(summary = "RAG 查询", description = "使用检索增强生成回答问题")
    public ResponseEntity<RagQueryResponse> query(@Valid @RequestBody RagQueryRequest request) {
        log.info("RAG query: {}", request.getQuestion());
        
        RagQueryResponse response = ragService.query(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch-index")
    @Operation(summary = "批量索引", description = "批量索引多个文档")
    public ResponseEntity<Map<String, Object>> batchIndex(
            @RequestBody java.util.List<DocumentIndexRequest> requests) {
        log.info("Batch indexing {} documents", requests.size());
        
        Map<String, Object> result = ragService.batchIndex(requests);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/index/{docId}")
    @Operation(summary = "删除索引", description = "从向量数据库删除文档")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable String docId) {
        log.info("Deleting document: {}", docId);
        
        Map<String, Object> result = ragService.deleteDocument(docId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取 RAG 统计", description = "获取索引和查询统计信息")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = ragService.getStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/refresh-index")
    @Operation(summary = "刷新索引", description = "重新索引所有对话和 FAQ")
    public ResponseEntity<Map<String, Object>> refreshIndex() {
        log.info("Refreshing RAG index");
        
        Map<String, Object> result = ragService.refreshIndex();
        return ResponseEntity.ok(result);
    }
}
