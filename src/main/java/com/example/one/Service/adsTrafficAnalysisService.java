package com.example.one.Service;

import com.example.one.pojo.AdsTrafficFlowAnalysis;
import com.example.one.repository.AdsTrafficFlowAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class adsTrafficAnalysisService {
    @Autowired
    private AdsTrafficFlowAnalysisRepository repository;

    public Map<String, Object> get2025TrafficStatistics() {
        List<AdsTrafficFlowAnalysis> dataList = repository.find2025Data();

        // 按年月+类型合并为复合键，分组聚合
        Map<String, Map<String, Double>> groupedData = dataList.stream()
                .collect(Collectors.groupingBy(
                        a -> String.format("%d-%02d-%s", a.getYear(), a.getMonth(), a.getTypeName()), // 复合键：年月-类型
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long currentSum = list.stream()
                                            .mapToLong(AdsTrafficFlowAnalysis::getCurrentFlow)
                                            .sum();
                                    long lastYearSum = list.stream()
                                            .mapToLong(AdsTrafficFlowAnalysis::getLastYearFlow)
                                            .sum();
                                    double yoy = calculateGrowth(currentSum, lastYearSum);

                                    // 防御性处理：避免空指针（假设至少有一条记录）
                                    long prevMonthFlow = list.isEmpty() ? 0L : list.get(0).getPrevMonthFlow();
                                    double mom = calculateGrowth(currentSum, prevMonthFlow);

                                    // 构建统计Map
                                    Map<String, Double> stats = new HashMap<>();
                                    stats.put("current", currentSum / 1000.0);
                                    stats.put("yoy", yoy);
                                    stats.put("mom", mom);
                                    return stats;
                                }
                        )
                ));

        // 构建响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("data", groupedData);
        result.put("total", calculateTotals(dataList));
        return result;
    }

    private double calculateGrowth(long current, long previous) {
        return previous == 0 ? 0 : ((double)(current - previous)/previous)*100;
    }

    private Map<String, Double> calculateTotals(List<AdsTrafficFlowAnalysis> dataList) {
        return dataList.stream()
                .flatMap(a -> Stream.of(
                        new AbstractMap.SimpleEntry<>("highway_current", a.getTypeName().equals("高速公路") ? a.getCurrentFlow() : 0),
                        new AbstractMap.SimpleEntry<>("urban_current", a.getTypeName().equals("城镇公路") ? a.getCurrentFlow() : 0),
                        new AbstractMap.SimpleEntry<>("highway_yoy", a.getTypeName().equals("高速公路") ? a.getYoyGrowthPercent() : 0),
                        new AbstractMap.SimpleEntry<>("urban_yoy", a.getTypeName().equals("城镇公路") ? a.getYoyGrowthPercent() : 0)
                ))
                // 修改后（显式类型转换）
                .collect(Collectors.groupingBy(
                        e -> (String) e.getKey(),  // 显式转换为String类型
                        Collectors.summingDouble(e -> ((Number) e.getValue()).doubleValue())  // 显式转换为Double
                ));
    }
}

