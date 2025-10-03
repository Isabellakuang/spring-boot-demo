package com.java.demo.service;

import com.java.demo.dto.FaqDto;
import com.java.demo.exception.ResourceNotFoundException;
import com.java.demo.model.FaqEntry;
import com.java.demo.repository.FaqRepository;
import io.micrometer.core.annotation.Timed;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Cacheable("faq_list")
    public List<FaqDto> getAllFaqs() {
        return faqRepository.findAll()
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Cacheable(value = "faq_entry", key = "#id")
    public FaqDto getById(Long id) {
        FaqEntry entry = faqRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ entry %d not found".formatted(id)));
        return toDto(entry);
    }

    @Timed(value = "faq.search", description = "FAQ search performance")
    public Optional<FaqDto> searchByQuery(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        
        String trimmed = query.toLowerCase(Locale.ROOT).strip();
        final double threshold = 1.5;
        
        // 使用 Map 缓存计算结果，避免重复计算相似度分数
        return getAllFaqs()
            .stream()
            .map(faq -> new AbstractMap.SimpleEntry<>(faq, TextSimilarity.score(trimmed, faq)))
            .filter(entry -> entry.getValue() >= threshold)
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }

    @Async
    public CompletableFuture<Integer> warmUpCacheAsync() {
        List<FaqDto> cached = getAllFaqs();
        return CompletableFuture.completedFuture(cached.size());
    }

    public List<FaqEntry> findAll() {
        return faqRepository.findAll();
    }

    public List<FaqDto> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        
        String trimmed = keyword.toLowerCase(Locale.ROOT).strip();
        final double threshold = 1.0;
        
        return getAllFaqs()
            .stream()
            .filter(faq -> TextSimilarity.score(trimmed, faq) >= threshold)
            .toList();
    }

    private FaqDto toDto(FaqEntry entry) {
        return new FaqDto(entry.getId(), entry.getQuestion(), entry.getAnswer(), entry.getTags());
    }
}
