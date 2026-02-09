<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>홈페이지</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background: white;
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
        }
        h1 {
            color: #667eea;
            margin-bottom: 1.5rem;
            font-size: 2.5rem;
        }
        p {
            color: #555;
            font-size: 1.2rem;
            margin-bottom: 2rem;
        }
        .nav-links {
            display: flex;
            gap: 1rem;
            justify-content: center;
        }
        .nav-links a {
            padding: 0.8rem 2rem;
            background: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background 0.3s;
        }
        .nav-links a:hover {
            background: #764ba2;
        }
        .info {
            margin-top: 2rem;
            padding: 1rem;
            background: #f0f0f0;
            border-radius: 8px;
            font-size: 0.9rem;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 ${message}</h1>
        <p>Spring Boot 3.0 + JSP 3.0 + MariaDB 프로젝트</p>
        <div class="nav-links">
            <a href="/">홈</a>
            <a href="/about">소개</a>
        </div>
        <div class="info">
            <strong>환경 정보:</strong><br>
            Tomcat 10.0.x | JSP 3.0 | Servlet 5.0 | JDK 17 | MariaDB 10.x
        </div>
    </div>
</body>
</html>
