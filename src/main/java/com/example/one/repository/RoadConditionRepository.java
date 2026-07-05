package com.example.one.repository;

import com.example.one.pojo.RoadCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadConditionRepository extends JpaRepository<RoadCondition, Integer> {

    @Query(value = "SELECT rc_id as id, alert_count, congestion_index, efficiency, abnormal_events, avg_speed, wait_time, null as dt FROM road_condition ORDER BY rc_id ASC LIMIT 14", nativeQuery = true)
    List<RoadCondition> findFirst14ByOrderByTimeIdAsc();
    
    @Query(value = "SELECT rc_id as id, alert_count, congestion_index, efficiency, abnormal_events, avg_speed, wait_time, null as dt FROM road_condition LIMIT 1", nativeQuery = true)
    RoadCondition findFirstRecord();
}
