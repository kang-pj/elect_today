package com.example.homepage.controller;

import com.example.homepage.service.RegionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 지역 선택 페이지
     */
    @GetMapping("/region-select")
    public String regionSelectPage(Model model) {
        // 시도별 지역 목록 조회
        Map<String, List<String>> regionMap = regionService.getRegionsBySido();
        model.addAttribute("regionMap", regionMap);
        
        log.info("지역 선택 페이지 접속");
        return "region-select";
    }

    /**
     * 지역 선택 처리 (쿠키 저장)
     */
    @PostMapping("/api/region/select")
    @ResponseBody
    public Map<String, Object> selectRegion(
            @RequestParam String sido,
            @RequestParam String region,
            HttpServletResponse response) {
        
        try {
            // 쿠키 생성 (1년 유효)
            Cookie sidoCookie = new Cookie("selected_sido", sido);
            sidoCookie.setMaxAge(365 * 24 * 60 * 60); // 1년
            sidoCookie.setPath("/");
            
            Cookie regionCookie = new Cookie("selected_region", region);
            regionCookie.setMaxAge(365 * 24 * 60 * 60); // 1년
            regionCookie.setPath("/");
            
            response.addCookie(sidoCookie);
            response.addCookie(regionCookie);
            
            log.info("지역 선택 완료: {} - {}", sido, region);
            
            return Map.of(
                "success", true,
                "message", "지역이 선택되었습니다.",
                "sido", sido,
                "region", region
            );
            
        } catch (Exception e) {
            log.error("지역 선택 오류", e);
            return Map.of(
                "success", false,
                "message", "지역 선택 중 오류가 발생했습니다."
            );
        }
    }

    /**
     * 선택된 지역 조회 (쿠키에서)
     */
    @GetMapping("/api/region/selected")
    @ResponseBody
    public Map<String, Object> getSelectedRegion(
            @CookieValue(value = "selected_sido", required = false) String sido,
            @CookieValue(value = "selected_region", required = false) String region) {
        
        if (sido != null && region != null) {
            return Map.of(
                "selected", true,
                "sido", sido,
                "region", region
            );
        } else {
            return Map.of(
                "selected", false
            );
        }
    }

    /**
     * 지역 선택 초기화 (쿠키 삭제)
     */
    @PostMapping("/api/region/reset")
    @ResponseBody
    public Map<String, Object> resetRegion(HttpServletResponse response) {
        
        Cookie sidoCookie = new Cookie("selected_sido", "");
        sidoCookie.setMaxAge(0);
        sidoCookie.setPath("/");
        
        Cookie regionCookie = new Cookie("selected_region", "");
        regionCookie.setMaxAge(0);
        regionCookie.setPath("/");
        
        response.addCookie(sidoCookie);
        response.addCookie(regionCookie);
        
        log.info("지역 선택 초기화");
        
        return Map.of(
            "success", true,
            "message", "지역 선택이 초기화되었습니다."
        );
    }
}
