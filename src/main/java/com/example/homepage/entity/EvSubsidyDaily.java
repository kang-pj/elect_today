package com.example.homepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ev_subsidy_daily",
       uniqueConstraints = @UniqueConstraint(columnNames = {"target_date", "sido", "region"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvSubsidyDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false, length = 50)
    private String sido;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(name = "daily_received", nullable = false)
    private Integer dailyReceived;

    @Column(name = "daily_delivered", nullable = false)
    private Integer dailyDelivered;

    @Column(name = "daily_remaining_change", nullable = false)
    private Integer dailyRemainingChange;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
