package com.java.demo.controller;

import com.java.demo.model.DataQualityMetric;
import com.java.demo.service.DataQualityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data-quality")
@RequiredArgsConstructor
@Tag(name = "Data Quality", description = "数据质量监控接口")
@SecurityRequirement(name = "Bearer Authentication")
public class DataQualityController {

    private final DataQualityService dataQualityService;

    @PostMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "运行数据质量检查", description = "对指定数据集运行质量检查")
    public ResponseEntity<DataQualityMetric> runQualityCheck(
            @RequestParam String datasetName) {
        DataQualityMetric result = dataQualityService.runQualityCheck(datasetName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trend/{datasetName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取质量趋势", description = "获取指定数据集的质量趋势")
    public ResponseEntity<List<DataQualityMetric>> getQualityTrend(
            @PathVariable String datasetName,
            @RequestParam(defaultValue = "7") int days) {
        List<DataQualityMetric> trend = dataQualityService.getQualityTrend(datasetName, days);
        return ResponseEntity.ok(trend);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取质量仪表板", description = "获取数据质量仪表板统计信息")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = dataQualityService.getQualityDashboard();
        return ResponseEntity.ok(dashboard);
    }
}