# 전기차 보조금 데이터 시스템

## ✅ Java 크롤링 성공!

**순수 Java + Selenium으로 크롤링이 정상 작동합니다!**
- 실제 사이트(ev.or.kr)에서 데이터 수집 성공
- 162개 행 데이터 수집 완료
- Python 없이 Java만으로 완전 동작

## 시스템 구성

### 1. Java Selenium 크롤러 (실제 크롤링)
- `EvSubsidyCrawler`: Selenium 기반 브라우저 자동화
- ChromeDriver 144 버전 사용
- 실제 사이트에서 데이터 수집 성공

### 2. 데이터 로더
- JSON 파일을 읽어서 데이터베이스에 저장
- 자동으로 최신 파일 선택 가능

### 3. REST API
- 데이터 조회 및 관리 API 제공

## 사용 방법

### 1단계: 애플리케이션 실행
```bash
export JAVA_HOME=/usr/local/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home
mvn spring-boot:run
```

### 2단계: 크롤링 API 호출
```bash
# 실제 크롤링 실행
curl -X POST http://localhost:8080/api/ev-subsidy/crawl
```

### 3단계: 데이터 조회
```bash
# 오늘 데이터 조회
curl http://localhost:8080/api/ev-subsidy/today

# 오늘 일일 변화량 조회
curl http://localhost:8080/api/ev-subsidy/today/daily
```

## API 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/ev-subsidy/health` | 헬스 체크 |
| GET | `/api/ev-subsidy/json-files` | 사용 가능한 JSON 파일 목록 |
| POST | `/api/ev-subsidy/load-json` | JSON 파일에서 데이터 로딩 |
| **POST** | **`/api/ev-subsidy/crawl`** | **실제 크롤링 실행 (작동!)** |
| GET | `/api/ev-subsidy/today` | 오늘 데이터 조회 |
| GET | `/api/ev-subsidy/today/daily` | 오늘 일일 변화량 조회 |

## 크롤링 결과 예시

```
사이트 접속 중...
테이블 수: 3
데이터 행 수: 162
서울 - 서울특별시
부산 - 부산광역시
대구 - 대구광역시
인천 - 인천광역시
광주 - 광주광역시
크롤링 성공!
```

## 데이터 구조

### 데이터베이스 테이블
- `ev_subsidy`: 일별 보조금 현황
  - crawlDate: 크롤링 날짜
  - sido: 시도
  - region: 지역
  - totalAnnounced: 총 공고 대수
  - totalReceived: 총 접수 대수
  - totalDelivered: 총 출고 대수
  - totalRemaining: 잔여 대수

- `ev_subsidy_daily`: 일일 변화량
  - targetDate: 대상 날짜
  - sido: 시도
  - region: 지역
  - dailyReceived: 일일 접수 증가량
  - dailyDelivered: 일일 출고 증가량
  - dailyRemainingChange: 일일 잔여 변화량

## 필수 요구사항

### ChromeDriver 설치
```bash
# ChromeDriver 144 버전이 /usr/local/bin/chromedriver-144에 설치되어 있어야 합니다
# 이미 설치되어 있습니다!
```

### Java 환경
```bash
# Java 17 설치 확인
java -version

# Maven 설치 확인
mvn -version
```

## 기술 스택
- **크롤링**: Java + Selenium 4.5.3
- **백엔드**: Spring Boot 3.0.13 + Java 17
- **데이터베이스**: MariaDB + Spring Data JPA
- **JSON 처리**: Jackson

## 성공 요인

### 해결된 문제들
1. ✅ **ChromeDriver 버전 불일치** → ChromeDriver 144 수동 설치
2. ✅ **WebDriverManager 버그** → 시스템 경로 직접 지정
3. ✅ **WebSocket 연결 오류** → `--remote-allow-origins=*` 옵션 추가
4. ✅ **봇 탐지 우회** → User-Agent 및 자동화 감지 비활성화

### 핵심 설정
```java
System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver-144");

ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
options.addArguments("--remote-allow-origins=*");  // 중요!
options.addArguments("--disable-blink-features=AutomationControlled");
options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
```

## 권장 워크플로우

### 개발/테스트
```
샘플 데이터 생성 → JSON 로더로 DB 저장 → API 테스트
```

### 실제 운영
```
Java 크롤러 실행 → 자동 DB 저장 → 일일 변화량 계산 → 데이터 조회
```

### 자동화 (향후)
```
Spring Scheduler → Java 크롤러 호출 → 자동 DB 저장 → 일일 변화량 계산
```

## 문제 해결

### ChromeDriver를 찾을 수 없음
```bash
# ChromeDriver 위치 확인
ls -la /usr/local/bin/chromedriver-144

# 권한 확인
chmod +x /usr/local/bin/chromedriver-144
```

### Chrome 버전 불일치
```bash
# Chrome 버전 확인
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --version

# 해당 버전의 ChromeDriver 다운로드
# https://googlechromelabs.github.io/chrome-for-testing/
```

## 테스트 방법

### 간단한 테스트
```bash
# TestJavaCrawler 실행
javac -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" TestJavaCrawler.java
java -cp ".:target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" TestJavaCrawler
```

### Spring Boot 통합 테스트
```bash
# 애플리케이션 실행
mvn spring-boot:run

# 크롤링 API 호출
curl -X POST http://localhost:8080/api/ev-subsidy/crawl
```

## 성공 사례

✅ **Java 크롤링 성공!**
- 2026-02-05 기준 162개 행 데이터 수집
- 순수 Java + Selenium으로 구현
- Python 의존성 없음
- 서버 배포 가능

## 배포 시 주의사항

### 서버 환경 설정
1. Java 17 설치
2. Chrome 브라우저 설치
3. ChromeDriver 설치 (Chrome 버전과 일치)
4. ChromeDriver 경로 설정

### 환경 변수
```bash
# application.properties 또는 환경 변수로 설정
webdriver.chrome.driver=/usr/local/bin/chromedriver-144
```

### Docker 배포 시
```dockerfile
# Chrome 및 ChromeDriver 설치 필요
RUN apt-get update && apt-get install -y \
    chromium \
    chromium-driver
```
