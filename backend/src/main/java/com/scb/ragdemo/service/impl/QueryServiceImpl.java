package com.scb.ragdemo.service.impl;

import com.scb.ragdemo.model.dto.request.QueryRequest;
import com.scb.ragdemo.model.dto.response.QueryResponse;
import com.scb.ragdemo.model.dto.response.SourceReference;
import com.scb.ragdemo.model.entity.DocumentChunk;
import com.scb.ragdemo.model.entity.QueryHistory;
import com.scb.ragdemo.model.enums.QueryMode;
import com.scb.ragdemo.repository.DocumentChunkRepository;
import com.scb.ragdemo.repository.QueryHistoryRepository;
import com.scb.ragdemo.service.PoeClientService;
import com.scb.ragdemo.service.QueryService;
import com.scb.ragdemo.service.RouterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final RouterService routerService;
    private final PoeClientService poeClientService;
    private final DocumentChunkRepository documentChunkRepository;
    private final QueryHistoryRepository queryHistoryRepository;

    @Override
    @Transactional
    public QueryResponse processQuery(QueryRequest request) {
        long startTime = System.currentTimeMillis();
        
        String question = request.getQuestion();
        QueryMode requestedMode = request.getMode();
        
        log.info("Processing query: question='{}', requestedMode={}", question, requestedMode);
        
        QueryMode actualMode;
        if (requestedMode == QueryMode.AUTO) {
            actualMode = routerService.determineQueryMode(question);
            log.info("AUTO mode determined: {}", actualMode);
        } else {
            actualMode = requestedMode;
        }
        
        QueryResponse cachedResponse = getCachedResult(question, actualMode.name());
        if (cachedResponse != null) {
            log.info("Returning cached result for question: {}", question);
            cachedResponse.setFromCache(true);
            return cachedResponse;
        }
        
        QueryResponse response;
        if (actualMode == QueryMode.RAG) {
            response = queryWithRag(question, request.getMaxSources() != null ? request.getMaxSources() : 3);
        } else {
            response = queryWithNlp(question);
        }
        
        long responseTime = System.currentTimeMillis() - startTime;
        response.setResponseTime(responseTime);
        
        saveQueryHistory(question, response.getAnswer(), actualMode, responseTime, 
                        response.getSources() != null ? response.getSources().size() : 0);
        
        cacheResult(question, actualMode.name(), response);
        
        log.info("Query processed successfully: mode={}, responseTime={}ms", actualMode, responseTime);
        
        return response;
    }

    @Override
    @Transactional
    public QueryResponse queryWithNlp(String question) {
        log.info("Executing NLP query: {}", question);
        
        long startTime = System.currentTimeMillis();
        
        String answer = poeClientService.sendMessage(question);
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        QueryResponse response = QueryResponse.builder()
                .answer(answer)
                .mode(QueryMode.NLP)
                .responseTime(responseTime)
                .sources(Collections.emptyList())
                .fromCache(false)
                .build();
        
        saveQueryHistory(question, answer, QueryMode.NLP, responseTime, 0);
        
        log.info("NLP query completed: responseTime={}ms", responseTime);
        
        return response;
    }

    @Override
    @Transactional
    public QueryResponse queryWithRag(String question, int topK) {
        log.info("Executing RAG query: question='{}', topK={}", question, topK);
        
        long startTime = System.currentTimeMillis();
        
        List<DocumentChunk> relevantChunks = documentChunkRepository.searchByContentTsv(question, topK);
        
        log.info("Found {} relevant document chunks", relevantChunks.size());
        
        String context = relevantChunks.stream()
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n\n"));
        
        String answer;
        if (context.isEmpty()) {
            log.warn("No relevant documents found, falling back to NLP mode");
            answer = poeClientService.sendMessage(question);
        } else {
            answer = poeClientService.sendMessageWithContext(question, context);
        }
        
        List<SourceReference> sources = relevantChunks.stream()
                .map(chunk -> SourceReference.builder()
                        .chunkId(chunk.getId())
                        .documentId(chunk.getDocumentId())
                        .filename(chunk.getDocument() != null ? chunk.getDocument().getFilename() : "Unknown")
                        .chunkIndex(chunk.getChunkIndex())
                        .content(chunk.getContent())
                        .similarity(0.0)
                        .build())
                .collect(Collectors.toList());
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        QueryResponse response = QueryResponse.builder()
                .answer(answer)
                .mode(QueryMode.RAG)
                .responseTime(responseTime)
                .sources(sources)
                .fromCache(false)
                .build();
        
        saveQueryHistory(question, answer, QueryMode.RAG, responseTime, sources.size());
        
        log.info("RAG query completed: responseTime={}ms, sources={}", responseTime, sources.size());
        
        return response;
    }

    @Override
    public Page<QueryHistory> getQueryHistory(Pageable pageable) {
        return queryHistoryRepository.findAll(pageable);
    }

    @Override
    public Page<QueryHistory> getQueryHistoryByMode(String mode, Pageable pageable) {
        QueryMode queryMode = QueryMode.valueOf(mode.toUpperCase());
        return queryHistoryRepository.findByQueryTypeOrderByCreatedAtDesc(queryMode, pageable);
    }

    @Override
    @Transactional
    public void deleteQueryHistory(Long id) {
        queryHistoryRepository.deleteById(id);
        log.info("Deleted query history: id={}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "queryCache", allEntries = true)
    public void clearAllHistory() {
        queryHistoryRepository.deleteAll();
        log.info("Cleared all query history");
    }

    @Override
    public Map<String, Object> getQueryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalQueries = queryHistoryRepository.count();
        long nlpQueries = queryHistoryRepository.countByQueryType(QueryMode.NLP);
        long ragQueries = queryHistoryRepository.countByQueryType(QueryMode.RAG);
        
        stats.put("totalQueries", totalQueries);
        stats.put("nlpQueries", nlpQueries);
        stats.put("ragQueries", ragQueries);
        stats.put("nlpPercentage", totalQueries > 0 ? (nlpQueries * 100.0 / totalQueries) : 0);
        stats.put("ragPercentage", totalQueries > 0 ? (ragQueries * 100.0 / totalQueries) : 0);
        
        return stats;
    }

    @Override
    @Cacheable(value = "queryCache", key = "#question + '_' + #mode")
    public QueryResponse getCachedResult(String question, String mode) {
        log.debug("Checking cache for: question='{}', mode={}", question, mode);
        return null;
    }

    @Override
    public void cacheResult(String question, String mode, QueryResponse response) {
        log.debug("Caching result for: question='{}', mode={}", question, mode);
    }

    private void saveQueryHistory(String question, String answer, QueryMode mode, long responseTime, int sourceCount) {
        QueryHistory history = QueryHistory.builder()
                .queryText(question)
                .responseText(answer)
                .queryType(mode)
                .responseTimeMs(responseTime)
                .sourceCount(sourceCount)
                .sessionId(UUID.randomUUID().toString())
                .build();
        
        queryHistoryRepository.save(history);
        log.debug("Saved query history: mode={}, responseTime={}ms", mode, responseTime);
    }
}
