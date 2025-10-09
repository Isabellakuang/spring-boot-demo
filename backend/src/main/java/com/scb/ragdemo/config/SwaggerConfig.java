package com.scb.ragdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Configures API documentation
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SCB RAG Demo API")
                        .version("1.0.0")
                        .description("Retrieval-Augmented Generation (RAG) Demo API Documentation - Supports PDF document upload, processing, vector storage and intelligent Q&A based on document content")
                        .contact(new Contact()
                                .name("SCB RAG Demo Team")
                                .email("support@scb-rag-demo.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Environment"),
                        new Server()
                                .url("https://api.scb-rag-demo.com")
                                .description("Production Environment")));
    }
}
