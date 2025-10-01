package com.java.demo.controller;

import com.java.demo.service.DataPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/data-pipeline")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Pipeline", description = "数据管道管理 API")
@SecurityRequirement(name = "bearer-jwt")
public class DataPipelineController {

    private final DataPipelineService dataPipelineService;

    @PostMapping("/trigger/etl")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "触发 ETL 流程", description = "手动触发 Airflow ETL DAG")
    public ResponseEntity<Map<String, Object>> triggerEtl(
            @RequestParam(required = false) String date) {
        log.info("Triggering ETL pipeline for date: {}", date);
        
        Map<String, Object> result = dataPipelineService.triggerEtlDag(date);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/trigger/data-quality")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "触发数据质量检查")
    public ResponseEntity<Map<String, Object>> triggerDataQuality() {
        log.info("Triggering data quality check");
        
        Map<String, Object> result = dataPipelineService.triggerDataQualityDag();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{dagId}/{runId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取 DAG 运行状态")
    public ResponseEntity<Map<String, Object>> getDagRunStatus(
            @PathVariable String dagId,
            @PathVariable String runId) {
        Map<String, Object> status = dataPipelineService.getDagRunStatus(dagId, runId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/runs/{dagId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取 DAG 运行历史")
    public ResponseEntity<Map<String, Object>> getDagRuns(
            @PathVariable String dagId,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> runs = dataPipelineService.getDagRuns(dagId, limit);
        return ResponseEntity.ok(runs);
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取管道指标")
    public ResponseEntity<Map<String, Object>> getPipelineMetrics() {
        Map<String, Object> metrics = dataPipelineService.getPipelineMetrics();
        return ResponseEntity.ok(metrics);
    }
}