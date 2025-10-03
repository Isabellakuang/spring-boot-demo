package com.java.demo.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 简化的内存向量存储 - 用于演示RAG功能
 * 使用TF-IDF进行简单的文本相似度计算
 */
@Service
@Slf4j
public class InMemoryVectorStore {

    private final Map<String, IndexedDocument> documentStore = new ConcurrentHashMap<>();
    private final Map<String, Double> idfCache = new ConcurrentHashMap<>();

    @Data
    public static class IndexedDocument {
        private String docId;
        private String content;
        private Map<String, Object> metadata;
        private Map<String, Integer> termFrequency;
        private LocalDateTime indexedAt;
        private int totalTerms;
    }

    @Data
    public static class SearchResult {
        private String docId;
        private String content;
        private Map<String, Object> metadata;
        private double score;
        private List<String> matchedTerms;
    }

    /**
     * 索引文档
     */
    public Map<String, Object> indexDocument(String docId, String content, Map<String, Object> metadata) {
        try {
            // 分词和计算词频
            Map<String, Integer> termFreq = calculateTermFrequency(content);
            
            IndexedDocument doc = new IndexedDocument();
            doc.setDocId(docId);
            doc.setContent(content);
            doc.setMetadata(metadata != null ? metadata : new HashMap<>());
            doc.setTermFrequency(termFreq);
            doc.setTotalTerms(termFreq.values().stream().mapToInt(Integer::intValue).sum());
            doc.setIndexedAt(LocalDateTime.now());
            
            documentStore.put(docId, doc);
            
            // 更新IDF缓存
            updateIdfCache();
            
            log.info("Indexed document: {} with {} unique terms", docId, termFreq.size());
            
            return Map.of(
                "success", true,
                "docId", docId,
                "uniqueTerms", termFreq.size(),
                "totalTerms", doc.getTotalTerms(),
                "indexedAt", doc.getIndexedAt()
            );
        } catch (Exception e) {
            log.error("Error indexing document {}: {}", docId, e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * 搜索相似文档
     */
    public List<SearchResult> search(String query, int topK) {
        if (documentStore.isEmpty()) {
            log.warn("Document store is empty");
            return Collections.emptyList();
        }

        Map<String, Integer> queryTermFreq = calculateTermFrequency(query);
        
        return documentStore.values().stream()
            .map(doc -> {
                double score = calculateTfIdfSimilarity(queryTermFreq, doc);
                List<String> matchedTerms = findMatchedTerms(queryTermFreq, doc);
                
                SearchResult result = new SearchResult();
                result.setDocId(doc.getDocId());
                result.setContent(doc.getContent());
                result.setMetadata(doc.getMetadata());
                result.setScore(score);
                result.setMatchedTerms(matchedTerms);
                
                return result;
            })
            .filter(result -> result.getScore() > 0)
            .sorted(Comparator.comparing(SearchResult::getScore).reversed())
            .limit(topK)
            .collect(Collectors.toList());
    }

    /**
     * 删除文档
     */
    public boolean deleteDocument(String docId) {
        IndexedDocument removed = documentStore.remove(docId);
        if (removed != null) {
            updateIdfCache();
            log.info("Deleted document: {}", docId);
            return true;
        }
        return false;
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        int totalDocs = documentStore.size();
        int totalTerms = documentStore.values().stream()
            .mapToInt(IndexedDocument::getTotalTerms)
            .sum();
        
        double avgTermsPerDoc = totalDocs > 0 ? (double) totalTerms / totalDocs : 0;
        
        return Map.of(
            "totalDocuments", totalDocs,
            "totalTerms", totalTerms,
            "averageTermsPerDoc", avgTermsPerDoc,
            "uniqueTermsInVocabulary", idfCache.size(),
            "storageType", "in-memory"
        );
    }

    /**
     * 清空所有文档
     */
    public void clear() {
        documentStore.clear();
        idfCache.clear();
        log.info("Cleared all documents from vector store");
    }

    // ===== 私有辅助方法 =====

    /**
     * 计算词频
     */
    private Map<String, Integer> calculateTermFrequency(String text) {
        Map<String, Integer> termFreq = new HashMap<>();
        
        // 简单分词：转小写，按空格和标点分割
        String[] terms = text.toLowerCase()
            .replaceAll("[^a-z0-9\\u4e00-\\u9fa5\\s]", " ")
            .split("\\s+");
        
        for (String term : terms) {
            if (!term.isEmpty() && term.length() > 1) {
                termFreq.merge(term, 1, Integer::sum);
            }
        }
        
        return termFreq;
    }

    /**
     * 更新IDF缓存
     */
    private void updateIdfCache() {
        idfCache.clear();
        
        int totalDocs = documentStore.size();
        if (totalDocs == 0) return;
        
        // 计算每个词在多少文档中出现
        Map<String, Integer> documentFrequency = new HashMap<>();
        
        for (IndexedDocument doc : documentStore.values()) {
            Set<String> uniqueTerms = doc.getTermFrequency().keySet();
            for (String term : uniqueTerms) {
                documentFrequency.merge(term, 1, Integer::sum);
            }
        }
        
        // 计算IDF
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            double idf = Math.log((double) totalDocs / entry.getValue());
            idfCache.put(entry.getKey(), idf);
        }
    }

    /**
     * 计算TF-IDF相似度
     */
    private double calculateTfIdfSimilarity(Map<String, Integer> queryTermFreq, IndexedDocument doc) {
        double dotProduct = 0.0;
        double queryNorm = 0.0;
        double docNorm = 0.0;
        
        // 计算查询向量的TF-IDF
        Map<String, Double> queryTfIdf = new HashMap<>();
        for (Map.Entry<String, Integer> entry : queryTermFreq.entrySet()) {
            String term = entry.getKey();
            double tf = (double) entry.getValue() / queryTermFreq.values().stream().mapToInt(Integer::intValue).sum();
            double idf = idfCache.getOrDefault(term, 0.0);
            double tfidf = tf * idf;
            queryTfIdf.put(term, tfidf);
            queryNorm += tfidf * tfidf;
        }
        
        // 计算文档向量的TF-IDF
        Map<String, Double> docTfIdf = new HashMap<>();
        for (Map.Entry<String, Integer> entry : doc.getTermFrequency().entrySet()) {
            String term = entry.getKey();
            double tf = (double) entry.getValue() / doc.getTotalTerms();
            double idf = idfCache.getOrDefault(term, 0.0);
            double tfidf = tf * idf;
            docTfIdf.put(term, tfidf);
            docNorm += tfidf * tfidf;
        }
        
        // 计算点积
        for (String term : queryTfIdf.keySet()) {
            if (docTfIdf.containsKey(term)) {
                dotProduct += queryTfIdf.get(term) * docTfIdf.get(term);
            }
        }
        
        // 余弦相似度
        if (queryNorm == 0 || docNorm == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(queryNorm) * Math.sqrt(docNorm));
    }

    /**
     * 查找匹配的词
     */
    private List<String> findMatchedTerms(Map<String, Integer> queryTermFreq, IndexedDocument doc) {
        return queryTermFreq.keySet().stream()
            .filter(term -> doc.getTermFrequency().containsKey(term))
            .collect(Collectors.toList());
    }
}
