package com.java.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagQueryResponse {
    private String answer;
    private List<Map<String, Object>> sources;
    private List<RetrievedDocument> retrievedDocuments;
    private Integer totalSources;
    private String model;
    private Double confidence;
    private Map<String, Object> usage;
    private LocalDateTime queriedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetrievedDocument {
        private String documentId;
        private String content;
        private String title;
        private String source;
        private Double score;
    }
}
