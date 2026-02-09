package com.example.homepage.service;

import com.example.homepage.crawler.EvSubsidyCrawler;
import com.example.homepage.dto.EvSubsidyData;
import com.example.homepage.entity.EvSubsidy;
import com.example.homepage.entity.EvSubsidyDaily;
import com.example.homepage.repository.EvSubsidyDailyRepository;
import com.example.homepage.repository.EvSubsidyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvSubsidyService {

    private final EvSubsidyCrawler crawler;
    private final EvSubsidyRepository subsidyRepository;
    private final EvSubsidyDailyRepository dailyRepository;

    @Transactional
    public int crawlAndSaveData() {
        LocalDate today = LocalDate.now();
        log.info("크롤링 시작: {}", today);
        
        List<EvSubsidyData> crawledData = crawler.crawlSubsidyData();
        
        if (crawledData.isEmpty()) {
            log.warn("크롤링된 데이터가 없습니다.");
            return 0;
        }
        
        int savedCount = 0;
        int dailyCount = 0;
        
        for (EvSubsidyData data : crawledData) {
            // 1. 오늘 데이터 저장
            EvSubsidy subsidy = saveSubsidyData(today, data);
            if (subsidy != null) {
                savedCount++;
                
                // 2. 일일 변화량 계산 및 저장
                if (calculateAndSaveDailyChange(today, data)) {
                    dailyCount++;
                }
            }
        }
        
        log.info("크롤링 완료: {} 개 저장, {} 개 일일 데이터 생성", savedCount, dailyCount);
        return savedCount;
    }

    private EvSubsidy saveSubsidyData(LocalDate date, EvSubsidyData data) {
        try {
            // 중복 체크
            Optional<EvSubsidy> existing = subsidyRepository
                    .findByCrawlDateAndSidoAndRegionAndCarType(date, data.getSido(), data.getRegion(), data.getCarType());
            
            if (existing.isPresent()) {
                log.debug("이미 존재하는 데이터: {} - {} ({})", date, data.getSido(), data.getRegion());
                return existing.get();
            }
            
            EvSubsidy subsidy = EvSubsidy.builder()
                    .crawlDate(date)
                    .sido(data.getSido())
                    .region(data.getRegion())
                    .carType(data.getCarType())
                    .totalAnnounced(data.getTotalAnnounced())
                    .priorityAnnounced(data.getPriorityAnnounced())
                    .corporationAnnounced(data.getCorporationAnnounced())
                    .taxiAnnounced(data.getTaxiAnnounced())
                    .generalAnnounced(data.getGeneralAnnounced())
                    .totalReceived(data.getTotalReceived())
                    .priorityReceived(data.getPriorityReceived())
                    .corporationReceived(data.getCorporationReceived())
                    .taxiReceived(data.getTaxiReceived())
                    .generalReceived(data.getGeneralReceived())
                    .totalDelivered(data.getTotalDelivered())
                    .priorityDelivered(data.getPriorityDelivered())
                    .corporationDelivered(data.getCorporationDelivered())
                    .taxiDelivered(data.getTaxiDelivered())
                    .generalDelivered(data.getGeneralDelivered())
                    .build();
            
            return subsidyRepository.save(subsidy);
            
        } catch (Exception e) {
            log.error("데이터 저장 오류: {} - {}", data.getSido(), data.getRegion(), e);
            return null;
        }
    }

    private boolean calculateAndSaveDailyChange(LocalDate today, EvSubsidyData todayData) {
        try {
            // 어제 데이터 조회
            LocalDate yesterday = today.minusDays(1);
            Optional<EvSubsidy> yesterdayOpt = subsidyRepository
                    .findByCrawlDateAndSidoAndRegion(yesterday, todayData.getSido(), todayData.getRegion());
            
            if (yesterdayOpt.isEmpty()) {
                log.debug("어제 데이터 없음: {} - {}", todayData.getSido(), todayData.getRegion());
                return false;
            }
            
            EvSubsidy yesterdayData = yesterdayOpt.get();
            
            // 일일 변화량 계산
            int dailyReceived = todayData.getTotalReceived() - yesterdayData.getTotalReceived();
            int dailyDelivered = todayData.getTotalDelivered() - yesterdayData.getTotalDelivered();
            int todayRemaining = todayData.getTotalAnnounced() - todayData.getTotalReceived();
            int yesterdayRemaining = yesterdayData.getTotalAnnounced() - yesterdayData.getTotalReceived();
            int dailyRemainingChange = todayRemaining - yesterdayRemaining;
            
            // 중복 체크
            Optional<EvSubsidyDaily> existingDaily = dailyRepository
                    .findByTargetDateAndSidoAndRegion(today, todayData.getSido(), todayData.getRegion());
            
            if (existingDaily.isPresent()) {
                log.debug("이미 존재하는 일일 데이터: {} - {} ({})", today, todayData.getSido(), todayData.getRegion());
                return false;
            }
            
            EvSubsidyDaily daily = EvSubsidyDaily.builder()
                    .targetDate(today)
                    .sido(todayData.getSido())
                    .region(todayData.getRegion())
                    .dailyReceived(dailyReceived)
                    .dailyDelivered(dailyDelivered)
                    .dailyRemainingChange(dailyRemainingChange)
                    .build();
            
            dailyRepository.save(daily);
            log.info("일일 변화량 저장: {} - {} | 접수: {}, 출고: {}, 잔여: {}", 
                    today, todayData.getRegion(), dailyReceived, dailyDelivered, dailyRemainingChange);
            
            return true;
            
        } catch (Exception e) {
            log.error("일일 변화량 계산 오류: {} - {}", todayData.getSido(), todayData.getRegion(), e);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<EvSubsidy> getTodayData() {
        return subsidyRepository.findByCrawlDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<EvSubsidyDaily> getTodayDailyData() {
        return dailyRepository.findByTargetDate(LocalDate.now());
    }

    /**
     * 특정 지역의 시계열 통계 데이터 조회 (today 타입만)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRegionStats(String sido, String region) {
        // today 타입 데이터만 조회
        List<EvSubsidy> dataList = subsidyRepository.findBySidoAndRegionAndDataTypeOrderByCrawlDateAsc(sido, region, "today");
        
        if (dataList.isEmpty()) {
            return Map.of("error", "데이터가 없습니다.");
        }
        
        // 최신 데이터
        EvSubsidy latestData = dataList.get(dataList.size() - 1);
        
        // 날짜별 데이터 추출
        List<String> dates = dataList.stream()
                .map(data -> data.getCrawlDate().toString())
                .toList();
        
        List<Integer> announced = dataList.stream()
                .map(EvSubsidy::getTotalAnnounced)
                .toList();
        
        List<Integer> received = dataList.stream()
                .map(EvSubsidy::getTotalReceived)
                .toList();
        
        List<Integer> delivered = dataList.stream()
                .map(EvSubsidy::getTotalDelivered)
                .toList();
        
        // 잔여 계산
        List<Integer> remaining = dataList.stream()
                .map(data -> data.getTotalAnnounced() - data.getTotalReceived())
                .toList();
        
        // 카테고리별 데이터
        List<Integer> priorityAnnounced = dataList.stream()
                .map(EvSubsidy::getPriorityAnnounced)
                .toList();
        List<Integer> priorityReceived = dataList.stream()
                .map(EvSubsidy::getPriorityReceived)
                .toList();
        List<Integer> priorityDelivered = dataList.stream()
                .map(EvSubsidy::getPriorityDelivered)
                .toList();
        List<Integer> priorityRemaining = dataList.stream()
                .map(data -> data.getPriorityAnnounced() - data.getPriorityReceived())
                .toList();
        
        List<Integer> corporationAnnounced = dataList.stream()
                .map(EvSubsidy::getCorporationAnnounced)
                .toList();
        List<Integer> corporationReceived = dataList.stream()
                .map(EvSubsidy::getCorporationReceived)
                .toList();
        List<Integer> corporationDelivered = dataList.stream()
                .map(EvSubsidy::getCorporationDelivered)
                .toList();
        List<Integer> corporationRemaining = dataList.stream()
                .map(data -> data.getCorporationAnnounced() - data.getCorporationReceived())
                .toList();
        
        List<Integer> taxiAnnounced = dataList.stream()
                .map(EvSubsidy::getTaxiAnnounced)
                .toList();
        List<Integer> taxiReceived = dataList.stream()
                .map(EvSubsidy::getTaxiReceived)
                .toList();
        List<Integer> taxiDelivered = dataList.stream()
                .map(EvSubsidy::getTaxiDelivered)
                .toList();
        List<Integer> taxiRemaining = dataList.stream()
                .map(data -> data.getTaxiAnnounced() - data.getTaxiReceived())
                .toList();
        
        List<Integer> generalAnnounced = dataList.stream()
                .map(EvSubsidy::getGeneralAnnounced)
                .toList();
        List<Integer> generalReceived = dataList.stream()
                .map(EvSubsidy::getGeneralReceived)
                .toList();
        List<Integer> generalDelivered = dataList.stream()
                .map(EvSubsidy::getGeneralDelivered)
                .toList();
        List<Integer> generalRemaining = dataList.stream()
                .map(data -> data.getGeneralAnnounced() - data.getGeneralReceived())
                .toList();
        
        Map<String, Object> result = new HashMap<>();
        result.put("sido", sido);
        result.put("region", region);
        result.put("carType", latestData.getCarType());
        result.put("dates", dates);
        
        // 전체 데이터
        result.put("announced", announced);
        result.put("received", received);
        result.put("delivered", delivered);
        result.put("remaining", remaining);
        
        // 카테고리별 데이터
        Map<String, Object> priority = new HashMap<>();
        priority.put("announced", priorityAnnounced);
        priority.put("received", priorityReceived);
        priority.put("delivered", priorityDelivered);
        priority.put("remaining", priorityRemaining);
        result.put("priority", priority);
        
        Map<String, Object> corporation = new HashMap<>();
        corporation.put("announced", corporationAnnounced);
        corporation.put("received", corporationReceived);
        corporation.put("delivered", corporationDelivered);
        corporation.put("remaining", corporationRemaining);
        result.put("corporation", corporation);
        
        Map<String, Object> taxi = new HashMap<>();
        taxi.put("announced", taxiAnnounced);
        taxi.put("received", taxiReceived);
        taxi.put("delivered", taxiDelivered);
        taxi.put("remaining", taxiRemaining);
        result.put("taxi", taxi);
        
        Map<String, Object> general = new HashMap<>();
        general.put("announced", generalAnnounced);
        general.put("received", generalReceived);
        general.put("delivered", generalDelivered);
        general.put("remaining", generalRemaining);
        result.put("general", general);
        
        // 최신 데이터 상세
        Map<String, Object> latest = new HashMap<>();
        latest.put("totalAnnounced", latestData.getTotalAnnounced());
        latest.put("priorityAnnounced", latestData.getPriorityAnnounced());
        latest.put("corporationAnnounced", latestData.getCorporationAnnounced());
        latest.put("taxiAnnounced", latestData.getTaxiAnnounced());
        latest.put("generalAnnounced", latestData.getGeneralAnnounced());
        
        latest.put("totalReceived", latestData.getTotalReceived());
        latest.put("priorityReceived", latestData.getPriorityReceived());
        latest.put("corporationReceived", latestData.getCorporationReceived());
        latest.put("taxiReceived", latestData.getTaxiReceived());
        latest.put("generalReceived", latestData.getGeneralReceived());
        
        latest.put("totalDelivered", latestData.getTotalDelivered());
        latest.put("priorityDelivered", latestData.getPriorityDelivered());
        latest.put("corporationDelivered", latestData.getCorporationDelivered());
        latest.put("taxiDelivered", latestData.getTaxiDelivered());
        latest.put("generalDelivered", latestData.getGeneralDelivered());
        
        latest.put("remaining", latestData.getTotalAnnounced() - latestData.getTotalReceived());
        latest.put("crawlDate", latestData.getCrawlDate().toString());
        
        result.put("latest", latest);
        
        // realtime 데이터 조회
        Optional<EvSubsidy> realtimeOpt = subsidyRepository.findByCrawlDateAndSidoAndRegionAndDataType(
                LocalDate.now(), sido, region, "realtime");
        
        if (realtimeOpt.isPresent()) {
            EvSubsidy realtimeData = realtimeOpt.get();
            Map<String, Object> realtime = new HashMap<>();
            
            // 전체 데이터
            realtime.put("totalReceived", realtimeData.getTotalReceived());
            realtime.put("totalDelivered", realtimeData.getTotalDelivered());
            realtime.put("totalRemaining", realtimeData.getTotalAnnounced() - realtimeData.getTotalReceived());
            
            // 카테고리별 데이터
            realtime.put("priorityReceived", realtimeData.getPriorityReceived());
            realtime.put("priorityDelivered", realtimeData.getPriorityDelivered());
            realtime.put("priorityRemaining", realtimeData.getPriorityAnnounced() - realtimeData.getPriorityReceived());
            
            realtime.put("corporationReceived", realtimeData.getCorporationReceived());
            realtime.put("corporationDelivered", realtimeData.getCorporationDelivered());
            realtime.put("corporationRemaining", realtimeData.getCorporationAnnounced() - realtimeData.getCorporationReceived());
            
            realtime.put("taxiReceived", realtimeData.getTaxiReceived());
            realtime.put("taxiDelivered", realtimeData.getTaxiDelivered());
            realtime.put("taxiRemaining", realtimeData.getTaxiAnnounced() - realtimeData.getTaxiReceived());
            
            realtime.put("generalReceived", realtimeData.getGeneralReceived());
            realtime.put("generalDelivered", realtimeData.getGeneralDelivered());
            realtime.put("generalRemaining", realtimeData.getGeneralAnnounced() - realtimeData.getGeneralReceived());
            
            realtime.put("updatedAt", realtimeData.getCreatedAt().toString());
            
            // 오늘 증가량 (전체)
            int todayReceived = realtimeData.getTotalReceived() - latestData.getTotalReceived();
            int todayDelivered = realtimeData.getTotalDelivered() - latestData.getTotalDelivered();
            realtime.put("todayReceived", todayReceived);
            realtime.put("todayDelivered", todayDelivered);
            
            // 오늘 증가량 (카테고리별)
            realtime.put("todayPriorityReceived", realtimeData.getPriorityReceived() - latestData.getPriorityReceived());
            realtime.put("todayPriorityDelivered", realtimeData.getPriorityDelivered() - latestData.getPriorityDelivered());
            
            realtime.put("todayCorporationReceived", realtimeData.getCorporationReceived() - latestData.getCorporationReceived());
            realtime.put("todayCorporationDelivered", realtimeData.getCorporationDelivered() - latestData.getCorporationDelivered());
            
            realtime.put("todayTaxiReceived", realtimeData.getTaxiReceived() - latestData.getTaxiReceived());
            realtime.put("todayTaxiDelivered", realtimeData.getTaxiDelivered() - latestData.getTaxiDelivered());
            
            realtime.put("todayGeneralReceived", realtimeData.getGeneralReceived() - latestData.getGeneralReceived());
            realtime.put("todayGeneralDelivered", realtimeData.getGeneralDelivered() - latestData.getGeneralDelivered());
            
            result.put("realtime", realtime);
        }
        
        return result;
    }

    /**
     * 특정 지역 실시간 크롤링 및 업데이트
     */
    @Transactional
    public Map<String, Object> updateRealtimeData(String sido, String region) {
        log.info("실시간 데이터 업데이트: {} - {}", sido, region);
        
        // 1. 실시간 크롤링 실행
        List<EvSubsidyData> crawledData = crawler.crawlSubsidyData();
        
        // 2. 해당 지역 데이터 찾기
        EvSubsidyData realtimeData = crawledData.stream()
                .filter(data -> data.getSido().equals(sido) && data.getRegion().equals(region))
                .findFirst()
                .orElse(null);
        
        if (realtimeData == null) {
            return Map.of("error", "실시간 데이터를 찾을 수 없습니다.");
        }
        
        // 3. realtime 타입 데이터 업데이트 (있으면 update, 없으면 insert)
        LocalDate today = LocalDate.now();
        Optional<EvSubsidy> existingOpt = subsidyRepository.findByCrawlDateAndSidoAndRegionAndDataType(
                today, sido, region, "realtime");
        
        EvSubsidy entity;
        if (existingOpt.isPresent()) {
            // 업데이트
            entity = existingOpt.get();
            entity.setTotalReceived(realtimeData.getTotalReceived());
            entity.setPriorityReceived(realtimeData.getPriorityReceived());
            entity.setCorporationReceived(realtimeData.getCorporationReceived());
            entity.setTaxiReceived(realtimeData.getTaxiReceived());
            entity.setGeneralReceived(realtimeData.getGeneralReceived());
            
            entity.setTotalDelivered(realtimeData.getTotalDelivered());
            entity.setPriorityDelivered(realtimeData.getPriorityDelivered());
            entity.setCorporationDelivered(realtimeData.getCorporationDelivered());
            entity.setTaxiDelivered(realtimeData.getTaxiDelivered());
            entity.setGeneralDelivered(realtimeData.getGeneralDelivered());
            
            log.info("실시간 데이터 업데이트 완료");
        } else {
            // 신규 삽입
            entity = EvSubsidy.builder()
                    .crawlDate(today)
                    .sido(sido)
                    .region(region)
                    .carType(realtimeData.getCarType())
                    .dataType("realtime")
                    .totalAnnounced(realtimeData.getTotalAnnounced())
                    .priorityAnnounced(realtimeData.getPriorityAnnounced())
                    .corporationAnnounced(realtimeData.getCorporationAnnounced())
                    .taxiAnnounced(realtimeData.getTaxiAnnounced())
                    .generalAnnounced(realtimeData.getGeneralAnnounced())
                    .totalReceived(realtimeData.getTotalReceived())
                    .priorityReceived(realtimeData.getPriorityReceived())
                    .corporationReceived(realtimeData.getCorporationReceived())
                    .taxiReceived(realtimeData.getTaxiReceived())
                    .generalReceived(realtimeData.getGeneralReceived())
                    .totalDelivered(realtimeData.getTotalDelivered())
                    .priorityDelivered(realtimeData.getPriorityDelivered())
                    .corporationDelivered(realtimeData.getCorporationDelivered())
                    .taxiDelivered(realtimeData.getTaxiDelivered())
                    .generalDelivered(realtimeData.getGeneralDelivered())
                    .build();
            
            log.info("실시간 데이터 신규 삽입");
        }
        
        entity = subsidyRepository.save(entity);
        
        // 4. 00시 기준 데이터와 비교
        Optional<EvSubsidy> todayDataOpt = subsidyRepository.findByCrawlDateAndSidoAndRegionAndDataType(
                today, sido, region, "today");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("updatedAt", entity.getCreatedAt().toString());
        result.put("totalReceived", entity.getTotalReceived());
        result.put("totalDelivered", entity.getTotalDelivered());
        
        if (todayDataOpt.isPresent()) {
            EvSubsidy todayData = todayDataOpt.get();
            int todayReceived = entity.getTotalReceived() - todayData.getTotalReceived();
            int todayDelivered = entity.getTotalDelivered() - todayData.getTotalDelivered();
            
            result.put("todayReceived", todayReceived);
            result.put("todayDelivered", todayDelivered);
            
            log.info("오늘 신청: {}대, 오늘 출고: {}대", todayReceived, todayDelivered);
        }
        
        return result;
    }
}
