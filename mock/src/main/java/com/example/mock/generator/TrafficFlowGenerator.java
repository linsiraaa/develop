package com.example.mock.generator;

// TrafficFlowGenerator.java


import com.example.mock.generator.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrafficFlowGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(TrafficFlowGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO traffic_flow (time_id, type_id, flow_count) VALUES (?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<Integer> roadTypeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();
        loadRoadTypeIds();

        if (timeIds.isEmpty() || roadTypeIds.isEmpty()) {
            log.error("缺少时间维度或公路类型数据，无法生成交通流量数据");
            return;
        }

        int generatedCount = 0;

        for (int timeId : timeIds) {
            for (int roadTypeId : roadTypeIds) {
                if (generatedCount >= count) break;

                long flowCount = 1000l + (long) (random.nextDouble() * (10000 - 1000 + 1));

                executeInsertAndGetId(INSERT_SQL, timeId, roadTypeId, flowCount);

                generatedCount++;
            }
            if (generatedCount >= count) break;
        }

        log.info("成功生成 {} 条交通流量数据", generatedCount);
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

    private void loadRoadTypeIds() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT type_id FROM road_type");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                roadTypeIds.add(rs.getInt("type_id"));
            }
        }
    }
}