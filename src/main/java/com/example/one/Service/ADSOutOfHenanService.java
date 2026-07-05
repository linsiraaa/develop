package com.example.one.Service;

import com.example.one.pojo.ADSOutOfHenan;
import com.example.one.repository.ADSOutOfHenanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ADSOutOfHenanService {

    @Autowired
    private ADSOutOfHenanRepository repository;

    public List<Map<String, Object>> getADSOutOfHenanWithWeights() {
        // 1. 获取原始数据（使用DISTINCT确保不重复）
        List<Map<String, Object>> data = repository.findAllDistinctCityPairs();

        // 2. 计算每个出发城市的出现次数
        Map<String, Long> fromCityCounts = data.stream()
                .collect(Collectors.groupingBy(
                        item -> (String) item.get("fromCityName"),
                        Collectors.counting()
                ));

        // 3. 计算每个目的城市的出现次数
        Map<String, Long> toCityCounts = data.stream()
                .collect(Collectors.groupingBy(
                        item -> (String) item.get("toCityName"),
                        Collectors.counting()
                ));

        // 4. 获取最大出现次数用于归一化
        long maxFromCount = fromCityCounts.values().stream().max(Long::compare).orElse(1L);
        long maxToCount = toCityCounts.values().stream().max(Long::compare).orElse(1L);

        // 5. 构建结果列表
        List<Map<String, Object>> result = new ArrayList<>();
        int autoIncrementId = 1;

        for (Map<String, Object> item : data) {
            Map<String, Object> cityPair = new LinkedHashMap<>();

            // 基础信息
            cityPair.put("id", autoIncrementId++);
            cityPair.put("fromCity", item.get("fromCityName"));
            cityPair.put("fromLongitude", item.get("fromLongitude"));
            cityPair.put("fromLatitude", item.get("fromLatitude"));

            // 出发城市权重
            long fromCount = fromCityCounts.getOrDefault(item.get("fromCityName"), 1L);
            int fromWeight = normalizeWeight(fromCount, maxFromCount);
            cityPair.put("fromWeight", fromWeight);
            cityPair.put("fromSymbolSize", fromWeight + 1);

            // 目的地信息
            cityPair.put("toCity", item.get("toCityName"));
            cityPair.put("toLongitude", item.get("toLongitude"));
            cityPair.put("toLatitude", item.get("toLatitude"));

            // 目的城市权重
            long toCount = toCityCounts.getOrDefault(item.get("toCityName"), 1L);
            int toWeight = normalizeWeight(toCount, maxToCount);
            cityPair.put("toWeight", toWeight);
            cityPair.put("toSymbolSize", toWeight);

            // 车辆数
            cityPair.put("vehicleCount", item.get("vehicleCount"));

            result.add(cityPair);
        }

        return result;
    }

    private int normalizeWeight(long count, long maxCount) {
        if (maxCount <= 1) return 1;
        return Math.max(1, (int) Math.round(1 + 9 * ((double) count / maxCount)));
    }
}
