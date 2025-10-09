package com.scb.ragdemo.service.impl;

import com.scb.ragdemo.exception.custom.DocumentNotFoundException;
import com.scb.ragdemo.exception.custom.InvalidFileException;
import com.scb.ragdemo.model.dto.response.DocumentResponse;
import com.scb.ragdemo.model.entity.Document;
import com.scb.ragdemo.model.entity.DocumentChunk;
import com.scb.ragdemo.model.enums.DocumentStatus;
import com.scb.ragdemo.repository.DocumentChunkRepository;
import com.scb.ragdemo.repository.DocumentRepository;
import com.scb.ragdemo.service.DocumentService;
import com.scb.ragdemo.util.FileValidator;
import com.scb.ragdemo.util.PdfParser;
import com.scb.ragdemo.util.TextSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Document Service Implementation
 * Handles document upload, parsing, chunking, and management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final PdfParser pdfParser;
    private final TextSplitter textSplitter;
    private final FileValidator fileValidator;
    
    // Document storage directory
    private static final String UPLOAD_DIR = "uploads/documents";
    
    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file) {
        log.info("Starting document upload: {}", file.getOriginalFilename());
        
        // Validate file
        fileValidator.validateFile(file);
        
        try {
            // Create upload directory
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String filename = generateUniqueFilename(originalFilename);
            Path filePath = uploadPath.resolve(filename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            log.info("File saved to path: {}", filePath);
            
            // Create document record
            Document document = new Document();
            document.setFilename(originalFilename);
            document.setFilePath(filePath.toString());
            document.setFileSize(file.getSize());
            document.setStatus(DocumentStatus.PENDING);
            document.setUploadTime(LocalDateTime.now());
            
            // Save to database
            document = documentRepository.save(document);
            log.info("Document record created with ID: {}", document.getId());
            
            // Asynchronously process document
            processDocumentAsync(document.getId());
            
            return convertToResponse(document);
            
        } catch (IOException e) {
            log.error("File save failed: {}", e.getMessage(), e);
            throw new InvalidFileException("File save failed: " + e.getMessage());
        }
    }
    
    @Override
    @Async
    @Transactional
    public void processDocumentAsync(Long documentId) {
        log.info("Starting asynchronous document processing, ID: {}", documentId);
        
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        try {
            // Update status to processing
            document.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(document);
            
            // Extract text content
            log.info("Extracting document content: {}", document.getFilename());
            Path filePath = Paths.get(document.getFilePath());
            String text;
            
            // Check file type and extract text accordingly
            String filename = document.getFilename().toLowerCase();
            if (filename.endsWith(".txt")) {
                // For TXT files, read directly
                text = Files.readString(filePath);
                log.info("Extracted text from TXT file, length: {}", text.length());
            } else if (filename.endsWith(".pdf")) {
                // For PDF files, use PDF parser
                text = pdfParser.extractText(Files.newInputStream(filePath), document.getFilename());
            } else {
                throw new InvalidFileException("Unsupported file type");
            }
            
            if (text == null || text.trim().isEmpty()) {
                throw new InvalidFileException("Unable to extract valid text content from document");
            }
            
            // Split text
            log.info("Splitting document content, text length: {}", text.length());
            List<String> chunks = textSplitter.splitText(text);
            log.info("Generated {} chunks", chunks.size());
            
            // Save chunks
            int chunkIndex = 0;
            for (String chunkContent : chunks) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setDocumentId(document.getId());
                chunk.setChunkIndex(chunkIndex++);
                chunk.setContent(chunkContent);
                chunk.setCreatedAt(LocalDateTime.now());
                
                documentChunkRepository.save(chunk);
            }
            
            // Update document status to completed
            document.setStatus(DocumentStatus.COMPLETED);
            document.setChunkCount(chunks.size());
            document.setProcessedTime(LocalDateTime.now());
            documentRepository.save(document);
            
            log.info("Document processing completed, ID: {}, chunk count: {}", documentId, chunks.size());
            
        } catch (Exception e) {
            log.error("Document processing failed, ID: {}, error: {}", documentId, e.getMessage(), e);
            
            // Update status to failed and record error message
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage(e.getMessage());
            documentRepository.save(document);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new DocumentNotFoundException(id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable)
            .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByStatus(String status) {
        DocumentStatus documentStatus = DocumentStatus.valueOf(status.toUpperCase());
        return documentRepository.findByStatus(documentStatus).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteDocument(Long id) {
        log.info("Deleting document, ID: {}", id);
        
        Document document = getDocumentById(id);
        
        try {
            // Delete file
            Path filePath = Paths.get(document.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted: {}", filePath);
            }
            
            // Delete database record (chunks will be cascade deleted)
            documentRepository.delete(document);
            log.info("Document record deleted, ID: {}", id);
            
        } catch (IOException e) {
            log.error("File deletion failed: {}", e.getMessage(), e);
            // Even if file deletion fails, still delete database record
            documentRepository.delete(document);
        }
    }
    
    @Override
    @Transactional
    public DocumentResponse reprocessDocument(Long id) {
        log.info("Reprocessing document, ID: {}", id);
        
        Document document = getDocumentById(id);
        
        // Delete existing chunks
        documentChunkRepository.deleteByDocumentId(id);
        log.info("Existing chunks deleted");
        
        // Reset document status
        document.setStatus(DocumentStatus.PENDING);
        document.setChunkCount(0);
        document.setErrorMessage(null);
        document.setProcessedTime(null);
        documentRepository.save(document);
        
        // Trigger asynchronous processing
        processDocumentAsync(id);
        
        return convertToResponse(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDocumentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total document count
        long totalDocuments = documentRepository.count();
        stats.put("totalDocuments", totalDocuments);
        
        // Count by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (DocumentStatus status : DocumentStatus.values()) {
            long count = documentRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }
        stats.put("statusCounts", statusCounts);
        
        // Total chunk count
        long totalChunks = documentChunkRepository.count();
        stats.put("totalChunks", totalChunks);
        
        // Average chunks per document
        double avgChunks = totalDocuments > 0 ? 
            (double) totalChunks / totalDocuments : 0;
        stats.put("averageChunksPerDocument", Math.round(avgChunks * 100.0) / 100.0);
        
        return stats;
    }
    
    /**
     * Generate unique filename
     * 
     * @param originalFilename Original filename
     * @return Unique filename
     */
    private String generateUniqueFilename(String originalFilename) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = "";
        
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        
        return timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
    }
    
    /**
     * Convert Document entity to DocumentResponse DTO
     * 
     * @param document Document entity
     * @return DocumentResponse DTO
     */
    private DocumentResponse convertToResponse(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setFilename(document.getFilename());
        response.setFileSize(document.getFileSize());
        response.setStatus(document.getStatus());
        response.setChunkCount(document.getChunkCount());
        response.setUploadTime(document.getUploadTime());
        response.setProcessedTime(document.getProcessedTime());
        response.setErrorMessage(document.getErrorMessage());
        
        return response;
    }
}
