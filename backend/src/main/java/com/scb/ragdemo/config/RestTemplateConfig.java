package com.scb.ragdemo.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate Configuration
 * Configures HTTP client for external API calls, primarily for Poe API integration
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Create RestTemplate Bean
     * Configures connection timeout, read timeout and request factory
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(
                        new SimpleClientHttpRequestFactory()))
                .build();
    }
}
