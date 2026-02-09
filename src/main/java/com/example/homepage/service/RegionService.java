package com.example.homepage.service;

import com.example.homepage.repository.EvSubsidyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    private final EvSubsidyRepository subsidyRepository;

    /**
     * 시도별 지역 목록 조회
     */
    @Transactional(readOnly = true)
    public Map<String, List<String>> getRegionsBySido() {
        // 모든 데이터에서 시도와 지역 추출
        var allData = subsidyRepository.findAll();
        
        Map<String, Set<String>> regionSetMap = new LinkedHashMap<>();
        
        for (var data : allData) {
            String sido = data.getSido();
            String region = data.getRegion();
            
            regionSetMap.computeIfAbsent(sido, k -> new TreeSet<>()).add(region);
        }
        
        // Set을 List로 변환
        Map<String, List<String>> regionMap = new LinkedHashMap<>();
        regionSetMap.forEach((sido, regions) -> {
            regionMap.put(sido, new ArrayList<>(regions));
        });
        
        log.info("시도별 지역 목록 조회 완료: {} 개 시도", regionMap.size());
        return regionMap;
    }

    /**
     * 모든 시도 목록 조회
     */
    @Transactional(readOnly = true)
    public List<String> getAllSidos() {
        return subsidyRepository.findAll().stream()
                .map(data -> data.getSido())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 특정 시도의 지역 목록 조회
     */
    @Transactional(readOnly = true)
    public List<String> getRegionsBySido(String sido) {
        return subsidyRepository.findAll().stream()
                .filter(data -> data.getSido().equals(sido))
                .map(data -> data.getRegion())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
