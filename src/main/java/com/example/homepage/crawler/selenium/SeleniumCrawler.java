package com.example.homepage.crawler.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class SeleniumCrawler {

    private static final String TARGET_URL = "https://ev.or.kr/nportal/buySupprt/initSubsidyPaymentCheckAction.do";
    private static final String DATA_FOLDER = "data/ev_subsidy";

    public static void main(String[] args) {
        System.out.println("=== Selenium 기반 전기차 보조금 크롤링 시작 ===\n");
        
        WebDriver driver = null;
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        try {
            // ChromeDriver 경로 설정 (시스템에 설치된 것 사용)
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            
            // Chrome 옵션 설정
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // 브라우저 창 숨기기
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            
            driver = new ChromeDriver(options);
            
            System.out.println("브라우저 시작 완료");
            System.out.println("URL 접속 중: " + TARGET_URL);
            
            driver.get(TARGET_URL);
            
            // 페이지 로딩 대기 (최대 10초)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // 테이블이 로드될 때까지 대기
            System.out.println("페이지 로딩 대기 중...");
            Thread.sleep(3000); // 3초 대기
            
            // 페이지 소스 저장
            String pageSource = driver.getPageSource();
            try (java.io.FileWriter writer = new java.io.FileWriter("selenium_page_source.html")) {
                writer.write(pageSource);
                System.out.println("페이지 소스 저장: selenium_page_source.html\n");
            }
            
            // 테이블 찾기 - 여러 셀렉터 시도
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            System.out.println("발견된 테이블 수: " + tables.size());
            
            if (tables.isEmpty()) {
                System.out.println("테이블을 찾을 수 없습니다.");
                
                // div나 다른 요소 확인
                List<WebElement> divs = driver.findElements(By.tagName("div"));
                System.out.println("발견된 div 수: " + divs.size());
                
                // 특정 클래스나 ID로 데이터 찾기 시도
                List<WebElement> dataElements = driver.findElements(By.cssSelector("[class*='table'], [class*='data'], [id*='table'], [id*='data']"));
                System.out.println("데이터 관련 요소 수: " + dataElements.size());
                
                if (!dataElements.isEmpty()) {
                    System.out.println("\n주요 데이터 요소:");
                    for (int i = 0; i < Math.min(5, dataElements.size()); i++) {
                        WebElement el = dataElements.get(i);
                        System.out.println("- " + el.getTagName() + " | class=" + el.getAttribute("class") + " | id=" + el.getAttribute("id"));
                    }
                }
                
                return;
            }
            
            // 데이터가 있는 테이블 찾기
            WebElement dataTable = null;
            for (WebElement table : tables) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                if (!rows.isEmpty()) {
                    dataTable = table;
                    break;
                }
            }
            
            if (dataTable == null) {
                dataTable = tables.get(0);
            }
            
            // tbody의 tr 요소들 파싱
            List<WebElement> rows = dataTable.findElements(By.cssSelector("tbody tr"));
            System.out.println("발견된 데이터 행 수: " + rows.size() + "\n");
            
            System.out.println("=== 수집된 데이터 ===");
            for (WebElement row : rows) {
                try {
                    List<WebElement> cols = row.findElements(By.tagName("td"));
                    
                    if (cols.size() < 6) {
                        continue;
                    }
                    
                    String sido = cols.get(0).getText().trim();
                    String region = cols.get(1).getText().trim();
                    int totalAnnounced = parseInteger(cols.get(2).getText());
                    int totalReceived = parseInteger(cols.get(3).getText());
                    int totalDelivered = parseInteger(cols.get(4).getText());
                    int totalRemaining = parseInteger(cols.get(5).getText());
                    
                    Map<String, Object> data = new HashMap<>();
                    data.put("sido", sido);
                    data.put("region", region);
                    data.put("totalAnnounced", totalAnnounced);
                    data.put("totalReceived", totalReceived);
                    data.put("totalDelivered", totalDelivered);
                    data.put("totalRemaining", totalRemaining);
                    
                    dataList.add(data);
                    
                    System.out.printf("시도: %-10s | 지역: %-15s | 공고: %,6d | 접수: %,6d | 출고: %,6d | 잔여: %,6d\n",
                            sido, region, totalAnnounced, totalReceived, totalDelivered, totalRemaining);
                    
                } catch (Exception e) {
                    System.err.println("행 파싱 오류: " + e.getMessage());
                }
            }
            
            System.out.println("\n총 " + dataList.size() + "개의 데이터 수집 완료");
            
            // JSON 파일로 저장
            if (!dataList.isEmpty()) {
                saveToJsonFile(dataList);
            }
            
        } catch (Exception e) {
            System.err.println("크롤링 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
                System.out.println("\n브라우저 종료");
            }
        }
    }

    private static void saveToJsonFile(List<Map<String, Object>> dataList) {
        try {
            // 폴더 생성
            Path folderPath = Paths.get(DATA_FOLDER);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
                System.out.println("폴더 생성: " + folderPath.toAbsolutePath());
            }
            
            // 오늘 날짜
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // 오늘 날짜의 기존 파일 개수 확인
            int sequence = getNextSequence(today);
            
            // 파일명 생성: yyyyMMdd_seq.json
            String filename = String.format("%s_%d.json", today, sequence);
            Path filePath = folderPath.resolve(filename);
            
            // JSON 데이터 생성
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            Map<String, Object> result = new HashMap<>();
            result.put("crawlTime", LocalDateTime.now().toString());
            result.put("date", today);
            result.put("sequence", sequence);
            result.put("totalCount", dataList.size());
            result.put("data", dataList);
            
            // 파일 저장
            mapper.writeValue(filePath.toFile(), result);
            
            System.out.println("\n=== JSON 파일 저장 완료 ===");
            System.out.println("폴더: " + DATA_FOLDER);
            System.out.println("파일명: " + filename);
            System.out.println("전체 경로: " + filePath.toAbsolutePath());
            System.out.println("시퀀스: " + sequence);
            
        } catch (Exception e) {
            System.err.println("JSON 저장 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int getNextSequence(String date) throws IOException {
        Path folderPath = Paths.get(DATA_FOLDER);
        
        if (!Files.exists(folderPath)) {
            return 1;
        }
        
        // 오늘 날짜로 시작하는 파일 찾기
        String pattern = date + "_";
        
        try (Stream<Path> files = Files.list(folderPath)) {
            long count = files
                .filter(path -> path.getFileName().toString().startsWith(pattern))
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .count();
            
            return (int) count + 1;
        }
    }

    private static int parseInteger(String text) {
        if (text == null || text.trim().isEmpty() || text.equals("-")) {
            return 0;
        }
        String cleaned = text.replaceAll("[^0-9]", "");
        return cleaned.isEmpty() ? 0 : Integer.parseInt(cleaned);
    }
}
