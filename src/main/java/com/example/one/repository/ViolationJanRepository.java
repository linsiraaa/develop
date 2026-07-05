package com.example.one.repository;

import com.example.one.pojo.ViolationJan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViolationJanRepository extends JpaRepository<ViolationJan, Long> {
    @Query("SELECT v.violationName as violationName, SUM(v.violationCount) as violationCount " +
            "FROM ViolationJan v " +
            "WHERE v.month = :month " +
            "GROUP BY v.violationName " +
            "ORDER BY SUM(v.violationCount) DESC")
    List<Object[]> findViolationCountsByMonth(@Param("month") Integer month);

    @Query("SELECT MAX(v.month) FROM ViolationJan v")
    Integer findLatestMonth();
}