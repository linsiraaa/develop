package com.example.mock.generator;

import com.example.mock.mockbean.PassengerFreightByVehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassengerFreightByVehicleGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(PassengerFreightByVehicleGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO passenger_freight_by_vehicle (time_id, vehicle_type_id, passenger_volume, freight_volume) VALUES (?, ?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<Integer> vehicleTypeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();
        loadVehicleTypeIds();

        if (timeIds.isEmpty() || vehicleTypeIds.isEmpty()) {
            log.error("缺少时间维度或交通工具类型数据，无法生成按交通工具类型细分的客货运量数据");
            return;
        }

        int generatedCount = 0;

        for (int timeId : timeIds) {
            for (int vehicleTypeId : vehicleTypeIds) {
                if (generatedCount >= count) break;

                long passengerVolume = 1000L + (long)(random.nextDouble() * (10000L - 1000L + 1));
                long freightVolume = 5000L + (long)(random.nextDouble() * (50000L - 5000L + 1));

                executeInsertAndGetId(INSERT_SQL, timeId, vehicleTypeId, passengerVolume, freightVolume);

                generatedCount++;
            }
            if (generatedCount >= count) break;
        }

        log.info("成功生成 {} 条按交通工具类型细分的客货运量数据", generatedCount);
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