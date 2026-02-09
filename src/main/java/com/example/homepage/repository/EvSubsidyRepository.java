package com.example.homepage.repository;

import com.example.homepage.entity.EvSubsidy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvSubsidyRepository extends JpaRepository<EvSubsidy, Long> {
    List<EvSubsidy> findByCrawlDate(LocalDate crawlDate);
    Optional<EvSubsidy> findByCrawlDateAndSidoAndRegion(LocalDate crawlDate, String sido, String region);
    Optional<EvSubsidy> findByCrawlDateAndSidoAndRegionAndCarType(LocalDate crawlDate, String sido, String region, String carType);
    List<EvSubsidy> findBySidoAndRegionOrderByCrawlDateDesc(String sido, String region);
    List<EvSubsidy> findBySidoAndRegionOrderByCrawlDateAsc(String sido, String region);
    
    // type별 조회
    List<EvSubsidy> findByCrawlDateAndDataType(LocalDate crawlDate, String dataType);
    Optional<EvSubsidy> findByCrawlDateAndSidoAndRegionAndDataType(LocalDate crawlDate, String sido, String region, String dataType);
    List<EvSubsidy> findBySidoAndRegionAndDataTypeOrderByCrawlDateAsc(String sido, String region, String dataType);
}
