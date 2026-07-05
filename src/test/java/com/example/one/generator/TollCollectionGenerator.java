package com.example.one.generator;

// TollCollectionGenerator.java



import com.example.one.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TollCollectionGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(TollCollectionGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO toll_collection (time_id, station_name, station_city_id, toll_amount, vehicle_count) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private List<Integer> timeIds = new ArrayList<>();
    private List<City> henanCities;

    // 预定义的收费站名称前缀
    private static final String[] STATION_PREFIXES = {
            "京港澳高速", "连霍高速", "大广高速", "二广高速", "沪陕高速",
            "宁洛高速", "郑民高速", "郑少洛高速", "商周高速", "焦桐高速"
    };

    public TollCollectionGenerator(CityGenerator cityGenerator) {
        if (cityGenerator == null) {
            throw new IllegalArgumentException("CityGenerator cannot be null");
        }

        this.henanCities = cityGenerator.getHenanCities();

        log.debug("初始化 TollCollectionGenerator: 河南城市数量={}", henanCities.size());
    }

    @Override
    public void generate(int count) throws SQLException {
        loadTimeIds();

        if (timeIds.isEmpty() || henanCities.isEmpty()) {
            log.error("缺少必要数据，无法生成收费站数据");
            return;
        }

        // 使用集合记录已生成的组合，避免重复
        Set<String> generatedKeys = new HashSet<>();
        int generatedCount = 0;

        // 尝试生成指定数量的记录，或达到最大尝试次数
        int maxAttempts = count * 2;
        int attempts = 0;

        while (generatedCount < count && attempts < maxAttempts) {
            attempts++;

            int timeId = timeIds.get(random.nextInt(timeIds.size()));
            City city = henanCities.get(random.nextInt(henanCities.size()));

            String prefix = STATION_PREFIXES[random.nextInt(STATION_PREFIXES.length)];
            String stationName = prefix + city.getCityName() + "站";

            // 生成唯一键
            String key = timeId + "-" + stationName;

            // 如果已存在，跳过此次尝试
            if (generatedKeys.contains(key)) {
                continue;
            }

            generatedKeys.add(key);

            log.debug("生成收费站数据: 时间={}, 站点={}", timeId, stationName);

            double tollAmount = random.nextDouble() * 1000000 + 100000;
            long vehicleCount = 10000L + (long)(random.nextDouble() * (100000L - 10000L + 1));;

            executeInsertAndGetId(INSERT_SQL, timeId, stationName, city.getCityId(), tollAmount, vehicleCount);
            generatedCount++;
        }

        log.info("成功生成 {} 条收费站数据（尝试次数: {}）", generatedCount, attempts);
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