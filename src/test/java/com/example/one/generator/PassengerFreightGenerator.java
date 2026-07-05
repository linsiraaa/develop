package com.example.one.generator;

// PassengerFreightGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassengerFreightGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(PassengerFreightGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO passenger_freight (time_id, passenger_volume, freight_volume) VALUES (?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty()) {
            log.error("缺少时间维度数据，无法生成客货运量数据");
            return;
        }

        for (int timeId : timeIds) {
            long passengerVolume = 1000L + (long)(random.nextDouble() * (10000L - 1000L + 1));
            long freightVolume = 5000L + (long)(random.nextDouble() * (50000L - 5000L + 1));

            executeInsertAndGetId(INSERT_SQL, timeId, passengerVolume, freightVolume);
        }

        log.info("成功生成 {} 条客货运量数据", timeIds.size());
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