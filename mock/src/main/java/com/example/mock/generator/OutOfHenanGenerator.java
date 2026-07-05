package com.example.mock.generator;

import com.example.mock.generator.CityGenerator;
import com.example.mock.generator.DataGenerator;

import com.example.mock.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OutOfHenanGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(OutOfHenanGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO out_of_henan (time_id, from_city_id, to_city_id, vehicle_count) VALUES (?, ?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<City> henanCities;
    private List<City> nonHenanCities;
    private final Random random = new Random();

    public OutOfHenanGenerator(CityGenerator cityGenerator) {
        if (cityGenerator == null) {
            throw new IllegalArgumentException("CityGenerator cannot be null");
        }

        this.henanCities = cityGenerator.getHenanCities();
        this.nonHenanCities = cityGenerator.getNonHenanCities();

        log.debug("初始化 OutOfHenanGenerator: 河南城市数量={}, 非河南城市数量={}",
                henanCities.size(), nonHenanCities.size());
    }

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty()) {
            log.error("缺少时间维度数据，无法生成出豫车辆数据");
            return;
        }

        if (henanCities.isEmpty() || nonHenanCities.isEmpty()) {
            log.error("城市数据不完整: 河南城市={}, 非河南城市={}",
                    henanCities.size(), nonHenanCities.size());
            return;
        }

        // 为每个河南城市分配多个外省城市作为目的地
        List<City> shuffledNonHenanCities = new ArrayList<>(nonHenanCities);
        Collections.shuffle(shuffledNonHenanCities);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            for (City fromCity : henanCities) {
                // 每个河南城市连接多个外省城市(3-5个)
                int connections = 3 + random.nextInt(3);
                List<City> selectedNonHenanCities = shuffledNonHenanCities.subList(0, Math.min(connections, shuffledNonHenanCities.size()));

                for (City toCity : selectedNonHenanCities) {
                    for (int i = 0; i < count / henanCities.size(); i++) {
                        int timeId = timeIds.get(random.nextInt(timeIds.size()));
                        long vehicleCount = 100L + (long)(random.nextDouble() * (1000L - 100L + 1));

                        pstmt.setInt(1, timeId);
                        pstmt.setInt(2, fromCity.getCityId());
                        pstmt.setInt(3, toCity.getCityId());
                        pstmt.setLong(4, vehicleCount);
                        pstmt.addBatch();
                    }
                }
            }
            pstmt.executeBatch();
            log.info("成功生成出豫车辆数据，每个河南城市连接3-5个外省城市");
        }
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