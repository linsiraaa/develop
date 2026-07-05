package com.example.one.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实时数据服务 - 从Redis获取Flink计算的实时数据
 */
@Service
public class RealtimeDataService {

    @Autowired
    private RedisDataService redisDataService;

    /**
     * 获取实时仪表盘数据（路况指标）- 只从Redis获取
     */
    public Map<String, Object> getRealtimeDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 从Redis获取Flink计算的路况数据
        Map<String, Object> roadCondition = redisDataService.getRoadCondition();
        
        if (roadCondition != null && !roadCondition.isEmpty()) {
            data.put("alertCount", roadCondition.getOrDefault("alert_count", 0));
            data.put("congestionIndex", roadCondition.getOrDefault("congestion_index", 0.0));
            data.put("efficiency", roadCondition.getOrDefault("efficiency", 0.0));
            data.put("abnormalEvents", roadCondition.getOrDefault("abnormal_events", 0));
            data.put("avgSpeed", roadCondition.getOrDefault("avg_speed", 0));
            data.put("waitTime", roadCondition.getOrDefault("wait_time", 0));
            data.put("source", "redis");
        } else {
            // Redis无数据时返回默认值
            data.put("alertCount", 0);
            data.put("congestionIndex", 0.0);
            data.put("efficiency", 0.0);
            data.put("abnormalEvents", 0);
            data.put("avgSpeed", 0);
            data.put("waitTime", 0);
            data.put("source", "default");
        }
        
        data.put("timestamp", System.currentTimeMillis());
        
        return data;
    }

    /**
     * 获取收费站排行榜
     */
    public List<Map<String, Object>> getTollRank(int topN) {
        return redisDataService.getTollRankTop(topN);
    }

    /**
     * 获取实时车辆统计（按城市）
     */
    public List<Map<String, Object>> getRealtimeVehicleStats() {
        return redisDataService.getAllVehicleInfo();
    }

    /**
     * 获取客货运量统计
     */
    public List<Map<String, Object>> getPassengerFreight() {
        return redisDataService.getPassengerFreight();
    }

    /**
     * 获取访豫车辆数据
     */
    public List<Map<String, Object>> getInToHenan() {
        return redisDataService.getInToHenan();
    }

    /**
     * 获取出省车辆数据
     */
    public List<Map<String, Object>> getOutOfHenan() {
        return redisDataService.getOutOfHenan();
    }

    /**
     * 检查Flink实时数据是否可用
     */
    public boolean isFlinkDataAvailable() {
        return redisDataService.isFlinkDataAvailable();
    }

    /**
     * 获取交通流量实时数据
     */
    public List<Map<String, Object>> getTrafficFlow() {
        return redisDataService.getTrafficFlow();
    }

    /**
     * 获取交通工具流量实时数据（按车型）
     */
    public List<Map<String, Object>> getVehicleTraffic() {
        return redisDataService.getVehicleTraffic();
    }

    /**
     * 获取交通违规事件实时数据
     */
    public List<Map<String, Object>> getTrafficViolation() {
        return redisDataService.getTrafficViolation();
    }
}
