package com.java.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmResponse {
    private Boolean success;
    private String content;
    private String response;  // Alias for content
    private String model;
    private Integer tokensUsed;
    private Map<String, Object> usage;
    private String error;
    private LocalDateTime generatedAt;
}
