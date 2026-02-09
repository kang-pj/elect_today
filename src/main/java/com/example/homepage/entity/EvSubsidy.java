package com.example.homepage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvSubsidy {

    private Long id;
    private LocalDate crawlDate;
    private String sido;
    private String region;
    private String carType;
    
    @Builder.Default
    private String dataType = "today"; // today: 00시 배치, realtime: 실시간

    // 공고 대수
    private Integer totalAnnounced;
    private Integer priorityAnnounced;
    private Integer corporationAnnounced;
    private Integer taxiAnnounced;
    private Integer generalAnnounced;

    // 접수 대수
    private Integer totalReceived;
    private Integer priorityReceived;
    private Integer corporationReceived;
    private Integer taxiReceived;
    private Integer generalReceived;

    // 출고 대수
    private Integer totalDelivered;
    private Integer priorityDelivered;
    private Integer corporationDelivered;
    private Integer taxiDelivered;
    private Integer generalDelivered;

    private LocalDateTime createdAt;
}
