package com.java.demo.controller;

import com.java.demo.dto.FaqDto;
import com.java.demo.service.FaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/knowledge")
@Tag(name = "Knowledge Base", description = "APIs for FAQ knowledge management")
public class KnowledgeController {

    private final FaqService faqService;

    public KnowledgeController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/faqs")
    @Operation(summary = "Get all FAQs", description = "Retrieve all FAQ entries from the knowledge base")
    public List<FaqDto> listFaqs() {
        return faqService.getAllFaqs();
    }

    @GetMapping("/faqs/{id}")
    @Operation(summary = "Get FAQ by ID", description = "Retrieve a specific FAQ entry by its ID")
    public FaqDto getFaq(@PathVariable Long id) {
        return faqService.getById(id);
    }

    @GetMapping("/faqs/search")
    @Operation(summary = "Search FAQs", description = "Search for the best matching FAQ based on query")
    public Optional<FaqDto> search(@RequestParam("query") String query) {
        return faqService.searchByQuery(query);
    }
}
