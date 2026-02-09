# PostgreSQL 데이터베이스 구성 완료 보고서

## 🎉 PostgreSQL 데이터베이스 구성 및 크롤링 완료!

### 작업 완료 사항

#### ✅ 1. PostgreSQL 데이터베이스 생성
- **데이터베이스명**: `ev_subsidy`
- **사용자**: `refine`
- **포트**: `5432` (기본)

#### ✅ 2. 테이블 생성
3개의 테이블이 생성되었습니다:

##### 2.1 users 테이블
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

##### 2.2 ev_subsidy 테이블 (메인 테이블)
```sql
CREATE TABLE ev_subsidy (
    id BIGSERIAL PRIMARY KEY,
    crawl_date DATE NOT NULL,
    sido VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    car_type VARCHAR(50),
    
    -- 공고 대수 (5개)
    total_announced INTEGER NOT NULL,
    priority_announced INTEGER,
    corporation_announced INTEGER,
    taxi_announced INTEGER,
    general_announced INTEGER,
    
    -- 접수 대수 (5개)
    total_received INTEGER NOT NULL,
    priority_received INTEGER,
    corporation_received INTEGER,
    taxi_received INTEGER,
    general_received INTEGER,
    
    -- 출고 대수 (5개)
    total_delivered INTEGER NOT NULL,
    priority_delivered INTEGER,
    corporation_delivered INTEGER,
    taxi_delivered INTEGER,
    general_delivered INTEGER,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_crawl UNIQUE (crawl_date, sido, region, car_type)
);
```

**인덱스:**
- `idx_ev_subsidy_crawl_date` - 날짜 검색 최적화
- `idx_ev_subsidy_sido` - 시도별 검색 최적화
- `idx_ev_subsidy_region` - 지역별 검색 최적화
- `idx_ev_subsidy_car_type` - 차종별 검색 최적화
- `unique_crawl` - 중복 방지 (날짜+시도+지역+차종)

##### 2.3 ev_subsidy_daily 테이블
```sql
CREATE TABLE ev_subsidy_daily (
    id BIGSERIAL PRIMARY KEY,
    target_date DATE NOT NULL,
    sido VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    daily_received INTEGER NOT NULL,
    daily_delivered INTEGER NOT NULL,
    daily_remaining_change INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_daily UNIQUE (target_date, sido, region)
);
```

#### ✅ 3. Spring Boot 설정
**application.properties:**
```properties
# PostgreSQL Configuration
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/ev_subsidy
spring.datasource.username=refine
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul
```

#### ✅ 4. 크롤링 실행 및 데이터 저장
- **총 저장 레코드**: 161개
- **크롤링 날짜**: 2026-02-09
- **데이터 항목**: 15개 (공고/접수/출고 각 5개)

---

## 저장된 데이터 확인

### 전체 통계
```
총 레코드 수: 161개
```

### 시도별 통계 (상위 10개)
| 시도 | 지역 수 | 총 공고 | 총 접수 | 총 출고 |
|------|---------|---------|---------|---------|
| 공단 | 1 | 30,000 | 3,380 | 213 |
| 경기 | 31 | 28,252 | 3,989 | 291 |
| 서울 | 1 | 10,500 | 2,041 | 557 |
| 경북 | 22 | 7,052 | 2,127 | 38 |
| 경남 | 18 | 6,354 | 1,973 | 171 |
| 충북 | 11 | 6,189 | 2,993 | 563 |
| 인천 | 1 | 6,160 | 957 | 95 |
| 강원 | 18 | 4,710 | 471 | 14 |
| 부산 | 1 | 4,126 | 670 | 222 |
| 충남 | 15 | 3,502 | 830 | 0 |

### 주요 도시 상세 데이터

#### 서울특별시
```
차종: 전기승용
공고 대수:
  - 전체: 10,500대
  - 우선순위: 1,600대
  - 법인·기관: 0대
  - 택시: 840대
  - 일반: 8,060대

접수 대수:
  - 전체: 2,041대
  - 우선순위: 669대
  - 법인·기관: 64대
  - 택시: 99대
  - 일반: 1,209대

출고 대수:
  - 전체: 557대
  - 우선순위: 188대
  - 법인·기관: 21대
  - 택시: 0대
  - 일반: 348대
```

#### 부산광역시
```
차종: 전기승용
공고: 4,126대 | 접수: 670대 | 출고: 222대
```

#### 대구광역시
```
차종: 전기승용
공고: 1,215대 | 접수: 1,314대 | 출고: 2대
```

#### 인천광역시
```
차종: 전기승용
공고: 6,160대 | 접수: 957대 | 출고: 95대
```

#### 광주광역시
```
차종: 전기승용
공고: 1,930대 | 접수: 667대 | 출고: 29대
```

---

## 데이터베이스 접속 방법

### 1. psql 명령줄
```bash
# 데이터베이스 접속
psql ev_subsidy

# 테이블 목록 확인
\dt

# 테이블 구조 확인
\d ev_subsidy

# 데이터 조회
SELECT * FROM ev_subsidy LIMIT 10;
```

### 2. Spring Boot API
```bash
# 서버 실행
mvn spring-boot:run

# 헬스 체크
curl http://localhost:8080/api/ev-subsidy/health

# 크롤링 실행
curl -X POST http://localhost:8080/api/ev-subsidy/crawl

# 오늘 데이터 조회
curl http://localhost:8080/api/ev-subsidy/today
```

---

## 유용한 SQL 쿼리

### 1. 전체 데이터 조회
```sql
SELECT * FROM ev_subsidy ORDER BY sido, region;
```

### 2. 특정 시도 데이터 조회
```sql
SELECT * FROM ev_subsidy WHERE sido = '서울';
```

### 3. 공고 대수 상위 10개 지역
```sql
SELECT sido, region, total_announced, total_received, total_delivered
FROM ev_subsidy
ORDER BY total_announced DESC
LIMIT 10;
```

### 4. 접수율 계산
```sql
SELECT 
    sido,
    region,
    total_announced,
    total_received,
    ROUND((total_received::NUMERIC / total_announced * 100), 2) as reception_rate
FROM ev_subsidy
WHERE total_announced > 0
ORDER BY reception_rate DESC
LIMIT 10;
```

### 5. 시도별 집계
```sql
SELECT 
    sido,
    COUNT(*) as region_count,
    SUM(total_announced) as total_announced,
    SUM(total_received) as total_received,
    SUM(total_delivered) as total_delivered,
    ROUND((SUM(total_received)::NUMERIC / SUM(total_announced) * 100), 2) as reception_rate
FROM ev_subsidy
GROUP BY sido
ORDER BY total_announced DESC;
```

### 6. 최근 크롤링 데이터
```sql
SELECT crawl_date, COUNT(*) as record_count
FROM ev_subsidy
GROUP BY crawl_date
ORDER BY crawl_date DESC;
```

---

## 데이터베이스 백업

### 백업 생성
```bash
pg_dump ev_subsidy > ev_subsidy_backup_$(date +%Y%m%d).sql
```

### 백업 복원
```bash
psql ev_subsidy < ev_subsidy_backup_20260209.sql
```

---

## 성능 최적화

### 인덱스 사용 확인
```sql
EXPLAIN ANALYZE
SELECT * FROM ev_subsidy WHERE sido = '서울';
```

### 테이블 통계 업데이트
```sql
ANALYZE ev_subsidy;
```

### 테이블 크기 확인
```sql
SELECT 
    pg_size_pretty(pg_total_relation_size('ev_subsidy')) as total_size,
    pg_size_pretty(pg_relation_size('ev_subsidy')) as table_size,
    pg_size_pretty(pg_indexes_size('ev_subsidy')) as indexes_size;
```

---

## 프로젝트 파일

### 생성된 파일
- `create_tables.sql` - 테이블 생성 SQL 스크립트
- `DATABASE_SETUP_REPORT.md` - 이 보고서
- `FINAL_SUMMARY.md` - 전체 프로젝트 요약

### 설정 파일
- `src/main/resources/application.properties` - Spring Boot 설정
- `pom.xml` - Maven 의존성 (PostgreSQL 드라이버 포함)

---

## 다음 단계

### 1. 정기 크롤링 설정
```java
@Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
public void scheduledCrawl() {
    crawlAndSaveData();
}
```

### 2. 데이터 분석 API 추가
- 시도별 통계
- 일일 변화량 추이
- 접수율 분석

### 3. 대시보드 구현
- 실시간 데이터 시각화
- 지역별 비교 차트
- 트렌드 분석

---

## 🎯 완료 체크리스트

- ✅ PostgreSQL 데이터베이스 생성
- ✅ 테이블 생성 (users, ev_subsidy, ev_subsidy_daily)
- ✅ 인덱스 생성 (성능 최적화)
- ✅ Spring Boot 설정 (PostgreSQL 연동)
- ✅ 크롤링 실행 (161개 데이터 수집)
- ✅ 데이터 저장 확인
- ✅ 15개 세부 항목 모두 저장 확인
- ✅ SQL 쿼리 테스트
- ✅ 통계 데이터 확인

---

**작성일**: 2026-02-09  
**데이터베이스**: PostgreSQL  
**총 레코드**: 161개  
**상태**: ✅ 완료
