package com.example.mock.generator;

// VehicleTrafficGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleTrafficGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(VehicleTrafficGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO vehicle_traffic (time_id, vehicle_type_id, vehicle_count) VALUES (?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<Integer> vehicleTypeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();
        loadVehicleTypeIds();

        if (timeIds.isEmpty() || vehicleTypeIds.isEmpty()) {
            log.error("缺少时间维度或交通工具类型数据，无法生成交通工具流量数据");
            return;
        }

        int generatedCount = 0;

        for (int timeId : timeIds) {
            for (int vehicleTypeId : vehicleTypeIds) {
                if (generatedCount >= count) break;

                long vehicleCount = 5000L + (long)(random.nextDouble() * (50000L - 1000L + 1));

                executeInsertAndGetId(INSERT_SQL, timeId, vehicleTypeId, vehicleCount);

                generatedCount++;
            }
            if (generatedCount >= count) break;
        }

        log.info("成功生成 {} 条交通工具流量数据", generatedCount);
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

    private void loadVehicleTypeIds() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT vehicle_type_id FROM vehicle_type");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                vehicleTypeIds.add(rs.getInt("vehicle_type_id"));
            }
        }
    }
}