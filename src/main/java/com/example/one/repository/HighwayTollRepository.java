package com.example.one.repository;

import com.example.one.pojo.HighwayToll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HighwayTollRepository extends JpaRepository<HighwayToll, BigDecimal> {

    // 方案1：原生SQL直接按金额排序（无ID操作）
    @Query(value = "SELECT station_name, toll_amount, dt " +
            "FROM ads_toll_collection_rank " +
            "ORDER BY toll_amount DESC " +
            "LIMIT 5",
            nativeQuery = true)
    List<HighwayToll> findTop5ByAmount();

    // 方案2：JPQL去重版（通过收费站名称分组）
    @Query("SELECT h FROM HighwayToll h " +
            "WHERE h.tollAmount = (SELECT MAX(h2.tollAmount) FROM HighwayToll h2 WHERE h2.stationName = h.stationName) " +
            "ORDER BY h.tollAmount DESC")
    List<HighwayToll> findTop5DistinctStations();
}