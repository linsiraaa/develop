package com.example.one.repository;

import com.example.one.pojo.TollStationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TollStationRepository extends JpaRepository<TollStationData, Long> {
    List<TollStationData> findByCityOrderByDailyRevenueDesc(String city);

    @Query("SELECT t FROM TollStationData t WHERE t.city = ?1 ORDER BY t.dailyRevenue DESC")
    List<TollStationData> findTop10ByCity(String city);

    List<TollStationData> findByDateBetweenOrderByDailyRevenueDesc(Date startDate, Date endDate);
}