package com.example.one.repository;

import com.example.one.pojo.AdsTrafficFlowAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsTrafficFlowAnalysisRepository extends JpaRepository<AdsTrafficFlowAnalysis, Long> {

    @Query(value = "SELECT * FROM ads_traffic_flow_analysis WHERE year = 2025", nativeQuery = true)
    List<AdsTrafficFlowAnalysis> find2025Data();

    @Query(value = "SELECT " +
            "year, month, type_name, " +
            "SUM(current_flow) AS current_flow, " +
            "SUM(last_year_flow) AS last_year_flow, " +
            "AVG(yoy_growth_percent) AS yoy_growth_percent, " +
            "AVG(mom_growth_percent) AS mom_growth_percent " +
            "FROM ads_traffic_flow_analysis " +
            "WHERE year = 2025 " +
            "GROUP BY year, month, type_name", nativeQuery = true)
    List<Object[]> findAggregated2025Data();
}