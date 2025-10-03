package com.java.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagQueryRequest {
    @NotBlank(message = "Query is required")
    private String query;
    
    private String question;  // Alias for query
    private Integer topK = 5;
    private Double minScore = 0.7;
    private String filter;
    
    // Helper method to get question from either field
    public String getQuestion() {
        return question != null ? question : query;
    }
}
