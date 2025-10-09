package com.scb.ragdemo.model.entity;

import com.scb.ragdemo.model.enums.QueryMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Query History Entity
 * Records user query history for analysis and optimization
 */
@Entity
@Table(name = "query_history", indexes = {
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_query_type", columnList = "query_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Query text
     */
    @Column(name = "query_text", nullable = false, columnDefinition = "TEXT")
    private String queryText;

    /**
     * Response text
     */
    @Column(name = "response_text", nullable = false, columnDefinition = "TEXT")
    private String responseText;

    /**
     * Query mode (LLM/RAG)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false, length = 20)
    private QueryMode queryType;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Response time (milliseconds)
     */
    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    /**
     * Number of source documents (RAG mode only)
     */
    @Column(name = "source_count")
    private Integer sourceCount;

    /**
     * Session ID for tracking related queries
     */
    @Column(name = "session_id", length = 100)
    private String sessionId;
}
