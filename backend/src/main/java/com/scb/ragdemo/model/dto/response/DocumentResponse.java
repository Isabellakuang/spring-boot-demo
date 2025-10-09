package com.scb.ragdemo.model.dto.response;

import com.scb.ragdemo.model.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Document Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    /**
     * Document ID
     */
    private Long id;

    /**
     * File name
     */
    private String filename;

    /**
     * File size (bytes)
     */
    private Long fileSize;

    /**
     * Upload time
     */
    private LocalDateTime uploadTime;

    /**
     * Processing completion time
     */
    private LocalDateTime processedTime;

    /**
     * Document status
     */
    private DocumentStatus status;

    /**
     * Number of chunks
     */
    private Integer chunkCount;

    /**
     * Error message (only present when processing fails)
     */
    private String errorMessage;

    /**
     * Formatted file size for display (e.g., "2.5 MB")
     */
    private String formattedFileSize;
}
