// 文档3: 修改后的ChargeRepository
package com.example.one.repository;

import com.example.one.pojo.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer> {

    @Query(nativeQuery = true, value =
            "SELECT c.city_name as cityName, " +
                    "ch.vehicle_total as vehicleTotal, " +
                    "ch.today_online as todayOnline, " +
                    "ch.today_alarm as todayAlarm " +
                    "FROM charge ch " +
                    "INNER JOIN city c ON ch.city_id = c.city_id " +
                    "ORDER BY vehicleTotal DESC")
    List<Object[]> findChargeStats();
}