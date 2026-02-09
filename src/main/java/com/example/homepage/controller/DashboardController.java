package com.example.homepage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    /**
     * 메인 대시보드 페이지
     */
    @GetMapping("/")
    public String dashboard(
            @CookieValue(value = "selected_sido", required = false) String sido,
            @CookieValue(value = "selected_region", required = false) String region,
            Model model) {
        
        if (sido != null && region != null) {
            model.addAttribute("sido", sido);
            model.addAttribute("region", region);
            log.info("대시보드 접속: {} - {}", sido, region);
        }
        
        return "dashboard";
    }
}
