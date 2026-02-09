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
public class EvSubsidyDaily {

    private Long id;
    private LocalDate targetDate;
    private String sido;
    private String region;
    private Integer dailyReceived;
    private Integer dailyDelivered;
    private Integer dailyRemainingChange;
    private LocalDateTime createdAt;
}
