package com.java.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_quality_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String datasetName;

    @Column(nullable = false)
    private LocalDateTime checkTime;

    private double completenessScore;
    private double accuracyScore;
    private double consistencyScore;
    private double timelinessScore;
    private double overallScore;

    private boolean passed;

    @Column(length = 2000)
    private String issues;

    @Column(length = 1000)
    private String recommendations;
}