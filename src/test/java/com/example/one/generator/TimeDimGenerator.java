package com.example.one.generator;

// TimeDimGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeDimGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(TimeDimGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO time_dim (year, month, month_key) VALUES (?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < count; i++) {
            LocalDate date = currentDate.minusMonths(i);
            int year = date.getYear();
            int month = date.getMonthValue();
            String monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            int timeId = executeInsertAndGetId(INSERT_SQL, year, month, monthKey);
            timeIds.add(timeId);
        }

        log.info("成功生成 {} 条时间维度数据", count);
    }

    public List<Integer> getTimeIds() {
        return timeIds;
    }
}