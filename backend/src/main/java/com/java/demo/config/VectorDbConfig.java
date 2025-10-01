package com.java.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VectorDbConfig {

    @Value("${pinecone.api-key}")
    private String apiKey;

    @Value("${pinecone.environment}")
    private String environment;

    @Value("${pinecone.index-name}")
    private String indexName;

    @Bean
    public RestTemplate pineconeRestTemplate() {
        return new RestTemplate();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getIndexName() {
        return indexName;
    }
}