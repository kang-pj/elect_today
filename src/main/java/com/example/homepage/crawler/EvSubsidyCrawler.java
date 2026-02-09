package com.example.homepage.crawler;

import com.example.homepage.dto.EvSubsidyData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EvSubsidyCrawler {

    private static final String TARGET_URL = "https://ev.or.kr/nportal/buySupprt/initSubsidyPaymentCheckAction.do";
    private static final String CHROMEDRIVER_PATH = "/usr/local/bin/chromedriver-144";

    public List<EvSubsidyData> crawlSubsidyData() {
        List<EvSubsidyData> dataList = new ArrayList<>();
        WebDriver driver = null;

        try {
            log.info("전기차 보조금 크롤링 시작: {}", TARGET_URL);

            // ChromeDriver 경로 설정
            System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);

            // Chrome 옵션 설정
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);

            log.info("브라우저 시작 완료");

            // 페이지 접속
            driver.get(TARGET_URL);

            // 페이지 로딩 대기
            Thread.sleep(5000);

            log.info("페이지 로딩 완료");

            // 테이블 찾기
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            log.info("발견된 테이블 수: {}", tables.size());

            if (tables.size() < 2) {
                log.warn("데이터 테이블을 찾을 수 없습니다");
                return dataList;
            }

            // 두 번째 테이블이 데이터 테이블
            WebElement dataTable = tables.get(1);
            List<WebElement> rows = dataTable.findElements(By.tagName("tr"));

            log.info("발견된 데이터 행 수: {}", rows.size());

            // 첫 번째 행은 헤더이므로 건너뛰기
            for (int i = 1; i < rows.size(); i++) {
                try {
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));

                    if (cells.size() < 8) {
                        continue;
                    }

                    String sido = cells.get(0).getText().trim();
                    String region = cells.get(1).getText().trim();
                    String carType = cells.get(2).getText().trim();

                    // 공고 대수 파싱 (5번째 컬럼)
                    String[] announcedLines = cells.get(5).getText().trim().split("\n");
                    int totalAnnounced = parseInteger(announcedLines.length > 0 ? announcedLines[0] : "0");
                    int priorityAnnounced = parseInteger(announcedLines.length > 1 ? announcedLines[1] : "0");
                    int corporationAnnounced = parseInteger(announcedLines.length > 2 ? announcedLines[2] : "0");
                    int taxiAnnounced = parseInteger(announcedLines.length > 3 ? announcedLines[3] : "0");
                    int generalAnnounced = parseInteger(announcedLines.length > 4 ? announcedLines[4] : "0");

                    // 접수 대수 파싱 (6번째 컬럼)
                    String[] receivedLines = cells.get(6).getText().trim().split("\n");
                    int totalReceived = parseInteger(receivedLines.length > 0 ? receivedLines[0] : "0");
                    int priorityReceived = parseInteger(receivedLines.length > 1 ? receivedLines[1] : "0");
                    int corporationReceived = parseInteger(receivedLines.length > 2 ? receivedLines[2] : "0");
                    int taxiReceived = parseInteger(receivedLines.length > 3 ? receivedLines[3] : "0");
                    int generalReceived = parseInteger(receivedLines.length > 4 ? receivedLines[4] : "0");

                    // 출고 대수 파싱 (7번째 컬럼)
                    String[] deliveredLines = cells.get(7).getText().trim().split("\n");
                    int totalDelivered = parseInteger(deliveredLines.length > 0 ? deliveredLines[0] : "0");
                    int priorityDelivered = parseInteger(deliveredLines.length > 1 ? deliveredLines[1] : "0");
                    int corporationDelivered = parseInteger(deliveredLines.length > 2 ? deliveredLines[2] : "0");
                    int taxiDelivered = parseInteger(deliveredLines.length > 3 ? deliveredLines[3] : "0");
                    int generalDelivered = parseInteger(deliveredLines.length > 4 ? deliveredLines[4] : "0");

                    EvSubsidyData data = EvSubsidyData.builder()
                            .sido(sido)
                            .region(region)
                            .carType(carType)
                            .totalAnnounced(totalAnnounced)
                            .priorityAnnounced(priorityAnnounced)
                            .corporationAnnounced(corporationAnnounced)
                            .taxiAnnounced(taxiAnnounced)
                            .generalAnnounced(generalAnnounced)
                            .totalReceived(totalReceived)
                            .priorityReceived(priorityReceived)
                            .corporationReceived(corporationReceived)
                            .taxiReceived(taxiReceived)
                            .generalReceived(generalReceived)
                            .totalDelivered(totalDelivered)
                            .priorityDelivered(priorityDelivered)
                            .corporationDelivered(corporationDelivered)
                            .taxiDelivered(taxiDelivered)
                            .generalDelivered(generalDelivered)
                            .build();

                    dataList.add(data);

                    if (i <= 5) {
                        log.info("{}: 공고 {}대 / 접수 {}대 / 출고 {}대", region, totalAnnounced, totalReceived, totalDelivered);
                    }

                } catch (Exception e) {
                    log.error("행 {} 파싱 오류: {}", i, e.getMessage());
                }
            }

            log.info("크롤링 완료: 총 {} 개 데이터 수집", dataList.size());

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생", e);
        } finally {
            if (driver != null) {
                driver.quit();
                log.info("브라우저 종료");
            }
        }

        return dataList;
    }

    private int parseInteger(String text) {
        if (text == null || text.trim().isEmpty() || text.equals("-")) {
            return 0;
        }
        // 괄호 안의 숫자나 일반 숫자 추출
        String cleaned = text.replaceAll("[^0-9]", "");
        return cleaned.isEmpty() ? 0 : Integer.parseInt(cleaned);
    }
}
