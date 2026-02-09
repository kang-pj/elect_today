package com.example.homepage.controller;

import com.example.homepage.entity.EvSubsidy;
import com.example.homepage.entity.EvSubsidyDaily;
import com.example.homepage.service.EvSubsidyDataLoader;
import com.example.homepage.service.EvSubsidyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ev-subsidy")
@RequiredArgsConstructor
public class EvSubsidyController {

    private final EvSubsidyService subsidyService;
    private final EvSubsidyDataLoader dataLoader;

    /**
     * 크롤링 실행 (실제 사이트에서 데이터 수집)
     */
    @PostMapping("/crawl")
    public ResponseEntity<Map<String, Object>> crawlData() {
        log.info("크롤링 요청 수신");
        
        try {
            int count = subsidyService.crawlAndSaveData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "크롤링 완료");
            response.put("savedCount", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("크롤링 오류", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * JSON 파일에서 데이터 로딩
     */
    @PostMapping("/load-json")
    public ResponseEntity<Map<String, Object>> loadFromJson(
            @RequestParam(required = false) String filename) {
        
        log.info("JSON 로딩 요청: {}", filename);
        
        try {
            int count;
            if (filename != null && !filename.isEmpty()) {
                count = dataLoader.loadFromJsonFile(filename);
            } else {
                count = dataLoader.loadTodayLatestData();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "데이터 로딩 완료");
            response.put("savedCount", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("JSON 로딩 오류", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로딩 실패: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 사용 가능한 JSON 파일 목록 조회
     */
    @GetMapping("/json-files")
    public ResponseEntity<List<String>> getJsonFiles() {
        List<String> files = dataLoader.getAvailableJsonFiles();
        return ResponseEntity.ok(files);
    }

    /**
     * 오늘 데이터 조회
     */
    @GetMapping("/today")
    public ResponseEntity<List<EvSubsidy>> getTodayData() {
        List<EvSubsidy> data = subsidyService.getTodayData();
        return ResponseEntity.ok(data);
    }

    /**
     * 오늘 일일 변화량 조회
     */
    @GetMapping("/today/daily")
    public ResponseEntity<List<EvSubsidyDaily>> getTodayDailyData() {
        List<EvSubsidyDaily> data = subsidyService.getTodayDailyData();
        return ResponseEntity.ok(data);
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", LocalDate.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 지역의 시계열 데이터 조회
     */
    @GetMapping("/region-stats")
    public ResponseEntity<Map<String, Object>> getRegionStats(
            @RequestParam String sido,
            @RequestParam String region) {
        
        log.info("지역 통계 조회: {} - {}", sido, region);
        
        try {
            Map<String, Object> stats = subsidyService.getRegionStats(sido, region);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("지역 통계 조회 오류", e);
            return ResponseEntity.status(500).body(Map.of("error", "데이터 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 특정 지역 실시간 크롤링 및 업데이트
     */
    @PostMapping("/update-realtime")
    public ResponseEntity<Map<String, Object>> updateRealtime(
            @RequestParam String sido,
            @RequestParam String region) {
        
        log.info("실시간 데이터 업데이트 요청: {} - {}", sido, region);
        
        try {
            Map<String, Object> result = subsidyService.updateRealtimeData(sido, region);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("실시간 업데이트 오류", e);
            return ResponseEntity.status(500).body(Map.of("error", "업데이트 중 오류가 발생했습니다."));
        }
    }
}
