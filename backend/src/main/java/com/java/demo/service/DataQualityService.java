package com.java.demo.service;

import com.java.demo.model.DataQualityMetric;
import com.java.demo.repository.DataQualityMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataQualityService {

    private final DataQualityMetricRepository metricRepository;

    /**
     * 运行数据质量检查
     */
    @Transactional
    public DataQualityMetric runQualityCheck(String datasetName) {
        log.info("Running quality check for dataset: {}", datasetName);
        
        DataQualityMetric metric = new DataQualityMetric();
        metric.setDatasetName(datasetName);
        metric.setCheckTime(LocalDateTime.now());
        
        // 完整性检查
        double completeness = checkCompleteness(datasetName);
        metric.setCompletenessScore(completeness);
        
        // 准确性检查
        double accuracy = checkAccuracy(datasetName);
        metric.setAccuracyScore(accuracy);
        
        // 一致性检查
        double consistency = checkConsistency(datasetName);
        metric.setConsistencyScore(consistency);
        
        // 及时性检查
        double timeliness = checkTimeliness(datasetName);
        metric.setTimelinessScore(timeliness);
        
        // 计算总体质量分数
        double overallScore = (completeness + accuracy + consistency + timeliness) / 4.0;
        metric.setOverallScore(overallScore);
        
        // 判断是否通过
        metric.setPassed(overallScore >= 0.8);
        
        if (!metric.isPassed()) {
            log.warn("Data quality check failed for {}: score={}", datasetName, overallScore);
        }
        
        return metricRepository.save(metric);
    }

    private double checkCompleteness(String datasetName) {
        // 检查数据完整性
        // 例如: 检查必填字段是否为空
        log.debug("Checking completeness for {}", datasetName);
        
        // 模拟检查逻辑
        long totalRecords = 1000L;
        long completeRecords = 950L;
        
        return (double) completeRecords / totalRecords;
    }

    private double checkAccuracy(String datasetName) {
        // 检查数据准确性
        // 例如: 检查数据格式、范围是否正确
        log.debug("Checking accuracy for {}", datasetName);
        
        // 模拟检查逻辑
        return 0.92;
    }

    private double checkConsistency(String datasetName) {
        // 检查数据一致性
        // 例如: 检查关联数据是否一致
        log.debug("Checking consistency for {}", datasetName);
        
        // 模拟检查逻辑
        return 0.88;
    }

    private double checkTimeliness(String datasetName) {
        // 检查数据及时性
        // 例如: 检查数据更新时间
        log.debug("Checking timeliness for {}", datasetName);
        
        // 模拟检查逻辑
        return 0.95;
    }

    /**
     * 获取数据质量趋势
     */
    public List<DataQualityMetric> getQualityTrend(String datasetName, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return metricRepository.findByDatasetNameAndCheckTimeAfter(datasetName, startTime);
    }

    /**
     * 定时数据质量检查任务
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行
    public void scheduledQualityCheck() {
        log.info("Running scheduled data quality checks");
        
        List<String> datasets = List.of(
            "conversations",
            "faq_entries",
            "users"
        );
        
        for (String dataset : datasets) {
            try {
                runQualityCheck(dataset);
            } catch (Exception e) {
                log.error("Error checking quality for dataset: {}", dataset, e);
            }
        }
    }

    /**
     * 获取数据质量仪表板数据
     */
    public Map<String, Object> getQualityDashboard() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        
        List<DataQualityMetric> recentMetrics = metricRepository.findByCheckTimeAfter(last24Hours);
        
        long totalChecks = recentMetrics.size();
        long passedChecks = recentMetrics.stream().filter(DataQualityMetric::isPassed).count();
        
        double averageScore = recentMetrics.stream()
            .mapToDouble(DataQualityMetric::getOverallScore)
            .average()
            .orElse(0.0);
        
        return Map.of(
            "totalChecks", totalChecks,
            "passedChecks", passedChecks,
            "failedChecks", totalChecks - passedChecks,
            "averageScore", averageScore,
            "passRate", totalChecks > 0 ? (double) passedChecks / totalChecks : 0.0,
            "recentMetrics", recentMetrics
        );
    }
}