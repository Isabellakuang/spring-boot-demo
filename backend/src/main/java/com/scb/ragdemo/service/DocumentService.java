package com.scb.ragdemo.service;

import com.scb.ragdemo.model.dto.response.DocumentResponse;
import com.scb.ragdemo.model.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Document Service Interface
 * Handles document upload, processing, querying, and deletion operations
 */
public interface DocumentService {
    
    /**
     * Upload a document file
     * 
     * @param file Uploaded file
     * @return Document response DTO
     */
    DocumentResponse uploadDocument(MultipartFile file);
    
    /**
     * Asynchronously process a document
     * Extracts text content and splits it into chunks for storage
     * 
     * @param documentId Document ID
     */
    void processDocumentAsync(Long documentId);
    
    /**
     * Get document by ID
     * 
     * @param id Document ID
     * @return Document entity
     */
    Document getDocumentById(Long id);
    
    /**
     * Get all documents with pagination
     * 
     * @param pageable Pagination parameters
     * @return Paginated document response DTOs
     */
    Page<DocumentResponse> getAllDocuments(Pageable pageable);
    
    /**
     * Get documents by processing status
     * 
     * @param status Document status
     * @return List of document response DTOs
     */
    List<DocumentResponse> getDocumentsByStatus(String status);
    
    /**
     * Delete a document and its associated chunks
     * 
     * @param id Document ID
     */
    void deleteDocument(Long id);
    
    /**
     * Reprocess a failed document
     * Deletes associated chunks and reprocesses the document
     * 
     * @param id Document ID
     * @return Document response DTO
     */
    DocumentResponse reprocessDocument(Long id);
    
    /**
     * Get document statistics
     * 
     * @return Statistics map
     */
    java.util.Map<String, Object> getDocumentStatistics();
}
