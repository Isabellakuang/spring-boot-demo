package com.scb.ragdemo.model.entity;

import com.scb.ragdemo.model.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Document Entity
 * Stores metadata and processing status of uploaded PDF documents
 */
@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Document filename
     */
    @Column(nullable = false, length = 255)
    private String filename;

    /**
     * File size (bytes)
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * Upload timestamp
     */
    @CreationTimestamp
    @Column(name = "upload_time", nullable = false, updatable = false)
    private LocalDateTime uploadTime;

    /**
     * Last update timestamp
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Processing completion timestamp
     */
    @Column(name = "processed_time")
    private LocalDateTime processedTime;

    /**
     * Document processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PROCESSING;

    /**
     * Number of chunks
     */
    @Column(name = "chunk_count")
    @Builder.Default
    private Integer chunkCount = 0;

    /**
     * File storage path on server
     */
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * Error message (if processing failed)
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /**
     * Associated document chunks
     * Cascade delete: when document is deleted, all chunks are automatically deleted
     */
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DocumentChunk> chunks = new ArrayList<>();
}
