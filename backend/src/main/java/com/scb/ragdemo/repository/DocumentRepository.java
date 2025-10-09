package com.scb.ragdemo.repository;

import com.scb.ragdemo.model.entity.Document;
import com.scb.ragdemo.model.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Document Repository
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find documents by status
     */
    List<Document> findByStatus(DocumentStatus status);

    /**
     * Find documents uploaded within a time range
     */
    List<Document> findByUploadTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find documents uploaded after a specific time
     */
    List<Document> findByUploadTimeAfter(LocalDateTime time);

    /**
     * Find documents by filename keyword
     */
    List<Document> findByFilenameContaining(String keyword);

    /**
     * Count documents by status
     */
    long countByStatus(DocumentStatus status);

    /**
     * Find all documents ordered by upload time descending
     */
    List<Document> findAllByOrderByUploadTimeDesc();
}
