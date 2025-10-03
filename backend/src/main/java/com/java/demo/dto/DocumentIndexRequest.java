package com.java.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIndexRequest {
    @NotBlank(message = "Content is required")
    private String content;
    
    private String document;  // Alias for content
    private String documentId;
    private String docId;  // Alias for documentId
    private String title;
    private String source;
    private List<String> tags;
    private Map<String, Object> metadata;
    
    // Helper method to get document ID from either field
    public String getDocId() {
        return docId != null ? docId : documentId;
    }
    
    // Helper method to get content from either field
    public String getContent() {
        return content != null ? content : document;
    }
}
