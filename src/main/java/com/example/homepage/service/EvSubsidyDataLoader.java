package com.example.homepage.service;

import com.example.homepage.dto.EvSubsidyData;
import com.example.homepage.entity.EvSubsidy;
import com.example.homepage.mapper.EvSubsidyMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 파일에서 전기차 보조금 데이터를 읽어서 DB에 저장하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvSubsidyDataLoader {

    private final EvSubsidyMapper subsidyMapper;
    private final ObjectMapper objectMapper;
    
    private static final String DATA_FOLDER = "data/ev_subsidy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * JSON 파일을 읽어서 DB에 저장
     */
    @Transactional
    public int loadFromJsonFile(String filename) {
        try {
            File file = new File(DATA_FOLDER, filename);
            if (!file.exists()) {
                log.error("파일을 찾을 수 없습니다: {}", file.getAbsolutePath());
                return 0;
            }
            
            log.info("JSON 파일 로딩 시작: {}", filename);
            
            JsonNode root = objectMapper.readTree(file);
            String dateStr = root.get("date").asText();
            LocalDate crawlDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            
            return loadJsonData(root, crawlDate);
            
        } catch (Exception e) {
            log.error("JSON 파일 로딩 오류: {}", filename, e);
            return 0;
        }
    }

    /**
     * JSON 파일을 특정 날짜로 읽어서 DB에 저장
     */
    @Transactional
    public int loadFromJsonFileWithDate(String filename, LocalDate targetDate) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                log.error("파일을 찾을 수 없습니다: {}", file.getAbsolutePath());
                return 0;
            }
            
            log.info("JSON 파일 로딩 시작: {} (날짜: {})", filename, targetDate);
            
            JsonNode root = objectMapper.readTree(file);
            return loadJsonData(root, targetDate);
            
        } catch (Exception e) {
            log.error("JSON 파일 로딩 오류: {}", filename, e);
            return 0;
        }
    }

    /**
     * JSON 데이터를 파싱하여 DB에 저장
     */
    private int loadJsonData(JsonNode root, LocalDate crawlDate) {
        try {
            JsonNode dataArray = root.get("data");
            int savedCount = 0;
            
            for (JsonNode node : dataArray) {
                String carType = node.has("carType") ? node.get("carType").asText() : "";
                
                // 새로운 형식 (announced, received, delivered 객체)
                if (node.has("announced") && node.has("received") && node.has("delivered")) {
                    JsonNode announced = node.get("announced");
                    JsonNode received = node.get("received");
                    JsonNode delivered = node.get("delivered");
                    
                    EvSubsidyData data = EvSubsidyData.builder()
                            .sido(node.get("sido").asText())
                            .region(node.get("region").asText())
                            .carType(carType)
                            .totalAnnounced(announced.get("total").asInt())
                            .priorityAnnounced(announced.get("priority").asInt())
                            .corporationAnnounced(announced.get("corporation").asInt())
                            .taxiAnnounced(announced.get("taxi").asInt())
                            .generalAnnounced(announced.get("general").asInt())
                            .totalReceived(received.get("total").asInt())
                            .priorityReceived(received.get("priority").asInt())
                            .corporationReceived(received.get("corporation").asInt())
                            .taxiReceived(received.get("taxi").asInt())
                            .generalReceived(received.get("general").asInt())
                            .totalDelivered(delivered.get("total").asInt())
                            .priorityDelivered(delivered.get("priority").asInt())
                            .corporationDelivered(delivered.get("corporation").asInt())
                            .taxiDelivered(delivered.get("taxi").asInt())
                            .generalDelivered(delivered.get("general").asInt())
                            .build();
                    
                    if (saveData(crawlDate, data)) {
                        savedCount++;
                    }
                } else {
                    // 기존 형식 (하위 호환성)
                    EvSubsidyData data = EvSubsidyData.builder()
                            .sido(node.get("sido").asText())
                            .region(node.get("region").asText())
                            .carType(carType)
                            .totalAnnounced(node.get("totalAnnounced").asInt())
                            .totalReceived(node.get("totalReceived").asInt())
                            .totalDelivered(node.get("totalDelivered").asInt())
                            .priorityAnnounced(0)
                            .corporationAnnounced(0)
                            .taxiAnnounced(0)
                            .generalAnnounced(0)
                            .priorityReceived(0)
                            .corporationReceived(0)
                            .taxiReceived(0)
                            .generalReceived(0)
                            .priorityDelivered(0)
                            .corporationDelivered(0)
                            .taxiDelivered(0)
                            .generalDelivered(0)
                            .build();
                    
                    if (saveData(crawlDate, data)) {
                        savedCount++;
                    }
                }
            }
            
            log.info("JSON 데이터 로딩 완료: {} 개 저장", savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("JSON 데이터 로딩 오류", e);
            return 0;
        }
    }

    /**
     * 오늘 날짜의 최신 JSON 파일을 자동으로 찾아서 로딩
     */
    @Transactional
    public int loadTodayLatestData() {
        String today = LocalDate.now().format(DATE_FORMATTER);
        File folder = new File(DATA_FOLDER);
        
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("데이터 폴더를 찾을 수 없습니다: {}", DATA_FOLDER);
            return 0;
        }
        
        // 오늘 날짜의 파일 중 가장 큰 시퀀스 번호 찾기
        File[] files = folder.listFiles((dir, name) -> 
            name.startsWith(today + "_") && name.endsWith(".json"));
        
        if (files == null || files.length == 0) {
            log.warn("오늘 날짜의 JSON 파일이 없습니다: {}", today);
            return 0;
        }
        
        // 가장 최신 파일 (시퀀스 번호가 가장 큰 파일)
        File latestFile = files[0];
        for (File file : files) {
            if (file.getName().compareTo(latestFile.getName()) > 0) {
                latestFile = file;
            }
        }
        
        log.info("최신 파일 선택: {}", latestFile.getName());
        return loadFromJsonFile(latestFile.getName());
    }

    /**
     * 특정 날짜의 모든 JSON 파일 로딩
     */
    @Transactional
    public int loadAllDataByDate(LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        File folder = new File(DATA_FOLDER);
        
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("데이터 폴더를 찾을 수 없습니다: {}", DATA_FOLDER);
            return 0;
        }
        
        File[] files = folder.listFiles((dir, name) -> 
            name.startsWith(dateStr + "_") && name.endsWith(".json"));
        
        if (files == null || files.length == 0) {
            log.warn("해당 날짜의 JSON 파일이 없습니다: {}", dateStr);
            return 0;
        }
        
        int totalSaved = 0;
        for (File file : files) {
            totalSaved += loadFromJsonFile(file.getName());
        }
        
        return totalSaved;
    }

    private boolean saveData(LocalDate crawlDate, EvSubsidyData data) {
        try {
            // 중복 체크
            EvSubsidy existing = subsidyMapper.findByCrawlDateAndSidoAndRegionAndCarType(
                    crawlDate, data.getSido(), data.getRegion(), data.getCarType());
            
            if (existing != null) {
                log.debug("이미 존재하는 데이터: {} - {} - {} ({})", 
                        crawlDate, data.getSido(), data.getRegion(), data.getCarType());
                return false;
            }
            
            EvSubsidy subsidy = EvSubsidy.builder()
                    .crawlDate(crawlDate)
                    .sido(data.getSido())
                    .region(data.getRegion())
                    .carType(data.getCarType())
                    .dataType("today")
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
            
            subsidyMapper.insert(subsidy);
            return true;
            
        } catch (Exception e) {
            log.error("데이터 저장 오류: {} - {}", data.getSido(), data.getRegion(), e);
            return false;
        }
    }

    /**
     * 사용 가능한 모든 JSON 파일 목록 조회
     */
    public List<String> getAvailableJsonFiles() {
        File folder = new File(DATA_FOLDER);
        List<String> fileList = new ArrayList<>();
        
        if (!folder.exists() || !folder.isDirectory()) {
            return fileList;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        
        fileList.sort(String::compareTo);
        return fileList;
    }
}
