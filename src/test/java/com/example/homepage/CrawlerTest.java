package com.example.homepage;

import com.example.homepage.crawler.EvSubsidyCrawler;
import com.example.homepage.dto.EvSubsidyData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlerTest {

    public static void main(String[] args) {
        System.out.println("=== 전기차 보조금 크롤링 테스트 시작 ===\n");
        
        EvSubsidyCrawler crawler = new EvSubsidyCrawler();
        List<EvSubsidyData> dataList = crawler.crawlSubsidyData();
        
        if (dataList.isEmpty()) {
            System.out.println("크롤링된 데이터가 없습니다.");
            return;
        }
        
        System.out.println("총 " + dataList.size() + "개의 데이터 수집 완료\n");
        
        // 콘솔 출력
        System.out.println("=== 수집된 데이터 (처음 10개) ===");
        int displayCount = Math.min(10, dataList.size());
        for (int i = 0; i < displayCount; i++) {
            EvSubsidyData data = dataList.get(i);
            System.out.printf("%d. %s - %s (%s) | 공고: %d | 접수: %d | 출고: %d\n",
                    i + 1, data.getSido(), data.getRegion(), data.getCarType(),
                    data.getTotalAnnounced(), data.getTotalReceived(), data.getTotalDelivered());
        }
        
        // JSON 파일로 저장
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "ev_subsidy_" + timestamp + ".json";
            
            Map<String, Object> result = new HashMap<>();
            result.put("crawlTime", LocalDateTime.now().toString());
            result.put("totalCount", dataList.size());
            result.put("data", dataList);
            
            File file = new File(filename);
            mapper.writeValue(file, result);
            
            System.out.println("\n=== JSON 파일 저장 완료 ===");
            System.out.println("파일명: " + filename);
            System.out.println("경로: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("JSON 저장 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
