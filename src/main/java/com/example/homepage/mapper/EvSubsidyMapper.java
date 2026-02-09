package com.example.homepage.mapper;

import com.example.homepage.entity.EvSubsidy;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EvSubsidyMapper {
    
    @Select("SELECT * FROM ev_subsidy WHERE crawl_date = #{crawlDate}")
    List<EvSubsidy> findByCrawlDate(LocalDate crawlDate);
    
    @Select("SELECT * FROM ev_subsidy WHERE crawl_date = #{crawlDate} AND sido = #{sido} AND region = #{region} LIMIT 1")
    EvSubsidy findByCrawlDateAndSidoAndRegion(@Param("crawlDate") LocalDate crawlDate, 
                                               @Param("sido") String sido, 
                                               @Param("region") String region);
    
    @Select("SELECT * FROM ev_subsidy WHERE crawl_date = #{crawlDate} AND sido = #{sido} AND region = #{region} AND car_type = #{carType} LIMIT 1")
    EvSubsidy findByCrawlDateAndSidoAndRegionAndCarType(@Param("crawlDate") LocalDate crawlDate, 
                                                         @Param("sido") String sido, 
                                                         @Param("region") String region, 
                                                         @Param("carType") String carType);
    
    @Select("SELECT * FROM ev_subsidy WHERE sido = #{sido} AND region = #{region} ORDER BY crawl_date DESC")
    List<EvSubsidy> findBySidoAndRegionOrderByCrawlDateDesc(@Param("sido") String sido, 
                                                             @Param("region") String region);
    
    @Select("SELECT * FROM ev_subsidy WHERE sido = #{sido} AND region = #{region} ORDER BY crawl_date ASC")
    List<EvSubsidy> findBySidoAndRegionOrderByCrawlDateAsc(@Param("sido") String sido, 
                                                            @Param("region") String region);
    
    @Select("SELECT * FROM ev_subsidy WHERE crawl_date = #{crawlDate} AND data_type = #{dataType}")
    List<EvSubsidy> findByCrawlDateAndDataType(@Param("crawlDate") LocalDate crawlDate, 
                                                @Param("dataType") String dataType);
    
    @Select("SELECT * FROM ev_subsidy WHERE crawl_date = #{crawlDate} AND sido = #{sido} AND region = #{region} AND data_type = #{dataType} LIMIT 1")
    EvSubsidy findByCrawlDateAndSidoAndRegionAndDataType(@Param("crawlDate") LocalDate crawlDate, 
                                                          @Param("sido") String sido, 
                                                          @Param("region") String region, 
                                                          @Param("dataType") String dataType);
    
    @Select("SELECT * FROM ev_subsidy WHERE sido = #{sido} AND region = #{region} AND data_type = #{dataType} ORDER BY crawl_date ASC")
    List<EvSubsidy> findBySidoAndRegionAndDataTypeOrderByCrawlDateAsc(@Param("sido") String sido, 
                                                                       @Param("region") String region, 
                                                                       @Param("dataType") String dataType);
    
    @Insert("INSERT INTO ev_subsidy (crawl_date, sido, region, car_type, data_type, " +
            "total_announced, priority_announced, corporation_announced, taxi_announced, general_announced, " +
            "total_received, priority_received, corporation_received, taxi_received, general_received, " +
            "total_delivered, priority_delivered, corporation_delivered, taxi_delivered, general_delivered, " +
            "created_at) " +
            "VALUES (#{crawlDate}, #{sido}, #{region}, #{carType}, #{dataType}, " +
            "#{totalAnnounced}, #{priorityAnnounced}, #{corporationAnnounced}, #{taxiAnnounced}, #{generalAnnounced}, " +
            "#{totalReceived}, #{priorityReceived}, #{corporationReceived}, #{taxiReceived}, #{generalReceived}, " +
            "#{totalDelivered}, #{priorityDelivered}, #{corporationDelivered}, #{taxiDelivered}, #{generalDelivered}, " +
            "NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EvSubsidy evSubsidy);
    
    @Update("UPDATE ev_subsidy SET " +
            "total_announced = #{totalAnnounced}, priority_announced = #{priorityAnnounced}, " +
            "corporation_announced = #{corporationAnnounced}, taxi_announced = #{taxiAnnounced}, " +
            "general_announced = #{generalAnnounced}, " +
            "total_received = #{totalReceived}, priority_received = #{priorityReceived}, " +
            "corporation_received = #{corporationReceived}, taxi_received = #{taxiReceived}, " +
            "general_received = #{generalReceived}, " +
            "total_delivered = #{totalDelivered}, priority_delivered = #{priorityDelivered}, " +
            "corporation_delivered = #{corporationDelivered}, taxi_delivered = #{taxiDelivered}, " +
            "general_delivered = #{generalDelivered} " +
            "WHERE id = #{id}")
    int update(EvSubsidy evSubsidy);
}
