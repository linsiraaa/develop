package com.example.mock.generator;

// TrafficViolationGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrafficViolationGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(TrafficViolationGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO traffic_violation (time_id, violation_id, violation_count) VALUES (?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<Integer> violationIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        // 加载时间维度和违规类型的ID
        loadTimeIds();
        loadViolationIds();

        if (timeIds.isEmpty() || violationIds.isEmpty()) {
            log.error("缺少时间维度或违规类型数据，无法生成交通违规数据");
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            int generatedCount = 0;

            // 为每个时间点和每种违规类型生成数据
            for (int timeId : timeIds) {
                for (int violationId : violationIds) {
                    if (generatedCount >= count) break;

                    // 生成随机违规次数，不同违规类型有不同的发生频率
                    int violationCount;
                    switch (violationId) {
                        case 1: // 超速行驶
                            violationCount = 50+ (int)(random.nextDouble() * (200 - 5 + 1));
                            break;
                        case 2: // 闯红灯
                            violationCount = 20+ (int)(random.nextDouble() * (80 - 20 + 1));
                            break;
                        case 3: // 违规变道
                            violationCount = 30+(int) (random.nextDouble() * (150 - 30 + 1));
                            break;
                        case 4: // 不按导向车道行驶
                            violationCount = 40+ (int)(random.nextDouble() * (180 - 40 + 1));
                            break;
                        case 5: // 占用应急车道
                            violationCount = 50+ (int)(random.nextDouble() *( 150 - 10 + 1));
                            break;
                        case 6: // 酒后驾驶
                            violationCount = 5+(int) (random.nextDouble() * (20 - 5 + 1));
                            break;
                        case 7: // 无证驾驶
                            violationCount = 3+(int) (random.nextDouble() * (15 - 3 + 1));
                            break;
                        case 8: // 疲劳驾驶
                            violationCount = 10+(int) (random.nextDouble() * (40 - 10 + 1));
                            break;
                        case 9: // 未系安全带
                            violationCount = 40+ (int)(random.nextDouble() * (200 - 40 + 1));
                            break;
                        case 10: // 遮挡号牌
                            violationCount = 2+(int) (random.nextDouble() * (10 - 2 + 1));
                            break;
                        default:
                            violationCount = 10+(int) (random.nextDouble() * (100 - 10 + 1));
                    }

                    pstmt.setInt(1, timeId);
                    pstmt.setInt(2, violationId);
                    pstmt.setInt(3, violationCount);
                    pstmt.executeUpdate();

                    generatedCount++;
                }
                if (generatedCount >= count) break;
            }

            log.info("成功生成 {} 条交通违规数据", generatedCount);
        }
    }

    private void loadTimeIds() throws SQLException {
        String sql = "SELECT time_id FROM time_dim";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                timeIds.add(rs.getInt("time_id"));
            }
        }
    }

    private void loadViolationIds() throws SQLException {
        String sql = "SELECT violation_id FROM violation_type";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                violationIds.add(rs.getInt("violation_id"));
            }
        }
    }
}
