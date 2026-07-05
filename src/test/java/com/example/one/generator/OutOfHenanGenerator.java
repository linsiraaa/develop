package com.example.one.generator;

// OutOfHenanGenerator.java


import com.example.one.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OutOfHenanGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(OutOfHenanGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO out_of_henan (time_id, from_city_id, to_city_id, vehicle_count) VALUES (?, ?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<City> henanCities;
    private List<City> nonHenanCities;

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

        for (int i = 0; i < count; i++) {
            int timeId = timeIds.get(random.nextInt(timeIds.size()));
            City fromCity = henanCities.get(random.nextInt(henanCities.size()));
            City toCity = nonHenanCities.get(random.nextInt(nonHenanCities.size()));

            log.debug("生成出豫数据: 时间={}, 出发地={}, 目的地={}",
                    timeId, fromCity.getCityName(), toCity.getCityName());

            long vehicleCount = 100L + (long)(random.nextDouble() * (1000L - 100L + 1));

            executeInsertAndGetId(INSERT_SQL, timeId, fromCity.getCityId(), toCity.getCityId(), vehicleCount);
        }

        log.info("成功生成 {} 条出豫车辆数据", count);
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