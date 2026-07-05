package com.example.one.repository;

import com.example.one.pojo.AdsTodayVehicleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsTodayVehicleInfoRepository extends JpaRepository<AdsTodayVehicleInfo, Integer> {
    @Query("SELECT a FROM AdsTodayVehicleInfo a WHERE a.id = ?1")
    AdsTodayVehicleInfo findFirstById(int id);
}