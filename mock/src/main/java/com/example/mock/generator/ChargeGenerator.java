package com.example.mock.generator;


import com.example.mock.generator.CityGenerator;
import com.example.mock.generator.DataGenerator;
import com.example.mock.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ChargeGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(ChargeGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO charge (city_id, vehicle_total, today_online, today_alarm) VALUES (?, ?, ?, ?)";

    private final List<City> henanCities;
    private final Random random = new Random();

    public ChargeGenerator(CityGenerator cityGenerator) {
        if (cityGenerator == null) {
            throw new IllegalArgumentException("CityGenerator cannot be null");
        }
        this.henanCities = cityGenerator.getHenanCities();
        log.debug("初始化 ChargeGenerator: 河南城市数量={}", henanCities.size());
    }

    @Override
    public void generate(int count) throws SQLException {
        if (henanCities.isEmpty()) {
            log.error("河南城市数据为空，无法生成收费统计数据");
            return;
        }

        // 确保必须包含的3个主要城市存在
        Map<String, City> mustHaveCities = henanCities.stream()
                .filter(c -> Arrays.asList("郑州", "开封", "洛阳").contains(c.getCityName()))
                .collect(Collectors.toMap(City::getCityName, c -> c));

        if (mustHaveCities.size() < 3) {
            log.error("缺少必需的城市数据: 郑州={}, 开封={}, 洛阳={}",
                    mustHaveCities.containsKey("郑州"),
                    mustHaveCities.containsKey("开封"),
                    mustHaveCities.containsKey("洛阳"));
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            for (int i = 0; i < count; i++) {
                Map<City, int[]> cityData = allocateValues(henanCities, mustHaveCities.get("郑州"));

                // 调整总和到80-100范围
                adjustTotals(cityData);

                // 验证总和
                validateTotals(cityData);

                for (Map.Entry<City, int[]> entry : cityData.entrySet()) {
                    City city = entry.getKey();
                    int[] values = entry.getValue();

                    pstmt.setInt(1, city.getCityId());
                    pstmt.setInt(2, values[0]);
                    pstmt.setInt(3, values[1]);
                    pstmt.setInt(4, values[2]);
                    pstmt.addBatch();

                    log.debug("生成数据[{}]: 城市={}({}), 总数={}, 在线={}, 告警={}",
                            i, city.getCityName(), city.getCityId(),
                            values[0], values[1], values[2]);
                }
            }

            pstmt.executeBatch();
            log.info("成功生成{}组数据，每组{}条，各项总和80-100", count, henanCities.size());
        }
    }

    private Map<City, int[]> allocateValues(List<City> cities, City zhengzhou) {
        Map<City, int[]> result = new HashMap<>();

        // 郑州分配35-45的值（确保最大）
        int zhengzhouTotal = 35 + random.nextInt(11);  // 35-45
        int zhengzhouOnline = 35 + random.nextInt(11); // 35-45
        int zhengzhouAlarm = 35 + random.nextInt(11);  // 35-45

        result.put(zhengzhou, new int[]{zhengzhouTotal, zhengzhouOnline, zhengzhouAlarm});

        // 为其他城市分配初始值
        List<City> otherCities = new ArrayList<>(cities);
        otherCities.remove(zhengzhou);
        Collections.shuffle(otherCities);

        for (City city : otherCities) {
            // 初始分配较小值（后面会调整）
            int total = 1 + random.nextInt(10);
            int online = 1 + random.nextInt(10);
            int alarm = 1 + random.nextInt(10);

            result.put(city, new int[]{total, online, alarm});
        }

        return result;
    }

    private void adjustTotals(Map<City, int[]> cityData) {
        // 计算当前总和
        int currentVehicle = cityData.values().stream().mapToInt(arr -> arr[0]).sum();
        int currentOnline = cityData.values().stream().mapToInt(arr -> arr[1]).sum();
        int currentAlarm = cityData.values().stream().mapToInt(arr -> arr[2]).sum();

        // 目标总和范围80-100
        int targetVehicle = 80 + random.nextInt(21); // 80-100
        int targetOnline = 80 + random.nextInt(21);  // 80-100
        int targetAlarm = 80 + random.nextInt(21);   // 80-100

        // 调整vehicleTotal
        scaleValues(cityData, 0, currentVehicle, targetVehicle);
        // 调整todayOnline
        scaleValues(cityData, 1, currentOnline, targetOnline);
        // 调整todayAlarm
        scaleValues(cityData, 2, currentAlarm, targetAlarm);
    }

    private void scaleValues(Map<City, int[]> cityData, int index, int currentSum, int targetSum) {
        if (currentSum == 0) return;

        float scaleFactor = (float) targetSum / currentSum;

        // 第一轮：应用缩放因子并取整
        int roundedSum = 0;
        for (int[] values : cityData.values()) {
            values[index] = Math.max(1, Math.round(values[index] * scaleFactor));
            roundedSum += values[index];
        }

        // 调整差值
        int diff = targetSum - roundedSum;
        if (diff != 0) {
            List<City> cities = new ArrayList<>(cityData.keySet());
            Collections.shuffle(cities);

            int adjustPerCity = diff > 0 ? 1 : -1;
            int remainingAdjust = Math.abs(diff);

            for (City city : cities) {
                if (remainingAdjust == 0) break;

                int[] values = cityData.get(city);
                int newValue = values[index] + adjustPerCity;
                if (newValue >= 1) {
                    values[index] = newValue;
                    remainingAdjust--;
                }
            }
        }
    }

    private void validateTotals(Map<City, int[]> cityData) {
        int totalVehicle = cityData.values().stream().mapToInt(arr -> arr[0]).sum();
        int totalOnline = cityData.values().stream().mapToInt(arr -> arr[1]).sum();
        int totalAlarm = cityData.values().stream().mapToInt(arr -> arr[2]).sum();

        if (totalVehicle < 80 || totalVehicle > 100 ||
                totalOnline < 80 || totalOnline > 100 ||
                totalAlarm < 80 || totalAlarm > 100) {
            throw new IllegalStateException(String.format(
                    "数据校验失败: vehicleTotal=%d, todayOnline=%d, todayAlarm=%d (应在80-100之间)",
                    totalVehicle, totalOnline, totalAlarm));
        }

        log.debug("校验通过: vehicleTotal={}, todayOnline={}, todayAlarm={}",
                totalVehicle, totalOnline, totalAlarm);
    }
}