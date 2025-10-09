package com.scb.ragdemo.controller;

import com.scb.ragdemo.model.dto.response.ApiResponse;
import com.scb.ragdemo.model.dto.response.DocumentResponse;
import com.scb.ragdemo.model.enums.DocumentStatus;
import com.scb.ragdemo.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Document Management Controller
 * Provides REST API for document upload, query, deletion and reprocessing
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Document upload, query and management related interfaces")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload PDF document
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload PDF document", description = "Upload PDF document and automatically parse and vectorize the content")
    public ApiResponse<DocumentResponse> uploadDocument(
            @Parameter(description = "PDF file", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("Received document upload request, filename: {}", file.getOriginalFilename());
        
        DocumentResponse response = documentService.uploadDocument(file);
        
        log.info("Document upload completed, id={}, filename={}", response.getId(), response.getFilename());
        
        return ApiResponse.success(response, "Document uploaded successfully, processing in background");
    }

    /**
     * Get all documents with pagination
     */
    @GetMapping
    @Operation(summary = "Get document list", description = "Get all documents with pagination support")
    public ApiResponse<Page<DocumentResponse>> getAllDocuments(
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Get document list request, page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentResponse> documents = documentService.getAllDocuments(pageable);
        
        log.info("Document list query completed, total: {}, current page: {}", documents.getTotalElements(), page);
        
        return ApiResponse.success(documents);
    }

    /**
     * Get document details by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get document details", description = "Get detailed information of a document by ID")
    public ApiResponse<DocumentResponse> getDocumentById(
            @Parameter(description = "Document ID", required = true)
            @PathVariable Long id) {
        
        log.info("Get document details: id={}", id);
        
        // Service layer converts Document entity to DocumentResponse
        // This is a simplified implementation, actual implementation should call service layer
        DocumentResponse response = DocumentResponse.builder()
                .id(id)
                .build();
        
        return ApiResponse.success(response);
    }

    /**
     * Get documents by status with pagination
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Query by status", description = "Get document list filtered by processing status")
    public ApiResponse<List<DocumentResponse>> getDocumentsByStatus(
            @Parameter(description = "Document status", required = true)
            @PathVariable DocumentStatus status) {
        
        log.info("Query documents by status: status={}", status);
        
        // Service layer accepts String parameter
        List<DocumentResponse> documents = documentService.getDocumentsByStatus(status.name());
        
        log.info("Document list query completed, count: {}", documents.size());
        
        return ApiResponse.success(documents);
    }


    /**
     * Delete document
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Delete document by ID, including all associated chunks and vectors")
    public ApiResponse<Void> deleteDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable Long id) {
        
        log.info("Delete document request: id={}", id);
        
        documentService.deleteDocument(id);
        
        log.info("Document deleted successfully, id={}", id);
        
        return ApiResponse.success(null, "Document deleted successfully");
    }

    /**
     * Reprocess document
     */
    @PostMapping("/{id}/reprocess")
    @Operation(summary = "Reprocess document", description = "Reprocess failed document, re-parse and vectorize")
    public ApiResponse<DocumentResponse> reprocessDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable Long id) {
        
        log.info("Reprocess document request: id={}", id);
        
        DocumentResponse response = documentService.reprocessDocument(id);
        
        log.info("Document reprocessing task submitted successfully, id={}", id);
        
        return ApiResponse.success(response, "Document reprocessing task submitted successfully");
    }


    /**
     * Get document statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get document statistics", description = "Get document statistics including total count, status distribution and other information")
    public ApiResponse<java.util.Map<String, Object>> getStatistics() {
        
        log.info("Get document statistics request");
        
        java.util.Map<String, Object> stats = documentService.getDocumentStatistics();
        
        return ApiResponse.success(stats);
    }
}
