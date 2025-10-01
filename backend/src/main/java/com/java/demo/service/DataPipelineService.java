package com.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPipelineService {

    private final RestTemplate restTemplate;

    @Value("${airflow.url:http://localhost:8080}")
    private String airflowUrl;

    @Value("${airflow.username:admin}")
    private String airflowUsername;

    @Value("${airflow.password:admin}")
    private String airflowPassword;

    public Map<String, Object> triggerEtlDag(String date) {
        String dagId = "etl_conversation_pipeline";
        String url = String.format("%s/api/v1/dags/%s/dagRuns", airflowUrl, dagId);
        
        HttpHeaders headers = createAirflowHeaders();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("conf", Map.of("execution_date", 
            date != null ? date : LocalDate.now().toString()));
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            Map<String, Object> result = response.getBody();
            result.put("triggeredAt", LocalDateTime.now());
            
            return result;
            
        } catch (Exception e) {
            log.error("Error triggering ETL DAG: {}", e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    public Map<String, Object> triggerDataQualityDag() {
        String dagId = "data_quality_dag";
        String url = String.format("%s/api/v1/dags/%s/dagRuns", airflowUrl, dagId);
        
        HttpHeaders headers = createAirflowHeaders();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
            Map.of("conf", Map.of()), headers
        );
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error triggering data quality DAG: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    public Map<String, Object> getDagRunStatus(String dagId, String runId) {
        String url = String.format("%s/api/v1/dags/%s/dagRuns/%s", 
            airflowUrl, dagId, runId);
        
        HttpHeaders headers = createAirflowHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error getting DAG run status: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    public Map<String, Object> getDagRuns(String dagId, int limit) {
        String url = String.format("%s/api/v1/dags/%s/dagRuns?limit=%d", 
            airflowUrl, dagId, limit);
        
        HttpHeaders headers = createAirflowHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error getting DAG runs: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    public Map<String, Object> getPipelineMetrics() {
        // 实际实现应该聚合多个 DAG 的指标
        return Map.of(
            "totalPipelineRuns", 150,
            "successRate", 0.95,
            "averageRuntime", "5.2 minutes",
            "lastRunAt", LocalDateTime.now().minusHours(2),
            "activePipelines", List.of(
                "etl_conversation_pipeline",
                "data_quality_dag",
                "data_ingestion_dag"
            )
        );
    }

    private HttpHeaders createAirflowHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String auth = airflowUsername + ":" + airflowPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        
        return headers;
    }
}