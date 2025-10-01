package com.java.demo.service;

import com.java.demo.config.VectorDbConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final VectorDbConfig vectorDbConfig;
    private final RestTemplate pineconeRestTemplate;
    private final LlmService llmService;

    /**
     * 向向量数据库插入文档
     */
    public void upsertDocument(String documentId, String content, Map<String, Object> metadata) {
        log.info("Upserting document to vector DB: {}", documentId);
        
        try {
            // 获取文档的嵌入向量
            double[] embedding = llmService.getEmbeddings(content);
            
            // 构建请求
            String url = String.format("https://%s-%s.svc.%s.pinecone.io/vectors/upsert",
                vectorDbConfig.getIndexName(),
                generateProjectId(),
                vectorDbConfig.getEnvironment()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", vectorDbConfig.getApiKey());
            
            Map<String, Object> vector = new HashMap<>();
            vector.put("id", documentId);
            vector.put("values", embedding);
            vector.put("metadata", metadata);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vectors", List.of(vector));
            requestBody.put("namespace", "default");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = pineconeRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            log.info("Document upserted successfully: {}", response.getBody());
            
        } catch (Exception e) {
            log.error("Error upserting document to vector DB", e);
            throw new RuntimeException("Failed to upsert document", e);
        }
    }

    /**
     * 向量相似度搜索
     */
    @Cacheable(value = "vectorSearch", key = "#query + '_' + #topK")
    public List<Map<String, Object>> search(String query, int topK) {
        log.info("Searching vector DB with query: {}", query);
        
        try {
            // 获取查询的嵌入向量
            double[] queryEmbedding = llmService.getEmbeddings(query);
            
            // 构建请求
            String url = String.format("https://%s-%s.svc.%s.pinecone.io/query",
                vectorDbConfig.getIndexName(),
                generateProjectId(),
                vectorDbConfig.getEnvironment()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", vectorDbConfig.getApiKey());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vector", queryEmbedding);
            requestBody.put("topK", topK);
            requestBody.put("includeMetadata", true);
            requestBody.put("namespace", "default");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = pineconeRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("matches")) {
                return (List<Map<String, Object>>) responseBody.get("matches");
            }
            
            return Collections.emptyList();
            
        } catch (Exception e) {
            log.error("Error searching vector DB", e);
            return Collections.emptyList();
        }
    }

    /**
     * 删除文档
     */
    public void deleteDocument(String documentId) {
        log.info("Deleting document from vector DB: {}", documentId);
        
        try {
            String url = String.format("https://%s-%s.svc.%s.pinecone.io/vectors/delete",
                vectorDbConfig.getIndexName(),
                generateProjectId(),
                vectorDbConfig.getEnvironment()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Key", vectorDbConfig.getApiKey());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("ids", List.of(documentId));
            requestBody.put("namespace", "default");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            pineconeRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            log.info("Document deleted successfully");
            
        } catch (Exception e) {
            log.error("Error deleting document from vector DB", e);
            throw new RuntimeException("Failed to delete document", e);
        }
    }

    private String generateProjectId() {
        // 从环境变量或配置中获取项目 ID
        // 这里简化处理
        return "default";
    }
}