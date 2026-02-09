# Spring Boot 홈페이지 프로젝트

## 환경 사양
- **Tomcat**: 10.0.x
- **JSP**: 3.0
- **Servlet**: 5.0
- **JDK**: 17
- **MariaDB**: 10.x
- **Spring Boot**: 3.0.13
- **인코딩**: UTF-8

## 프로젝트 구조
```
homepage/
├── src/
│   ├── main/
│   │   ├── java/com/example/homepage/
│   │   │   ├── HomepageApplication.java
│   │   │   ├── controller/
│   │   │   │   └── HomeController.java
│   │   │   ├── entity/
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       └── UserService.java
│   │   ├── resources/
│   │   │   └── application.properties
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── views/
│   │               ├── index.jsp
│   │               └── about.jsp
│   └── test/
└── pom.xml
```

## 데이터베이스 설정

MariaDB에 데이터베이스를 생성하세요:

```sql
CREATE DATABASE homepage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

`application.properties` 파일에서 데이터베이스 연결 정보를 수정하세요:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/homepage?characterEncoding=UTF-8&serverTimezone=Asia/Seoul
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 실행 방법

### 0. Java 17 설정 (중요!)
Maven 빌드 시 Java 17을 사용해야 합니다:

```bash
# macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 또는 직접 경로 지정
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

### 1. Maven 빌드
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean package
```

또는 테스트 스킵:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean package -DskipTests
```

### 2. Spring Boot 내장 서버로 실행
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn spring-boot:run
```

### 3. 외부 Tomcat 10.0.x에 배포
생성된 WAR 파일(`target/homepage-0.0.1-SNAPSHOT.war`)을 Tomcat의 `webapps` 디렉토리에 복사하세요.

## 접속 URL
- 홈페이지: http://localhost:8080/
- 소개 페이지: http://localhost:8080/about

## 주요 기능
- Spring Boot 3.0 기반 웹 애플리케이션
- JSP 3.0을 사용한 뷰 렌더링
- MariaDB 연동 및 JPA 사용
- UTF-8 인코딩 완벽 지원
- User 엔티티 및 CRUD 기본 구조

## 기술 스택
- **Backend**: Spring Boot 3.0, Spring Data JPA
- **Frontend**: JSP 3.0, JSTL 3.0
- **Database**: MariaDB 10.x
- **Build Tool**: Maven
- **Java Version**: 17
