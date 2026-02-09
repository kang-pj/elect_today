package com.example.homepage.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RegionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 지역 선택 페이지와 API는 제외
        if (requestURI.startsWith("/region-select") || 
            requestURI.startsWith("/api/") ||
            requestURI.startsWith("/css/") ||
            requestURI.startsWith("/js/") ||
            requestURI.startsWith("/images/")) {
            return true;
        }
        
        // 쿠키에서 지역 정보 확인
        Cookie[] cookies = request.getCookies();
        boolean hasRegion = false;
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("selected_sido".equals(cookie.getName()) && !cookie.getValue().isEmpty()) {
                    hasRegion = true;
                    break;
                }
            }
        }
        
        // 지역이 선택되지 않았으면 지역 선택 페이지로 리다이렉트
        if (!hasRegion && requestURI.equals("/")) {
            log.info("지역 미선택 - 지역 선택 페이지로 리다이렉트");
            response.sendRedirect("/region-select");
            return false;
        }
        
        return true;
    }
}
