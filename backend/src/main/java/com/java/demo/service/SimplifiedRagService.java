package com.java.demo.service;

import com.java.demo.dto.DocumentIndexRequest;
import com.java.demo.dto.RagQueryRequest;
import com.java.demo.dto.RagQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简化的RAG服务 - 使用内存向量存储
 * 适合本地演示，无需外部向量数据库
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimplifiedRagService {

    private final InMemoryVectorStore vectorStore;
    private final SimplifiedLlmService llmService;

    /**
     * RAG查询 - 检索增强生成
     */
    public RagQueryResponse query(RagQueryRequest request) {
        try {
            log.info("Processing RAG query: {}", request.getQuestion());
            
            // 1. 检索相关文档
            List<InMemoryVectorStore.SearchResult> searchResults = 
                vectorStore.search(request.getQuestion(), request.getTopK());
            
            if (searchResults.isEmpty()) {
                return RagQueryResponse.builder()
                    .answer("抱歉，我没有找到相关信息来回答您的问题。请先索引一些文档。")
                    .sources(Collections.emptyList())
                    .confidence(0.0)
                    .usage(Map.of("note", "No documents in knowledge base"))
                    .queriedAt(LocalDateTime.now())
                    .build();
            }
            
            // 2. 构建上下文
            String context = buildContext(searchResults);
            
            // 3. 生成答案
            String prompt = buildRagPrompt(request.getQuestion(), context);
            String answer = llmService.generateText(prompt, 500);
            
            // 4. 计算置信度
            double confidence = calculateConfidence(searchResults);
            
            // 5. 准备源引用
            List<Map<String, Object>> sources = searchResults.stream()
                .map(result -> Map.of(
                    "docId", (Object) result.getDocId(),
                    "content", result.getContent(),
                    "score", result.getScore(),
                    "matchedTerms", result.getMatchedTerms(),
                    "metadata", result.getMetadata()
                ))
                .collect(Collectors.toList());
            
            log.info("RAG query completed. Found {} relevant documents, confidence: {}", 
                searchResults.size(), confidence);
            
            return RagQueryResponse.builder()
                .answer(answer)
                .sources(sources)
                .confidence(confidence)
                .usage(Map.of(
                    "retrievedDocuments", searchResults.size(),
                    "averageScore", searchResults.stream()
                        .mapToDouble(InMemoryVectorStore.SearchResult::getScore)
                        .average()
                        .orElse(0.0)
                ))
                .queriedAt(LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            log.error("Error processing RAG query: {}", e.getMessage(), e);
            return RagQueryResponse.builder()
                .answer("处理查询时发生错误：" + e.getMessage())
                .sources(Collections.emptyList())
                .confidence(0.0)
                .usage(Map.of("error", e.getMessage()))
                .queriedAt(LocalDateTime.now())
                .build();
        }
    }

    /**
     * 索引单个文档
     */
    public Map<String, Object> indexDocument(DocumentIndexRequest request) {
        try {
            log.info("Indexing document: {}", request.getDocId());
            
            // 简单分块策略
            List<String> chunks = splitIntoChunks(request.getDocument(), 500, 50);
            
            int successCount = 0;
            for (int i = 0; i < chunks.size(); i++) {
                String chunkId = request.getDocId() + "_chunk_" + i;
                Map<String, Object> chunkMetadata = new HashMap<>(request.getMetadata());
                chunkMetadata.put("chunk_index", i);
                chunkMetadata.put("total_chunks", chunks.size());
                chunkMetadata.put("parent_doc_id", request.getDocId());
                
                Map<String, Object> result = vectorStore.indexDocument(
                    chunkId, 
                    chunks.get(i), 
                    chunkMetadata
                );
                
                if (Boolean.TRUE.equals(result.get("success"))) {
                    successCount++;
                }
            }
            
            log.info("Indexed {} chunks for document {}", successCount, request.getDocId());
            
            return Map.of(
                "success", true,
                "docId", request.getDocId(),
                "indexed_chunks", successCount,
                "total_chunks", chunks.size(),
                "indexedAt", LocalDateTime.now()
            );
            
        } catch (Exception e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * 批量索引文档
     */
    public Map<String, Object> batchIndex(List<DocumentIndexRequest> requests) {
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (DocumentIndexRequest request : requests) {
            try {
                Map<String, Object> result = indexDocument(request);
                if (Boolean.TRUE.equals(result.get("success"))) {
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
            "errors", errors,
            "completedAt", LocalDateTime.now()
        );
    }

    /**
     * 删除文档
     */
    public Map<String, Object> deleteDocument(String docId) {
        try {
            boolean deleted = vectorStore.deleteDocument(docId);
            
            return Map.of(
                "success", deleted,
                "docId", docId,
                "deletedAt", LocalDateTime.now()
            );
        } catch (Exception e) {
            log.error("Error deleting document: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> vectorStats = vectorStore.getStats();
        
        return Map.of(
            "vectorStore", vectorStats,
            "ragEngine", Map.of(
                "type", "simplified",
                "storage", "in-memory",
                "features", Arrays.asList(
                    "document_indexing",
                    "tf-idf_search",
                    "rag_query",
                    "chunk_splitting"
                )
            ),
            "retrievedAt", LocalDateTime.now()
        );
    }

    /**
     * 刷新索引 - 重新索引所有对话和FAQ
     */
    public Map<String, Object> refreshIndex() {
        try {
            log.info("Refreshing RAG index");
            
            // 清空现有索引
            vectorStore.clear();
            
            // 在实际应用中，这里会：
            // 1. 从数据库加载所有对话
            // 2. 从数据库加载所有FAQ
            // 3. 重新索引所有文档
            
            return Map.of(
                "success", true,
                "message", "Index refreshed successfully",
                "note", "In production, this would reload all conversations and FAQs from database",
                "refreshedAt", LocalDateTime.now()
            );
        } catch (Exception e) {
            log.error("Error refreshing index: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * 清空所有索引
     */
    public Map<String, Object> clearAll() {
        vectorStore.clear();
        return Map.of(
            "success", true,
            "message", "All documents cleared",
            "clearedAt", LocalDateTime.now()
        );
    }

    // ===== 私有辅助方法 =====

    /**
     * 将文本分割成块
     */
    private List<String> splitIntoChunks(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        
        for (int i = 0; i < words.length; i += (chunkSize - overlap)) {
            int end = Math.min(i + chunkSize, words.length);
            String chunk = String.join(" ", Arrays.copyOfRange(words, i, end));
            if (!chunk.trim().isEmpty()) {
                chunks.add(chunk);
            }
            
            if (end >= words.length) break;
        }
        
        return chunks.isEmpty() ? List.of(text) : chunks;
    }

    /**
     * 构建上下文
     */
    private String buildContext(List<InMemoryVectorStore.SearchResult> results) {
        StringBuilder context = new StringBuilder();
        
        for (int i = 0; i < results.size(); i++) {
            InMemoryVectorStore.SearchResult result = results.get(i);
            context.append(String.format(
                "【文档片段 %d】(相似度: %.2f)\n%s\n\n",
                i + 1,
                result.getScore(),
                result.getContent()
            ));
        }
        
        return context.toString();
    }

    /**
     * 构建RAG提示词
     */
    private String buildRagPrompt(String question, String context) {
        return String.format("""
            请基于以下上下文信息回答用户的问题。如果上下文中没有相关信息，请明确说明。
            
            上下文信息：
            %s
            
            用户问题：%s
            
            请提供准确、简洁的回答，并在回答中引用相关的文档片段编号。
            """, context, question);
    }

    /**
     * 计算置信度
     */
    private double calculateConfidence(List<InMemoryVectorStore.SearchResult> results) {
        if (results.isEmpty()) {
            return 0.0;
        }
        
        // 基于平均相似度分数
        double avgScore = results.stream()
            .mapToDouble(InMemoryVectorStore.SearchResult::getScore)
            .average()
            .orElse(0.0);
        
        // 考虑结果数量的影响
        double countFactor = Math.min(results.size() / 5.0, 1.0);
        
        return Math.min(avgScore * (0.7 + 0.3 * countFactor), 1.0);
    }
}
