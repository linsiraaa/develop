package com.example.one.repository;

import com.example.one.pojo.ToolFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ToolFlowRepository extends JpaRepository<ToolFlow, Long> {
    @Query("SELECT tf.flowValue AS value, tf.vehicleType AS type, tf.month AS month " +
            "FROM ToolFlow tf " +
            "WHERE tf.month IN :months " +
            "ORDER BY tf.vehicleType, tf.month")
    List<Object[]> findGroupedByMonthAndType(List<String> months);
}
