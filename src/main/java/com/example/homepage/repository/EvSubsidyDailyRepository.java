package com.example.homepage.repository;

import com.example.homepage.entity.EvSubsidyDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvSubsidyDailyRepository extends JpaRepository<EvSubsidyDaily, Long> {
    List<EvSubsidyDaily> findByTargetDate(LocalDate targetDate);
    Optional<EvSubsidyDaily> findByTargetDateAndSidoAndRegion(LocalDate targetDate, String sido, String region);
    List<EvSubsidyDaily> findBySidoAndRegionOrderByTargetDateDesc(String sido, String region);
}
