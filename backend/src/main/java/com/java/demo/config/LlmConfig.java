package com.java.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "demo.llm")
public class LlmConfig {
    private String openaiApiKey;
    private String openaiModel = "gpt-4o-mini";
    private String pineconeApiKey;
    private String pineconeEnvironment;
    private String pineconeIndex;
    private String mlServiceUrl = "http://localhost:8000";
}
