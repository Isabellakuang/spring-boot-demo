package com.scb.ragdemo.repository;

import com.scb.ragdemo.model.entity.QueryHistory;
import com.scb.ragdemo.model.enums.QueryMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Query History Repository
 */
@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {

    /**
     * Find query history within a time range
     */
    List<QueryHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find query history by query type
     */
    List<QueryHistory> findByQueryType(QueryMode queryType);

    /**
     * Find query history by session ID
     */
    List<QueryHistory> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    /**
     * Find the most recent 10 query records
     */
    List<QueryHistory> findTop10ByOrderByCreatedAtDesc();

    /**
     * Count queries within a time range
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Count queries by query type
     */
    long countByQueryType(QueryMode queryType);

    /**
     * Calculate average response time
     */
    @Query("SELECT AVG(qh.responseTimeMs) FROM QueryHistory qh WHERE qh.queryType = :queryType")
    Double getAverageResponseTime(@Param("queryType") QueryMode queryType);

    /**
     * Find queries exceeding a response time threshold
     */
    List<QueryHistory> findByResponseTimeMsGreaterThan(Long threshold);

    /**
     * Paginated query of all history records, ordered by creation time descending
     */
    Page<QueryHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Paginated query by query type, ordered by creation time descending
     */
    Page<QueryHistory> findByQueryTypeOrderByCreatedAtDesc(QueryMode queryType, Pageable pageable);

    /**
     * Count queries after a specific time
     */
    @Query("SELECT COUNT(qh) FROM QueryHistory qh WHERE qh.createdAt >= :startTime")
    long countByCreatedAtAfter(@Param("startTime") LocalDateTime startTime);
}
