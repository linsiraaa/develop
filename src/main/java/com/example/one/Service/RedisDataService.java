package com.example.one.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Redis数据服务 - 从Redis读取Flink实时计算的结果
 */
@Service
public class RedisDataService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis Key前缀 - 与Flink Job中的定义保持一致
    private static final String KEY_TOLL_RANK = "traffic:toll_rank";
    private static final String KEY_ROAD_CONDITION = "traffic:road_condition";
    private static final String KEY_VEHICLE_INFO = "traffic:vehicle_info";
    private static final String KEY_IN_TO_HENAN = "traffic:in_to_henan";
    private static final String KEY_OUT_OF_HENAN = "traffic:out_of_henan";
    private static final String KEY_PASSENGER_FREIGHT = "traffic:passenger_freight";
    private static final String KEY_TRAFFIC_FLOW = "traffic:traffic_flow";
    private static final String KEY_VEHICLE_TRAFFIC = "traffic:vehicle_traffic";
    private static final String KEY_TRAFFIC_VIOLATION = "traffic:traffic_violation";

    /**
     * 获取路况数据
     */
    public Map<String, Object> getRoadCondition() {
        try {
            Object data = redisTemplate.opsForHash().get(KEY_ROAD_CONDITION, "global");
            if (data != null && !data.toString().isEmpty()) {
                Map<String, Object> result = JSON.parseObject(data.toString());
                // 确保返回的数据有实际内容
                if (result != null && !result.isEmpty() && result.containsKey("alert_count")) {
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println("获取路况数据失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取收费站排行 Top N
     */
    public List<Map<String, Object>> getTollRankTop(int n) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_TOLL_RANK);
            List<Map<String, Object>> allStations = new ArrayList<>();
            
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("station_name", json.getString("station_name"));
                item.put("toll_amount", json.getDoubleValue("toll_amount"));
                allStations.add(item);
            }
            
            // 按收费金额降序排序
            allStations.sort((a, b) -> {
                Double amountA = (Double) a.get("toll_amount");
                Double amountB = (Double) b.get("toll_amount");
                return amountB.compareTo(amountA);
            });
            
            // 取Top N
            result = allStations.subList(0, Math.min(n, allStations.size()));
        } catch (Exception e) {
            System.err.println("获取收费站排行失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有城市车辆统计
     */
    public List<Map<String, Object>> getAllVehicleInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_VEHICLE_INFO);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("city_name", json.getString("city_name"));
                item.put("vehicle_total", json.getIntValue("vehicle_total"));
                item.put("today_online", json.getIntValue("today_online"));
                item.put("today_alarm", json.getIntValue("today_alarm"));
                result.add(item);
            }
        } catch (Exception e) {
            System.err.println("获取城市车辆统计失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指定城市车辆统计
     */
    public Map<String, Object> getVehicleInfoByCity(String cityName) {
        try {
            String data = (String) redisTemplate.opsForHash().get(KEY_VEHICLE_INFO, cityName);
            if (data != null) {
                return JSON.parseObject(data);
            }
        } catch (Exception e) {
            System.err.println("获取城市车辆统计失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取客货运量
     */
    public List<Map<String, Object>> getPassengerFreight() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_PASSENGER_FREIGHT);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("vehicle_type", json.getString("vehicle_type"));
                item.put("passenger_volume", json.getLongValue("passenger_volume"));
                item.put("freight_volume", json.getLongValue("freight_volume"));
                result.add(item);
            }
        } catch (Exception e) {
            System.err.println("获取客货运量失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取访豫车辆
     */
    public List<Map<String, Object>> getInToHenan() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_IN_TO_HENAN);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("from_city_name", json.getString("from_city_name"));
                item.put("from_latitude", json.getDoubleValue("from_latitude"));
                item.put("from_longitude", json.getDoubleValue("from_longitude"));
                item.put("vehicle_count", json.getLongValue("vehicle_count"));
                result.add(item);
            }
        } catch (Exception e) {
            System.err.println("获取访豫车辆失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取出省车辆
     */
    public List<Map<String, Object>> getOutOfHenan() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_OUT_OF_HENAN);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("from_city_name", json.getString("from_city_name"));
                item.put("from_latitude", json.getDoubleValue("from_latitude"));
                item.put("from_longitude", json.getDoubleValue("from_longitude"));
                item.put("to_city_name", json.getString("to_city_name"));
                item.put("to_latitude", json.getDoubleValue("to_latitude"));
                item.put("to_longitude", json.getDoubleValue("to_longitude"));
                item.put("vehicle_count", json.getLongValue("vehicle_count"));
                result.add(item);
            }
        } catch (Exception e) {
            System.err.println("获取出省车辆失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 检查Redis中是否有数据
     */
    public boolean hasData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 检查Flink计算数据是否可用
     */
    public boolean isFlinkDataAvailable() {
        return hasData(KEY_ROAD_CONDITION) || hasData(KEY_TOLL_RANK);
    }

    /**
     * 获取交通流量数据（支持多月份）
     * Redis中的key格式: time_id_type_id
     */
    public List<Map<String, Object>> getTrafficFlow() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_TRAFFIC_FLOW);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("type_id", json.getIntValue("type_id"));
                item.put("type_name", json.getString("type_name"));
                item.put("flow_count", json.getLongValue("flow_count"));
                item.put("time_id", json.getIntValue("time_id"));
                result.add(item);
            }
            // 按time_id排序
            result.sort((a, b) -> {
                int timeIdA = (Integer) a.get("time_id");
                int timeIdB = (Integer) b.get("time_id");
                return Integer.compare(timeIdA, timeIdB);
            });
        } catch (Exception e) {
            System.err.println("获取交通流量失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取交通工具流量数据（按车型，支持多月份）
     * Redis中的key格式: time_id_vehicle_type_id
     */
    public List<Map<String, Object>> getVehicleTraffic() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_VEHICLE_TRAFFIC);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("vehicle_type_id", json.getIntValue("vehicle_type_id"));
                item.put("vehicle_type", json.getString("vehicle_type"));
                item.put("vehicle_count", json.getLongValue("vehicle_count"));
                item.put("time_id", json.getIntValue("time_id"));
                result.add(item);
            }
            // 按time_id排序
            result.sort((a, b) -> {
                int timeIdA = (Integer) a.get("time_id");
                int timeIdB = (Integer) b.get("time_id");
                return Integer.compare(timeIdA, timeIdB);
            });
        } catch (Exception e) {
            System.err.println("获取交通工具流量失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取交通违规事件数据（支持多月份）
     * Redis中的key格式: time_id_violation_id
     */
    public List<Map<String, Object>> getTrafficViolation() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_TRAFFIC_VIOLATION);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String jsonStr = entry.getValue().toString();
                JSONObject json = JSON.parseObject(jsonStr);
                Map<String, Object> item = new HashMap<>();
                item.put("violation_id", json.getIntValue("violation_id"));
                item.put("violation_name", json.getString("violation_name"));
                item.put("violation_count", json.getIntValue("violation_count"));
                item.put("time_id", json.getIntValue("time_id"));
                result.add(item);
            }
            // 按time_id排序
            result.sort((a, b) -> {
                int timeIdA = (Integer) a.get("time_id");
                int timeIdB = (Integer) b.get("time_id");
                return Integer.compare(timeIdA, timeIdB);
            });
        } catch (Exception e) {
            System.err.println("获取交通违规事件失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * time_id 到 月份的映射
     * 2025-01=14, 2025-02=15, ..., 2025-12=25, 2026-01=1
     */
    private static final Map<Integer, String> TIME_ID_TO_MONTH = new HashMap<>();
    static {
        // 2024年
        TIME_ID_TO_MONTH.put(2, "2024-01");
        TIME_ID_TO_MONTH.put(3, "2024-02");
        TIME_ID_TO_MONTH.put(4, "2024-03");
        TIME_ID_TO_MONTH.put(5, "2024-04");
        TIME_ID_TO_MONTH.put(6, "2024-05");
        TIME_ID_TO_MONTH.put(7, "2024-06");
        TIME_ID_TO_MONTH.put(8, "2024-07");
        TIME_ID_TO_MONTH.put(9, "2024-08");
        TIME_ID_TO_MONTH.put(10, "2024-09");
        TIME_ID_TO_MONTH.put(11, "2024-10");
        TIME_ID_TO_MONTH.put(12, "2024-11");
        TIME_ID_TO_MONTH.put(13, "2024-12");
        // 2025年
        TIME_ID_TO_MONTH.put(14, "2025-01");
        TIME_ID_TO_MONTH.put(15, "2025-02");
        TIME_ID_TO_MONTH.put(16, "2025-03");
        TIME_ID_TO_MONTH.put(17, "2025-04");
        TIME_ID_TO_MONTH.put(18, "2025-05");
        TIME_ID_TO_MONTH.put(19, "2025-06");
        TIME_ID_TO_MONTH.put(20, "2025-07");
        TIME_ID_TO_MONTH.put(21, "2025-08");
        TIME_ID_TO_MONTH.put(22, "2025-09");
        TIME_ID_TO_MONTH.put(23, "2025-10");
        TIME_ID_TO_MONTH.put(24, "2025-11");
        TIME_ID_TO_MONTH.put(25, "2025-12");
        // 2026年
        TIME_ID_TO_MONTH.put(1, "2026-01");
    }

    /**
     * 根据time_id获取月份字符串
     */
    public String getMonthByTimeId(int timeId) {
        return TIME_ID_TO_MONTH.getOrDefault(timeId, "未知");
    }
}
