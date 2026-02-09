# 전기차 보조금 대시보드 가이드

## 🎯 개요

선택한 지역의 전기차 보조금 데이터를 시각화하여 보여주는 대시보드입니다.
**Chart.js**를 사용한 꺾은선 그래프로 시계열 데이터를 표시합니다.

---

## 📊 주요 기능

### 1. 데이터 유형 토글
- **잔여**: 공고 대수 - 접수 대수 (우하향 그래프)
- **신청**: 접수 대수 (우상향 그래프)

### 2. 카테고리 선택
- **전체**: 모든 신청 유형 합계
- **우선순위**: 우선순위 신청자
- **법인·기관**: 법인 및 기관 신청
- **택시**: 택시 사업자 신청
- **일반**: 일반 신청자

### 3. 실시간 통계 카드
- 공고 대수 (세부 항목 포함)
- 접수 대수 (세부 항목 포함)
- 출고 대수 (세부 항목 포함)
- 잔여 대수 및 접수율

### 4. 인터랙티브 그래프
- Chart.js 기반 꺾은선 그래프
- 날짜별 데이터 시각화
- 호버 시 상세 정보 표시
- 반응형 디자인

---

## 🗂️ 파일 구조

### Backend

#### 1. Controller
**DashboardController.java**
```java
@GetMapping("/")  // 메인 대시보드 페이지
```

**EvSubsidyController.java**
```java
@GetMapping("/api/ev-subsidy/region-stats")  // 지역 통계 API
```

#### 2. Service
**EvSubsidyService.java**
```java
Map<String, Object> getRegionStats(String sido, String region)
```
- 날짜별 시계열 데이터 조회
- 최신 데이터 상세 정보
- 잔여 대수 계산

#### 3. Repository
**EvSubsidyRepository.java**
```java
List<EvSubsidy> findBySidoAndRegionOrderByCrawlDateAsc(String sido, String region)
```

### Frontend

**dashboard.jsp**
- Chart.js 통합
- 반응형 UI
- 실시간 데이터 업데이트
- 토글 버튼 인터랙션

---

## 🚀 사용 방법

### 1. 서버 실행
```bash
mvn spring-boot:run
```

### 2. 브라우저 접속
```
http://localhost:8080/
```

### 3. 지역 선택
- 처음 접속 시 자동으로 지역 선택 페이지로 이동
- 시/도와 지역 선택
- 선택 완료 후 대시보드로 이동

### 4. 대시보드 사용
1. **데이터 유형 선택**: 잔여 / 신청
2. **카테고리 선택**: 전체 / 우선순위 / 법인·기관 / 택시 / 일반
3. **그래프 확인**: 시계열 데이터 시각화
4. **통계 확인**: 상단 통계 카드

---

## 🔌 API 엔드포인트

### 지역 통계 조회
```
GET /api/ev-subsidy/region-stats?sido={sido}&region={region}
```

**파라미터:**
- `sido`: 시/도 (예: 서울)
- `region`: 시/군/구 (예: 서울특별시)

**응답:**
```json
{
  "sido": "서울",
  "region": "서울특별시",
  "carType": "전기승용",
  "dates": ["2026-02-09"],
  "announced": [10500],
  "received": [2041],
  "delivered": [557],
  "remaining": [8459],
  "latest": {
    "totalAnnounced": 10500,
    "priorityAnnounced": 1600,
    "corporationAnnounced": 0,
    "taxiAnnounced": 840,
    "generalAnnounced": 8060,
    "totalReceived": 2041,
    "priorityReceived": 669,
    "corporationReceived": 64,
    "taxiReceived": 99,
    "generalReceived": 1209,
    "totalDelivered": 557,
    "priorityDelivered": 188,
    "corporationDelivered": 21,
    "taxiDelivered": 0,
    "generalDelivered": 348,
    "remaining": 8459
  }
}
```

---

## 🎨 UI/UX 특징

### 디자인
- **헤더**: 그라데이션 배경 (보라색 계열)
- **통계 카드**: 그리드 레이아웃, 4개 카드
- **그래프**: 흰색 배경, 그림자 효과
- **컨트롤**: 토글 버튼, 직관적 UI

### 색상 구분
- **잔여 데이터**: 빨간색 (#f44336) - 우하향 표시
- **신청 데이터**: 초록색 (#4caf50) - 우상향 표시

### 반응형
- 데스크톱: 4열 그리드
- 태블릿: 2열 그리드
- 모바일: 1열 그리드

---

## 📈 데이터 흐름

```
1. 사용자 접속 (쿠키에서 지역 정보 로드)
   ↓
2. DashboardController
   - 쿠키에서 sido, region 추출
   - JSP에 전달
   ↓
3. JSP 렌더링
   - 페이지 로드
   - JavaScript 실행
   ↓
4. API 호출
   GET /api/ev-subsidy/region-stats
   ↓
5. EvSubsidyService
   - 데이터베이스 조회
   - 시계열 데이터 추출
   - 통계 계산
   ↓
6. 응답 반환 (JSON)
   ↓
7. 그래프 렌더링
   - Chart.js로 시각화
   - 통계 카드 업데이트
```

---

## 🔧 커스터마이징

### 그래프 색상 변경
**dashboard.jsp**
```javascript
borderColor: isRemaining ? '#f44336' : '#4caf50',  // 선 색상
backgroundColor: isRemaining ? 'rgba(244, 67, 54, 0.1)' : 'rgba(76, 175, 80, 0.1)',  // 배경색
```

### 그래프 높이 변경
**dashboard.jsp**
```css
.chart-wrapper {
    height: 400px;  /* 원하는 높이로 변경 */
}
```

### 통계 카드 추가
**dashboard.jsp**
```javascript
<div class="stat-card">
    <h3>새로운 통계</h3>
    <div class="value">${value}<span class="unit">단위</span></div>
</div>
```

---

## 🧪 테스트

### 1. API 테스트
```bash
# 지역 선택
curl -c cookies.txt -X POST "http://localhost:8080/api/region/select" \
  -d "sido=서울&region=서울특별시"

# 통계 조회
curl -b cookies.txt "http://localhost:8080/api/ev-subsidy/region-stats?sido=서울&region=서울특별시"
```

### 2. 브라우저 테스트
1. `http://localhost:8080/` 접속
2. 지역 선택 (서울 - 서울특별시)
3. 대시보드 확인
4. 데이터 유형 토글 (잔여 ↔ 신청)
5. 카테고리 변경 (전체 → 우선순위 등)
6. 그래프 변화 확인

---

## 📊 그래프 설명

### 잔여 그래프 (빨간색)
- **의미**: 아직 신청 가능한 대수
- **계산**: 공고 대수 - 접수 대수
- **트렌드**: 시간이 지날수록 감소 (우하향)
- **용도**: 신청 가능 여부 판단

### 신청 그래프 (초록색)
- **의미**: 누적 신청 대수
- **계산**: 접수 대수
- **트렌드**: 시간이 지날수록 증가 (우상향)
- **용도**: 신청 추이 파악

---

## 🔒 보안 고려사항

### 1. 쿠키 검증
- 서버에서 쿠키 값 검증
- 존재하지 않는 지역 요청 차단

### 2. SQL 인젝션 방지
- JPA 사용으로 자동 방지
- 파라미터 바인딩

### 3. XSS 방지
- JSP 자동 이스케이프
- JavaScript textContent 사용

---

## 🐛 문제 해결

### 1. 그래프가 표시되지 않음
**원인**: Chart.js 로드 실패
**해결**: CDN 연결 확인
```html
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
```

### 2. 데이터가 없음
**원인**: 크롤링 미실행
**해결**:
```bash
curl -X POST http://localhost:8080/api/ev-subsidy/crawl
```

### 3. 지역 정보 없음
**원인**: 쿠키 삭제됨
**해결**: 지역 선택 페이지에서 재선택

---

## 📈 향후 개선 사항

### 1. 기능 추가
- [ ] 날짜 범위 선택
- [ ] 여러 지역 비교
- [ ] 데이터 내보내기 (CSV, Excel)
- [ ] 알림 설정 (잔여 대수 임계값)

### 2. 그래프 개선
- [ ] 막대 그래프 추가
- [ ] 파이 차트 (카테고리별 비율)
- [ ] 애니메이션 효과
- [ ] 확대/축소 기능

### 3. 성능 최적화
- [ ] 데이터 캐싱
- [ ] 지연 로딩
- [ ] API 응답 압축

---

## 📝 관련 파일

### Backend
- `src/main/java/com/example/homepage/controller/DashboardController.java`
- `src/main/java/com/example/homepage/controller/EvSubsidyController.java`
- `src/main/java/com/example/homepage/service/EvSubsidyService.java`
- `src/main/java/com/example/homepage/repository/EvSubsidyRepository.java`

### Frontend
- `src/main/webapp/WEB-INF/views/dashboard.jsp`

### Documentation
- `DASHBOARD_GUIDE.md` (이 파일)
- `REGION_SELECT_GUIDE.md`
- `DATABASE_SETUP_REPORT.md`

---

## 🎯 주요 기술 스택

- **Backend**: Spring Boot 3.0, JPA
- **Frontend**: JSP, Chart.js 4.4.0
- **Database**: PostgreSQL
- **UI**: Vanilla CSS (반응형)

---

**작성일**: 2026-02-09  
**버전**: 1.0.0  
**상태**: ✅ 완료 및 테스트 완료
