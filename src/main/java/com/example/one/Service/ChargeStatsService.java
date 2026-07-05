package com.example.one.Service;

import com.example.one.pojo.Charge;
import com.example.one.repository.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChargeStatsService {

    @Autowired
    private ChargeRepository chargeRepository;
    
    @Autowired
    private RedisDataService redisDataService;

    public Map<String, Object> getChargeStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 优先从Redis获取
        List<Map<String, Object>> redisData = redisDataService.getAllVehicleInfo();
        if (redisData != null && !redisData.isEmpty()) {
            return buildFromRedisVehicleData(redisData);
        }

        // Redis无数据时从MySQL获取
        List<Charge> allStats = chargeRepository.findChargeStats().stream()
                .map(row -> new Charge(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).intValue()
                ))
                .collect(Collectors.toList());

        Charge provinceTotal = calculateProvinceTotal(allStats);
        List<Charge> top9Cities = allStats.stream().limit(9).collect(Collectors.toList());

        result.put("provinceTotal", provinceTotal);
        result.put("topCities", top9Cities);
        return result;
    }
    
    // 从Redis数据构建响应
    private Map<String, Object> buildFromRedisVehicleData(List<Map<String, Object>> redisData) {
        Map<String, Object> result = new HashMap<>();
        
        List<Charge> allStats = new ArrayList<>();
        for (Map<String, Object> cityData : redisData) {
            allStats.add(new Charge(
                    (String) cityData.getOrDefault("city_name", "未知"),
                    ((Number) cityData.getOrDefault("vehicle_total", 0)).intValue(),
                    ((Number) cityData.getOrDefault("today_online", 0)).intValue(),
                    ((Number) cityData.getOrDefault("today_alarm", 0)).intValue()
            ));
        }
        
        // 按车辆总数降序排序
        allStats.sort((a, b) -> b.getVehicleTotal() - a.getVehicleTotal());
        
        Charge provinceTotal = calculateProvinceTotal(allStats);
        List<Charge> top9Cities = allStats.stream().limit(9).collect(Collectors.toList());

        result.put("provinceTotal", provinceTotal);
        result.put("topCities", top9Cities);
        return result;
    }

    /**
     * 计算全省统计数据
     */
    private Charge calculateProvinceTotal(List<Charge> allStats) {
        int totalVehicle = allStats.stream()
                .mapToInt(Charge::getVehicleTotal)
                .sum();

        int totalOnline = allStats.stream()
                .mapToInt(Charge::getTodayOnline)
                .sum();

        int totalAlarm = allStats.stream()
                .mapToInt(Charge::getTodayAlarm)
                .sum();

        return new Charge("全省", totalVehicle, totalOnline, totalAlarm);
    }
}
