package com.example.homepage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvSubsidyData {
    private String sido;
    private String region;
    private String carType;
    
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
}
