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
import java.util.*;

public class InToHenanGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(InToHenanGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO in_to_henan (time_id, from_city_id, to_city_id, vehicle_count) VALUES (?, ?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<City> nonHenanCities;
    private List<City> henanCities;
    private final Random random = new Random();

    public InToHenanGenerator(CityGenerator cityGenerator) {
        if (cityGenerator == null) {
            throw new IllegalArgumentException("CityGenerator cannot be null");
        }

        this.nonHenanCities = cityGenerator.getNonHenanCities();
        this.henanCities = cityGenerator.getHenanCities();

        log.debug("初始化 InToHenanGenerator: 非河南城市数量={}, 河南城市数量={}",
                nonHenanCities.size(), henanCities.size());
    }

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty()) {
            log.error("缺少时间维度数据，无法生成访豫车辆数据");
            return;
        }

        if (nonHenanCities.isEmpty() || henanCities.isEmpty()) {
            log.error("城市数据不完整: 非河南城市={}, 河南城市={}",
                    nonHenanCities.size(), henanCities.size());
            return;
        }

        // 打乱城市顺序确保随机分配
        Collections.shuffle(nonHenanCities);
        Collections.shuffle(henanCities);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            // 为每个外省城市分配1-3个河南城市
            for (City fromCity : nonHenanCities) {
                // 随机决定连接1-3个河南城市
                int connections = 1 + random.nextInt(3);
                Set<City> connectedHenanCities = new HashSet<>();

                // 确保不重复连接同一个河南城市
                while (connectedHenanCities.size() < connections && !henanCities.isEmpty()) {
                    City toCity = henanCities.get(random.nextInt(henanCities.size()));
                    if (connectedHenanCities.add(toCity)) {
                        // 为每条连接生成多条时间记录
                        for (int i = 0; i < count / nonHenanCities.size(); i++) {
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
            }
            pstmt.executeBatch();
            log.info("成功生成访豫车辆数据，每个外省城市连接1-3个河南城市，每条连接有多个时间记录");
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