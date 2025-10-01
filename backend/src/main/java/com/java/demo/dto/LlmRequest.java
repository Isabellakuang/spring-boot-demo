package com.java.demo.dto;

import lombok.Data;

@Data
public class LlmRequest {
    private String prompt;
    private String model = "gpt-3.5-turbo";
    private double temperature = 0.7;
    private int maxTokens = 1000;
}