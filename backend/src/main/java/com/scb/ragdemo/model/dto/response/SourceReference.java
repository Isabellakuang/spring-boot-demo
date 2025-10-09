package com.scb.ragdemo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Source Reference DTO
 * Used in RAG mode to return source document information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceReference {

    /**
     * Document chunk ID
     */
    private Long chunkId;

    /**
     * Document ID
     */
    private Long documentId;

    /**
     * Document filename
     */
    private String filename;

    /**
     * Chunk content
     */
    private String content;

    /**
     * Chunk index
     */
    private Integer chunkIndex;

    /**
     * Similarity score (0-1 range)
     */
    private Double similarity;
}
