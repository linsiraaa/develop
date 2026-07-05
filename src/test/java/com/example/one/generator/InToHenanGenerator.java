package com.example.one.generator;

// InToHenanGenerator.java


import com.example.one.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InToHenanGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(InToHenanGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO in_to_henan (time_id, from_city_id, vehicle_count) VALUES (?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<City> nonHenanCities;

    public InToHenanGenerator(CityGenerator cityGenerator) {
        if (cityGenerator == null) {
            throw new IllegalArgumentException("CityGenerator cannot be null");
        }

        this.nonHenanCities = cityGenerator.getNonHenanCities();

        log.debug("初始化 InToHenanGenerator: 非河南城市数量={}", nonHenanCities.size());
    }

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty()) {
            log.error("缺少时间维度数据，无法生成访豫车辆数据");
            return;
        }

        if (nonHenanCities.isEmpty()) {
            log.error("非河南城市数据为空，无法生成访豫车辆数据");
            return;
        }

        for (int i = 0; i < count; i++) {
            int timeId = timeIds.get(random.nextInt(timeIds.size()));
            City fromCity = nonHenanCities.get(random.nextInt(nonHenanCities.size()));

            log.debug("生成访豫数据: 时间={}, 来源地={}", timeId, fromCity.getCityName());

            long vehicleCount = 100L + (long)(random.nextDouble() * (1000L - 100L + 1));;

            executeInsertAndGetId(INSERT_SQL, timeId, fromCity.getCityId(), vehicleCount);
        }

        log.info("成功生成 {} 条访豫车辆数据", count);
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