# DBeaver PostgreSQL 연결 정보

## 📊 데이터베이스 접속 정보

### 기본 연결 정보
```
Connection Type: PostgreSQL
Host: localhost
Port: 5432
Database: ev_subsidy
Username: refine
Password: (비어있음 - 빈 칸으로 두세요)
```

---

## DBeaver 설정 방법

### 1. 새 연결 생성
1. DBeaver 실행
2. 좌측 상단 **"새 데이터베이스 연결"** 버튼 클릭 (또는 `Ctrl+Shift+N`)
3. **PostgreSQL** 선택 후 **다음**

### 2. 연결 설정
**Main 탭:**
```
Host: localhost
Port: 5432
Database: ev_subsidy
Username: refine
Password: (비워두기)
```

**체크박스:**
- ✅ Show all databases (모든 데이터베이스 표시)
- ✅ Save password (비밀번호 저장 - 비어있어도 체크)

### 3. 드라이버 설정
- DBeaver가 자동으로 PostgreSQL JDBC 드라이버를 다운로드합니다
- 처음 연결 시 "Download driver files" 클릭

### 4. 연결 테스트
- **"Test Connection"** 버튼 클릭
- "Connected" 메시지 확인
- **완료** 클릭

---

## 빠른 설정 (JDBC URL 방식)

### JDBC URL
```
jdbc:postgresql://localhost:5432/ev_subsidy
```

### 전체 연결 문자열
```
jdbc:postgresql://localhost:5432/ev_subsidy?user=refine
```

---

## 연결 후 확인 사항

### 1. 테이블 목록 확인
좌측 네비게이터에서:
```
ev_subsidy
  └─ Schemas
      └─ public
          └─ Tables
              ├─ ev_subsidy (161 rows)
              ├─ ev_subsidy_daily (0 rows)
              └─ users (0 rows)
```

### 2. 데이터 확인 쿼리
```sql
-- 전체 레코드 수
SELECT COUNT(*) FROM ev_subsidy;

-- 상위 10개 데이터
SELECT * FROM ev_subsidy LIMIT 10;

-- 서울 데이터
SELECT * FROM ev_subsidy WHERE sido = '서울';
```

---

## 고급 설정 (선택사항)

### SSH 터널 사용 (원격 접속 시)
**SSH 탭:**
```
Use SSH Tunnel: ☐ (로컬이므로 체크 안 함)
```

### SSL 설정
**SSL 탭:**
```
Use SSL: ☐ (로컬이므로 체크 안 함)
```

### 연결 풀 설정
**Connection 탭:**
```
Connection timeout: 30 seconds
Keep-Alive interval: 60 seconds
```

---

## 문제 해결

### 연결 실패 시 체크리스트

#### 1. PostgreSQL 실행 확인
```bash
pg_isready
# 출력: /tmp:5432 - accepting connections
```

#### 2. 포트 확인
```bash
lsof -i :5432
# PostgreSQL이 5432 포트에서 실행 중인지 확인
```

#### 3. 데이터베이스 존재 확인
```bash
psql -l | grep ev_subsidy
```

#### 4. 사용자 권한 확인
```bash
psql ev_subsidy -c "\du"
# refine 사용자가 Superuser 권한을 가지고 있는지 확인
```

---

## 유용한 DBeaver 기능

### 1. ER 다이어그램 보기
1. `ev_subsidy` 테이블 우클릭
2. **"View Diagram"** 선택
3. 테이블 관계 시각화

### 2. 데이터 내보내기
1. 테이블 우클릭
2. **"Export Data"** 선택
3. 형식 선택 (CSV, JSON, Excel 등)

### 3. SQL 에디터
- `Ctrl+]` 또는 `Cmd+]`: 새 SQL 에디터
- `Ctrl+Enter`: 쿼리 실행
- `Ctrl+Shift+E`: 실행 계획 보기

### 4. 데이터 필터링
- 테이블 데이터 뷰에서 컬럼 헤더 클릭
- 필터 조건 입력
- 실시간 데이터 필터링

---

## 추천 쿼리 북마크

### 1. 시도별 통계
```sql
SELECT 
    sido,
    COUNT(*) as region_count,
    SUM(total_announced) as total_announced,
    SUM(total_received) as total_received,
    SUM(total_delivered) as total_delivered,
    ROUND((SUM(total_received)::NUMERIC / NULLIF(SUM(total_announced), 0) * 100), 2) as reception_rate
FROM ev_subsidy
GROUP BY sido
ORDER BY total_announced DESC;
```

### 2. 접수율 상위 지역
```sql
SELECT 
    sido,
    region,
    total_announced,
    total_received,
    ROUND((total_received::NUMERIC / NULLIF(total_announced, 0) * 100), 2) as reception_rate
FROM ev_subsidy
WHERE total_announced > 0
ORDER BY reception_rate DESC
LIMIT 20;
```

### 3. 최근 크롤링 데이터
```sql
SELECT 
    crawl_date,
    COUNT(*) as record_count,
    SUM(total_announced) as total_announced,
    SUM(total_received) as total_received,
    SUM(total_delivered) as total_delivered
FROM ev_subsidy
GROUP BY crawl_date
ORDER BY crawl_date DESC;
```

### 4. 서울 상세 정보
```sql
SELECT 
    sido,
    region,
    car_type,
    total_announced,
    priority_announced,
    corporation_announced,
    taxi_announced,
    general_announced,
    total_received,
    priority_received,
    corporation_received,
    taxi_received,
    general_received,
    total_delivered,
    priority_delivered,
    corporation_delivered,
    taxi_delivered,
    general_delivered,
    created_at
FROM ev_subsidy
WHERE sido = '서울';
```

---

## 연결 정보 요약 카드

```
┌─────────────────────────────────────┐
│   PostgreSQL 연결 정보              │
├─────────────────────────────────────┤
│ Host:     localhost                 │
│ Port:     5432                      │
│ Database: ev_subsidy                │
│ User:     refine                    │
│ Password: (empty)                   │
├─────────────────────────────────────┤
│ JDBC URL:                           │
│ jdbc:postgresql://localhost:5432/   │
│ ev_subsidy                          │
├─────────────────────────────────────┤
│ Tables:                             │
│ • ev_subsidy (161 rows)             │
│ • ev_subsidy_daily (0 rows)         │
│ • users (0 rows)                    │
└─────────────────────────────────────┘
```

---

## 스크린샷 가이드

### DBeaver 연결 설정 화면
```
┌─────────────────────────────────────────┐
│ PostgreSQL - New Connection             │
├─────────────────────────────────────────┤
│ Main │ PostgreSQL │ Driver properties   │
├─────────────────────────────────────────┤
│                                         │
│ Host:     [localhost            ]       │
│ Port:     [5432                 ]       │
│ Database: [ev_subsidy           ]       │
│                                         │
│ Authentication:                         │
│ Username: [refine               ]       │
│ Password: [                     ]       │
│                                         │
│ ☑ Save password                         │
│ ☑ Show all databases                    │
│                                         │
│ [Test Connection]  [OK]  [Cancel]       │
└─────────────────────────────────────────┘
```

---

**작성일**: 2026-02-09  
**데이터베이스**: PostgreSQL 로컬 인스턴스  
**상태**: ✅ 연결 가능
