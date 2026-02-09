package com.example.homepage.crawler;

import com.example.homepage.dto.EvSubsidyData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Python 크롤러를 Java에서 호출하는 래퍼 클래스
 */
@Slf4j
@Component
public class PythonCrawlerWrapper {

    private static final String PYTHON_SCRIPT_PATH = "../auto_e_c/ev_crawler.py";
    private static final String PYTHON_OUTPUT_DIR = "../auto_e_c/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Python 크롤러 실행
     */
    public List<EvSubsidyData> executePythonCrawler() {
        try {
            log.info("Python 크롤러 실행 시작");
            
            // Python 스크립트 실행
            ProcessBuilder processBuilder = new ProcessBuilder("python3", PYTHON_SCRIPT_PATH);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
            // 출력 읽기
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("Python: {}", line);
            }
            
            int exitCode = process.waitFor();
            log.info("Python 크롤러 종료 코드: {}", exitCode);
            
            if (exitCode != 0) {
                log.error("Python 크롤러 실행 실패");
                return new ArrayList<>();
            }
            
            // 가장 최근 생성된 JSON 파일 찾기
            File outputDir = new File(PYTHON_OUTPUT_DIR);
            File[] jsonFiles = outputDir.listFiles((dir, name) -> 
                name.startsWith("ev_subsidy_") && name.endsWith(".json")
            );
            
            if (jsonFiles == null || jsonFiles.length == 0) {
                log.error("생성된 JSON 파일을 찾을 수 없습니다");
                return new ArrayList<>();
            }
            
            // 가장 최근 파일 선택
            File latestFile = jsonFiles[0];
            for (File file : jsonFiles) {
                if (file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }
            
            log.info("JSON 파일 읽기: {}", latestFile.getName());
            
            // JSON 파일 파싱
            return parseJsonFile(latestFile);
            
        } catch (Exception e) {
            log.error("Python 크롤러 실행 오류", e);
            return new ArrayList<>();
        }
    }

    /**
     * Python 크롤러가 생성한 JSON 파일 파싱
     */
    private List<EvSubsidyData> parseJsonFile(File jsonFile) {
        List<EvSubsidyData> results = new ArrayList<>();
        
        try {
            JsonNode rootArray = objectMapper.readTree(jsonFile);
            
            for (JsonNode node : rootArray) {
                String sido = node.get("시도").asText();
                String region = node.get("지역구분").asText();
                
                JsonNode announced = node.get("공고대수");
                JsonNode received = node.get("접수대수");
                
                int totalAnnounced = announced.get("전체").asInt();
                int totalReceived = received.get("전체").asInt();
                
                // 출고는 Python 데이터에 없으므로 0으로 설정
                int totalDelivered = 0;
                
                EvSubsidyData data = EvSubsidyData.builder()
                    .sido(sido)
                    .region(region)
                    .carType("")
                    .totalAnnounced(totalAnnounced)
                    .totalReceived(totalReceived)
                    .totalDelivered(totalDelivered)
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
                
                results.add(data);
            }
            
            log.info("총 {}개 데이터 파싱 완료", results.size());
            
        } catch (Exception e) {
            log.error("JSON 파일 파싱 오류", e);
        }
        
        return results;
    }

    /**
     * Python 크롤러 설치 확인
     */
    public boolean isPythonCrawlerAvailable() {
        File scriptFile = new File(PYTHON_SCRIPT_PATH);
        return scriptFile.exists();
    }

    /**
     * Python 환경 확인
     */
    public boolean checkPythonEnvironment() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.error("Python 환경 확인 실패", e);
            return false;
        }
    }
}
