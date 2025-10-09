package com.scb.ragdemo.repository;

import com.scb.ragdemo.model.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Document Chunk Repository
 */
@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    /**
     * Find all chunks by document ID, ordered by chunk index
     */
    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);

    /**
     * Delete all chunks by document ID
     */
    void deleteByDocumentId(Long documentId);

    /**
     * Count chunks by document ID
     */
    long countByDocumentId(Long documentId);

    /**
     * Full-text search using PostgreSQL tsvector
     * Uses ts_rank for relevance scoring
     * Uses 'english' configuration to filter stop words like 'what', 'is', 'the', etc.
     */
    @Query(value = """
        SELECT dc.*, ts_rank(dc.content_tsv, plainto_tsquery('english', :query)) as rank
        FROM document_chunks dc
        WHERE dc.content_tsv @@ plainto_tsquery('english', :query)
        ORDER BY rank DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> searchByContentTsv(@Param("query") String query, @Param("limit") int limit);

    /**
     * Full-text search within a specific document using PostgreSQL tsvector
     * Uses 'english' configuration to filter stop words
     */
    @Query(value = """
        SELECT dc.*, ts_rank(dc.content_tsv, plainto_tsquery('english', :query)) as rank
        FROM document_chunks dc
        WHERE dc.document_id = :documentId
        AND dc.content_tsv @@ plainto_tsquery('english', :query)
        ORDER BY rank DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> searchByDocumentAndContent(
        @Param("documentId") Long documentId,
        @Param("query") String query,
        @Param("limit") int limit
    );

    /**
     * Simple keyword search in content (fallback method)
     */
    @Query("SELECT dc FROM DocumentChunk dc WHERE dc.content LIKE %:keyword% ORDER BY dc.id DESC")
    List<DocumentChunk> searchByContentContaining(@Param("keyword") String keyword);
}
