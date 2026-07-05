package com.example.one.Service;

import com.example.one.pojo.RoadCondition;
import com.example.one.repository.RoadConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardDataService {
    @Autowired
    private RoadConditionRepository roadConditionRepository;
    
    @Autowired
    private RedisDataService redisDataService;

    public List<Map<String, Object>> getDashboardTopData() {
        // 优先从Redis获取实时数据
        Map<String, Object> redisData = redisDataService.getRoadCondition();
        if (redisData != null && !redisData.isEmpty()) {
            return buildFromRedisData(redisData);
        }
        
        // Redis无数据时从MySQL获取
        List<RoadCondition> roadConditions = roadConditionRepository.findFirst14ByOrderByTimeIdAsc();
        if (roadConditions == null || roadConditions.isEmpty()) {
            // 返回默认数据而不是空列表
            return buildDefaultData();
        }
        
        // 数据不足14条时也尝试处理
        if (roadConditions.size() < 14) {
            // 用现有数据构建
            RoadCondition current = roadConditions.get(0);
            long alertCount = current.getAlertCount() != null ? current.getAlertCount() : 0;
            long abnormalEvents = current.getAbnormalEvents() != null ? current.getAbnormalEvents() : 0;
            return Arrays.asList(
                    buildPoliceData((int) alertCount, (int) alertCount, (int) (alertCount * 7), (int) (alertCount * 7)),
                    buildCongestionData(current),
                    buildEfficiencyData(current),
                    buildIncidentData((int) abnormalEvents, (int) (abnormalEvents / 2), (int) (abnormalEvents * 7))
            );
        }

        // 计算警情数相关指标
        int currentPolice = roadConditions.get(0).getAlertCount() != null ? roadConditions.get(0).getAlertCount().intValue() : 0;
        int prevDayPolice = roadConditions.get(1).getAlertCount() != null ? roadConditions.get(1).getAlertCount().intValue() : 0;
        int firstWeekPoliceSum = roadConditions.subList(0, 7).stream()
                .mapToInt(rc -> rc.getAlertCount() != null ? rc.getAlertCount().intValue() : 0).sum();
        int secondWeekPoliceSum = roadConditions.subList(7, 14).stream()
                .mapToInt(rc -> rc.getAlertCount() != null ? rc.getAlertCount().intValue() : 0).sum();

        // 计算异常事件相关指标
        int currentIncidents = roadConditions.get(0).getAbnormalEvents() != null ? roadConditions.get(0).getAbnormalEvents().intValue() : 0;
        int resolvedIncidents = new Random().nextInt(currentIncidents + 1);
        int firstWeekIncidentSum = roadConditions.subList(0, 7).stream()
                .mapToInt(rc -> rc.getAbnormalEvents() != null ? rc.getAbnormalEvents().intValue() : 0).sum();

        // 获取当前交通状况
        RoadCondition currentCondition = roadConditions.get(0);

        return Arrays.asList(
                buildPoliceData(currentPolice, prevDayPolice, firstWeekPoliceSum, secondWeekPoliceSum),
                buildCongestionData(currentCondition),
                buildEfficiencyData(currentCondition),
                buildIncidentData(currentIncidents, resolvedIncidents, firstWeekIncidentSum)
        );
    }

    private Map<String, Object> buildPoliceData(int current, int prevDay, int firstWeekSum, int secondWeekSum) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "当前警情数(起)");
        data.put("value", current);
        data.put("boxClass", "visual_conTop_box visual_conTop1");

        double dailyRatio = (current - prevDay) * 100.0 / prevDay;
        double weeklyRatio = (firstWeekSum - secondWeekSum) * 100.0 / secondWeekSum;

        data.put("details", Arrays.asList(
                createDetailItem("日环比:", String.format("%.1f%%", dailyRatio)),
                createDetailItem("周环比:", String.format("%.1f%%", weeklyRatio))
        ));
        return data;
    }

    private Map<String, Object> buildCongestionData(RoadCondition condition) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "区域拥堵指数");
        double congestionIndex = condition.getCongestionIndex() != null ? condition.getCongestionIndex() : 0;
        int avgSpeed = condition.getAvgSpeed() != null ? condition.getAvgSpeed() : 0;
        data.put("value", (int) congestionIndex);
        data.put("boxClass", "visual_conTop_box visual_conTop2");
        data.put("details", Arrays.asList(
                createDetailItem("状态:", getTrafficStatus(avgSpeed)),
                createDetailItem("平均车速:", avgSpeed + "km/h")
        ));
        return data;
    }

    private Map<String, Object> buildEfficiencyData(RoadCondition condition) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "收费站通行效率");
        double efficiency = condition.getEfficiency() != null ? condition.getEfficiency() : 0;
        int waitTime = condition.getWaitTime() != null ? condition.getWaitTime() : 0;
        data.put("value", (int) Math.round(efficiency));
        data.put("boxClass", "visual_conTop_box visual_conTop1");
        data.put("details", Arrays.asList(
                createDetailItem("平均等待:", waitTime + "分钟"),
                createDetailItem("最快通道:", "-")
        ));
        return data;
    }

    private Map<String, Object> buildIncidentData(int current, int resolved, int weekSum) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "异常事件数");
        data.put("value", weekSum);
        data.put("boxClass", "visual_conTop_box visual_conTop2");
        data.put("details", Arrays.asList(
                createDetailItem("今日新增:", String.valueOf(current)),
                createDetailItem("已处理:", String.valueOf(resolved))
        ));
        return data;
    }

    private String getTrafficStatus(int avgSpeed) {
        if (avgSpeed <= 10) return "拥堵";
        if (avgSpeed <= 30) return "缓行";
        return "较为畅通";
    }

    private Map<String, String> createDetailItem(String label, String value) {
        Map<String, String> detail = new HashMap<>();
        detail.put("label", label);
        detail.put("value", value);
        return detail;
    }
    
    // 从Redis数据构建响应
    private List<Map<String, Object>> buildFromRedisData(Map<String, Object> redisData) {
        int alertCount = ((Number) redisData.getOrDefault("alert_count", 0)).intValue();
        double congestionIndex = ((Number) redisData.getOrDefault("congestion_index", 0)).doubleValue();
        double efficiency = ((Number) redisData.getOrDefault("efficiency", 0)).doubleValue();
        int abnormalEvents = ((Number) redisData.getOrDefault("abnormal_events", 0)).intValue();
        int avgSpeed = ((Number) redisData.getOrDefault("avg_speed", 0)).intValue();
        int waitTime = ((Number) redisData.getOrDefault("wait_time", 0)).intValue();
        String updateTime = (String) redisData.getOrDefault("update_time", "");

        return Arrays.asList(
                buildRedisPoliceData(alertCount, updateTime),
                buildRedisCongestionData(congestionIndex, avgSpeed),
                buildRedisEfficiencyData(efficiency, waitTime),
                buildRedisIncidentData(abnormalEvents)
        );
    }

    private Map<String, Object> buildRedisPoliceData(int current, String updateTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "当前警情数(起)");
        data.put("value", current);
        data.put("boxClass", "visual_conTop_box visual_conTop1");
        data.put("details", Arrays.asList(
                createDetailItem("更新时间:", updateTime),
                createDetailItem("数据来源:", "实时")
        ));
        return data;
    }

    private Map<String, Object> buildRedisCongestionData(double congestionIndex, int avgSpeed) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "区域拥堵指数");
        data.put("value", (int) congestionIndex);
        data.put("boxClass", "visual_conTop_box visual_conTop2");
        data.put("details", Arrays.asList(
                createDetailItem("状态:", getTrafficStatus(avgSpeed)),
                createDetailItem("平均车速:", avgSpeed + "km/h")
        ));
        return data;
    }

    private Map<String, Object> buildRedisEfficiencyData(double efficiency, int waitTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "收费站通行效率");
        data.put("value", (int) Math.round(efficiency));
        data.put("boxClass", "visual_conTop_box visual_conTop1");
        data.put("details", Arrays.asList(
                createDetailItem("平均等待:", waitTime + "分钟"),
                createDetailItem("数据来源:", "实时")
        ));
        return data;
    }

    private Map<String, Object> buildRedisIncidentData(int abnormalEvents) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "异常事件数");
        data.put("value", abnormalEvents);
        data.put("boxClass", "visual_conTop_box visual_conTop2");
        data.put("details", Arrays.asList(
                createDetailItem("今日新增:", String.valueOf(abnormalEvents)),
                createDetailItem("数据来源:", "实时")
        ));
        return data;
    }

    // 返回默认数据
    private List<Map<String, Object>> buildDefaultData() {
        return Arrays.asList(
                buildRedisPoliceData(0, "-"),
                buildRedisCongestionData(0, 0),
                buildRedisEfficiencyData(0, 0),
                buildRedisIncidentData(0)
        );
    }
}
