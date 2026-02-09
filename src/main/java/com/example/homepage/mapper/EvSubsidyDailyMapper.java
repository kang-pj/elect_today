package com.example.homepage.mapper;

import com.example.homepage.entity.EvSubsidyDaily;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EvSubsidyDailyMapper {
    
    @Select("SELECT * FROM ev_subsidy_daily WHERE target_date = #{targetDate}")
    List<EvSubsidyDaily> findByTargetDate(LocalDate targetDate);
    
    @Select("SELECT * FROM ev_subsidy_daily WHERE target_date = #{targetDate} AND sido = #{sido} AND region = #{region} LIMIT 1")
    EvSubsidyDaily findByTargetDateAndSidoAndRegion(@Param("targetDate") LocalDate targetDate, 
                                                     @Param("sido") String sido, 
                                                     @Param("region") String region);
    
    @Select("SELECT * FROM ev_subsidy_daily WHERE sido = #{sido} AND region = #{region} ORDER BY target_date DESC")
    List<EvSubsidyDaily> findBySidoAndRegionOrderByTargetDateDesc(@Param("sido") String sido, 
                                                                   @Param("region") String region);
    
    @Insert("INSERT INTO ev_subsidy_daily (target_date, sido, region, daily_received, daily_delivered, daily_remaining_change, created_at) " +
            "VALUES (#{targetDate}, #{sido}, #{region}, #{dailyReceived}, #{dailyDelivered}, #{dailyRemainingChange}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EvSubsidyDaily evSubsidyDaily);
}
