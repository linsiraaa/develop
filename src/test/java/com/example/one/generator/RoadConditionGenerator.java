package com.example.one.generator;

// RoadConditionGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoadConditionGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(RoadConditionGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO road_condition (time_id, alert_count, congestion_index, efficiency, abnormal_events) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty()) {
            log.error("缺少时间维度数据，无法生成道路情况数据");
            return;
        }

        for (int timeId : timeIds) {
            int alertCount = 100 + (int)(random.nextDouble() * (1000 - 100 + 1));
            double congestionIndex = random.nextDouble() * 7 + 3;
            double efficiency = random.nextDouble() * 40 + 20;
            int abnormalEvents = 50+ (int)(random.nextDouble() * (300 - 50 + 1));;

            executeInsertAndGetId(INSERT_SQL, timeId, alertCount, congestionIndex, efficiency, abnormalEvents);
        }

        log.info("成功生成 {} 条道路情况数据", timeIds.size());
    }

    private void loadTimeIds() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT time_id FROM time_dim");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                timeIds.add(rs.getInt("time_id"));
            }
        }
    }
}