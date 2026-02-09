package com.example.homepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ev_subsidy", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"crawl_date", "sido", "region", "car_type"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvSubsidy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crawl_date", nullable = false)
    private LocalDate crawlDate;

    @Column(nullable = false, length = 50)
    private String sido;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(name = "car_type", length = 50)
    private String carType;

    @Column(name = "data_type", length = 20, nullable = false)
    private String dataType = "today"; // today: 00시 배치, realtime: 실시간

    // 공고 대수
    @Column(name = "total_announced", nullable = false)
    private Integer totalAnnounced;
    
    @Column(name = "priority_announced")
    private Integer priorityAnnounced;
    
    @Column(name = "corporation_announced")
    private Integer corporationAnnounced;
    
    @Column(name = "taxi_announced")
    private Integer taxiAnnounced;
    
    @Column(name = "general_announced")
    private Integer generalAnnounced;

    // 접수 대수
    @Column(name = "total_received", nullable = false)
    private Integer totalReceived;
    
    @Column(name = "priority_received")
    private Integer priorityReceived;
    
    @Column(name = "corporation_received")
    private Integer corporationReceived;
    
    @Column(name = "taxi_received")
    private Integer taxiReceived;
    
    @Column(name = "general_received")
    private Integer generalReceived;

    // 출고 대수
    @Column(name = "total_delivered", nullable = false)
    private Integer totalDelivered;
    
    @Column(name = "priority_delivered")
    private Integer priorityDelivered;
    
    @Column(name = "corporation_delivered")
    private Integer corporationDelivered;
    
    @Column(name = "taxi_delivered")
    private Integer taxiDelivered;
    
    @Column(name = "general_delivered")
    private Integer generalDelivered;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
