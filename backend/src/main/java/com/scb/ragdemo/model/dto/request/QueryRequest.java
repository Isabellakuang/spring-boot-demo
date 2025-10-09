package com.scb.ragdemo.model.dto.request;

import com.scb.ragdemo.model.enums.QueryMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    /**
     * Query question content
     */
    @NotBlank(message = "Query question cannot be empty")
    @Size(min = 1, max = 1000, message = "Query question length must be between 1-1000 characters")
    private String question;

    /**
     * Query mode (NLP or RAG)
     * If not specified, the system will automatically select the appropriate mode
     */
    private QueryMode mode;

    /**
     * Session ID for tracking conversation context
     */
    private String sessionId;

    /**
     * Maximum number of source references to return (only effective in RAG mode)
     */
    @Builder.Default
    private Integer maxSources = 3;
}
