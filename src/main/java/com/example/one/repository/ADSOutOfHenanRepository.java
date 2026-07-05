package com.example.one.repository;

import com.example.one.pojo.ADSOutOfHenan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ADSOutOfHenanRepository extends JpaRepository<ADSOutOfHenan, Long> {

    @Query("SELECT DISTINCT new map(" +
            "o.fromCityName as fromCityName, " +
            "o.fromLongitude as fromLongitude, " +
            "o.fromLatitude as fromLatitude, " +
            "o.toCityName as toCityName, " +
            "o.toLongitude as toLongitude, " +
            "o.toLatitude as toLatitude, " +
            "o.vehicleCount as vehicleCount) " +
            "FROM ADSOutOfHenan o")
    List<Map<String, Object>> findAllDistinctCityPairs();
}
