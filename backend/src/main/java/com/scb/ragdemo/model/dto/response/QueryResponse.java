package com.scb.ragdemo.model.dto.response;

import com.scb.ragdemo.model.enums.QueryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Query Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    /**
     * Answer content
     */
    private String answer;

    /**
     * Query mode used
     */
    private QueryMode mode;

    /**
     * Source references (only present in RAG mode)
     */
    private List<SourceReference> sources;

    /**
     * Response time (milliseconds)
     */
    private Long responseTime;

    /**
     * Query history ID
     */
    private Long historyId;

    /**
     * Whether result is from cache
     */
    @Builder.Default
    private Boolean fromCache = false;
}
