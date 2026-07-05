package com.example.mock.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoadConditionGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(RoadConditionGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO road_condition (time_id, alert_count, congestion_index, efficiency, " +
                    "abnormal_events, avg_speed, wait_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private final Random random = new Random();
    private List<Integer> timeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty()) {
            log.error("缺少时间维度数据，无法生成道路情况数据");
            return;
        }

        // 确保获取14个time_id，不足则抛出异常
        if (timeIds.size() < 14) {
            throw new SQLException("time_dim表中不足14条记录，无法生成两周数据");
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            // 只处理前14条记录
            for (int i = 0; i < 14; i++) {
                int timeId = timeIds.get(i);

                // 生成随机但合理的数据
                int alertCount = generateAlertCount();
                int abnormalEvents = generateAbnormalEvents();
                double congestionIndex = generateCongestionIndex();
                int avgSpeed = calculateAvgSpeed(congestionIndex);
                double efficiency = generateEfficiency();
                int waitTime = calculateWaitTime(efficiency);

                // 设置参数
                pstmt.setInt(1, timeId);
                pstmt.setInt(2, alertCount);
                pstmt.setDouble(3, congestionIndex);
                pstmt.setDouble(4, efficiency);
                pstmt.setInt(5, abnormalEvents);
                pstmt.setInt(6, avgSpeed);
                pstmt.setInt(7, waitTime);
                pstmt.addBatch();

                log.debug("生成第{}条道路数据: timeId={}, 警情={}, 拥堵={}, 速度={}km/h",
                        i+1, timeId, alertCount, congestionIndex, avgSpeed);
            }

            pstmt.executeBatch();
            log.info("成功生成14条道路情况数据（两周数据）");
        }
    }

    private int generateAlertCount() {
        return 100 + random.nextInt(901); // 100-1000次警情
    }

    private int generateAbnormalEvents() {
        return 50 + random.nextInt(251); // 50-300次异常事件
    }

    private double generateCongestionIndex() {
        return 3 + random.nextDouble() * 7; // 3.0-10.0
    }

    private int calculateAvgSpeed(double congestionIndex) {
        // 拥堵指数越高，速度越低
        return (int)(40 * (1 - (congestionIndex - 3) / 7)); // 0-40km/h
    }

    private double generateEfficiency() {
        return 5 + random.nextDouble() * 15; // 5.0-20.0 km/h
    }

    private int calculateWaitTime(double efficiency) {
        // 效率越高，等待时间越短
        return (int)(6 * (1 - (efficiency - 5) / 15)); // 0-6分钟
    }

    private void loadTimeIds() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT time_id FROM time_dim ORDER BY time_id LIMIT 14");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                timeIds.add(rs.getInt("time_id"));
            }
        }
    }
}