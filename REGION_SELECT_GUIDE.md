# 지역 선택 페이지 가이드

## 🎯 개요

사용자가 메인 사이트 진입 전에 자신의 지역을 선택하도록 하는 페이지입니다.
선택된 지역 정보는 **쿠키**에 저장되어 브라우저를 닫거나 쿠키를 삭제하지 않는 한 계속 유지됩니다.

---

## 📋 주요 기능

### 1. 지역 선택
- **2단계 선택**: 시/도 → 시/군/구
- **실시간 데이터**: 데이터베이스에서 실제 지역 목록 조회
- **자동 필터링**: 시/도 선택 시 해당 지역만 표시

### 2. 쿠키 저장
- **저장 항목**: `selected_sido`, `selected_region`
- **유효 기간**: 1년 (365일)
- **경로**: `/` (전체 사이트)

### 3. 자동 리다이렉트
- 메인 페이지(`/`) 접속 시 지역 미선택 상태면 자동으로 지역 선택 페이지로 이동
- 지역 선택 완료 후 자동으로 메인 페이지로 이동

---

## 🗂️ 파일 구조

### Backend (Java)

#### 1. Controller
**RegionController.java**
```java
@Controller
public class RegionController {
    @GetMapping("/region-select")          // 지역 선택 페이지
    @PostMapping("/api/region/select")     // 지역 선택 처리
    @GetMapping("/api/region/selected")    // 선택된 지역 조회
    @PostMapping("/api/region/reset")      // 지역 선택 초기화
}
```

#### 2. Service
**RegionService.java**
```java
@Service
public class RegionService {
    Map<String, List<String>> getRegionsBySido()  // 시도별 지역 목록
    List<String> getAllSidos()                     // 모든 시도 목록
    List<String> getRegionsBySido(String sido)     // 특정 시도의 지역 목록
}
```

#### 3. Interceptor
**RegionInterceptor.java**
- 메인 페이지 접속 시 지역 선택 여부 확인
- 미선택 시 `/region-select`로 리다이렉트

#### 4. Configuration
**WebConfig.java**
- 인터셉터 등록 및 경로 설정

### Frontend (JSP)

**region-select.jsp**
- 반응형 디자인
- 2단계 선택 UI
- 실시간 유효성 검사
- 애니메이션 효과

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

### 3. 자동 리다이렉트
- 지역이 선택되지 않았다면 자동으로 `/region-select`로 이동

### 4. 지역 선택
1. 시/도 선택 (예: 서울)
2. 시/군/구 선택 (예: 서울특별시)
3. "선택 완료" 버튼 클릭
4. 자동으로 메인 페이지로 이동

---

## 🔌 API 엔드포인트

### 1. 지역 선택 페이지
```
GET /region-select
```
**응답**: JSP 페이지

### 2. 지역 선택 처리
```
POST /api/region/select
Content-Type: application/x-www-form-urlencoded

sido=서울&region=서울특별시
```

**응답**:
```json
{
  "success": true,
  "message": "지역이 선택되었습니다.",
  "sido": "서울",
  "region": "서울특별시"
}
```

**쿠키 설정**:
- `selected_sido=서울; Max-Age=31536000; Path=/`
- `selected_region=서울특별시; Max-Age=31536000; Path=/`

### 3. 선택된 지역 조회
```
GET /api/region/selected
```

**응답 (선택됨)**:
```json
{
  "selected": true,
  "sido": "서울",
  "region": "서울특별시"
}
```

**응답 (미선택)**:
```json
{
  "selected": false
}
```

### 4. 지역 선택 초기화
```
POST /api/region/reset
```

**응답**:
```json
{
  "success": true,
  "message": "지역 선택이 초기화되었습니다."
}
```

---

## 🎨 UI/UX 특징

### 디자인
- **그라데이션 배경**: 보라색 계열
- **카드 형태**: 흰색 배경, 둥근 모서리
- **애니메이션**: 슬라이드 업, 페이드 인 효과
- **반응형**: 모바일/태블릿/데스크톱 대응

### 사용자 경험
1. **직관적인 2단계 선택**
   - 시/도 선택 → 지역 활성화
   - 지역 선택 → 버튼 활성화

2. **실시간 피드백**
   - 선택 시 즉시 UI 업데이트
   - 로딩 인디케이터 표시
   - 성공 메시지 표시

3. **자동 저장 및 이동**
   - 선택 완료 후 2초 대기
   - 자동으로 메인 페이지 이동

---

## 🔧 커스터마이징

### 쿠키 유효 기간 변경
**RegionController.java**
```java
// 현재: 1년
sidoCookie.setMaxAge(365 * 24 * 60 * 60);

// 변경 예시: 30일
sidoCookie.setMaxAge(30 * 24 * 60 * 60);
```

### 리다이렉트 경로 변경
**region-select.jsp**
```javascript
// 현재: 메인 페이지
window.location.href = '/';

// 변경 예시: 대시보드
window.location.href = '/dashboard';
```

### 인터셉터 제외 경로 추가
**WebConfig.java**
```java
registry.addInterceptor(regionInterceptor)
    .excludePathPatterns(
        "/region-select",
        "/api/**",
        "/login",      // 추가
        "/register"    // 추가
    );
```

---

## 🧪 테스트

### 1. 지역 선택 페이지 접속
```bash
curl http://localhost:8080/region-select
```

### 2. 지역 선택 API 테스트
```bash
# 지역 선택
curl -c cookies.txt -X POST "http://localhost:8080/api/region/select" \
  -d "sido=서울&region=서울특별시"

# 선택된 지역 확인
curl -b cookies.txt http://localhost:8080/api/region/selected
```

### 3. 브라우저 테스트
1. 시크릿 모드로 `http://localhost:8080/` 접속
2. 자동으로 `/region-select`로 리다이렉트 확인
3. 지역 선택 후 메인 페이지 이동 확인
4. 새로고침 시 지역 유지 확인

---

## 📊 데이터 흐름

```
1. 사용자 접속
   ↓
2. 인터셉터 확인 (쿠키 체크)
   ↓
3-A. 지역 미선택 → /region-select 리다이렉트
   ↓
4. 지역 선택 페이지 표시
   ↓
5. 사용자 선택 (시/도 → 지역)
   ↓
6. POST /api/region/select
   ↓
7. 쿠키 저장 (1년 유효)
   ↓
8. 메인 페이지로 리다이렉트
   ↓
3-B. 지역 선택됨 → 메인 페이지 표시
```

---

## 🔒 보안 고려사항

### 1. XSS 방지
- JSP에서 `${entry.key}` 자동 이스케이프
- JavaScript에서 textContent 사용

### 2. CSRF 방지
- Spring Security 적용 시 CSRF 토큰 추가 필요

### 3. 쿠키 보안
```java
// HttpOnly 추가 (JavaScript 접근 차단)
sidoCookie.setHttpOnly(true);

// Secure 추가 (HTTPS만 전송)
sidoCookie.setSecure(true);

// SameSite 추가 (CSRF 방지)
sidoCookie.setAttribute("SameSite", "Strict");
```

---

## 🐛 문제 해결

### 1. 지역 목록이 표시되지 않음
**원인**: 데이터베이스에 데이터 없음
**해결**:
```bash
curl -X POST http://localhost:8080/api/ev-subsidy/crawl
```

### 2. 쿠키가 저장되지 않음
**원인**: 브라우저 쿠키 차단
**해결**: 브라우저 설정에서 쿠키 허용

### 3. 리다이렉트 무한 루프
**원인**: 인터셉터 설정 오류
**해결**: WebConfig에서 `/region-select` 제외 확인

---

## 📈 향후 개선 사항

### 1. 기능 추가
- [ ] 최근 선택 지역 기록
- [ ] 즐겨찾기 지역 설정
- [ ] 여러 지역 비교 기능

### 2. UI/UX 개선
- [ ] 지도 기반 선택
- [ ] 검색 기능 추가
- [ ] 인기 지역 추천

### 3. 성능 최적화
- [ ] 지역 목록 캐싱
- [ ] API 응답 압축
- [ ] CDN 적용

---

## 📝 관련 파일

### Backend
- `src/main/java/com/example/homepage/controller/RegionController.java`
- `src/main/java/com/example/homepage/service/RegionService.java`
- `src/main/java/com/example/homepage/config/RegionInterceptor.java`
- `src/main/java/com/example/homepage/config/WebConfig.java`

### Frontend
- `src/main/webapp/WEB-INF/views/region-select.jsp`

### Documentation
- `REGION_SELECT_GUIDE.md` (이 파일)

---

**작성일**: 2026-02-09  
**버전**: 1.0.0  
**상태**: ✅ 완료 및 테스트 완료
