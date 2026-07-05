package com.example.one.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleFreightService {

    @Autowired
    private RedisDataService redisDataService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getFormattedVehicleFreightSummary() {
        // 优先从Redis获取实时数据
        try {
            List<Map<String, Object>> redisData = redisDataService.getPassengerFreight();
            if (redisData != null && !redisData.isEmpty()) {
                Map<String, Object> result = buildFromRedisData(redisData);
                if (hasValidData(result)) {
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println("从Redis获取客货运量失败: " + e.getMessage());
        }
        
        // Redis无数据时从MySQL获取（使用原生SQL查询passenger_freight_by_vehicle表）
        return getFromMySQL();
    }
    
    private boolean hasValidData(Map<String, Object> data) {
        for (Object value : data.values()) {
            if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                Object passenger = map.get("passengerVolume");
                if (passenger != null && ((Number) passenger).longValue() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Map<String, Object> getFromMySQL() {
        Map<String, Object> formattedData = new HashMap<>();
        formattedData.put("large", createDefaultMap());
        formattedData.put("medium", createDefaultMap());
        formattedData.put("small", createDefaultMap());
        
        try {
            // 从passenger_freight_by_vehicle表联合vehicle_type表查询
            String sql = "SELECT vt.vehicle_type, pfv.passenger_volume, pfv.freight_volume " +
                        "FROM passenger_freight_by_vehicle pfv " +
                        "INNER JOIN vehicle_type vt ON pfv.vehicle_type_id = vt.vehicle_type_id " +
                        "WHERE vt.vehicle_type IN ('大型车', '中型车', '小型车')";
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> row : rows) {
                String vehicleType = (String) row.get("vehicle_type");
                Map<String, Object> data = new HashMap<>();
                data.put("passengerVolume", row.get("passenger_volume"));
                data.put("freightVolume", row.get("freight_volume"));
                
                if ("大型车".equals(vehicleType)) {
                    formattedData.put("large", data);
                } else if ("中型车".equals(vehicleType)) {
                    formattedData.put("medium", data);
                } else if ("小型车".equals(vehicleType)) {
                    formattedData.put("small", data);
                }
            }
        } catch (Exception e) {
            System.err.println("从MySQL获取客货运量失败: " + e.getMessage());
        }
        
        return formattedData;
    }
    
    private Map<String, Object> buildFromRedisData(List<Map<String, Object>> redisData) {
        Map<String, Object> formattedData = new HashMap<>();
        formattedData.put("large", createDefaultMap());
        formattedData.put("medium", createDefaultMap());
        formattedData.put("small", createDefaultMap());
        
        for (Map<String, Object> item : redisData) {
            String vehicleType = (String) item.get("vehicle_type");
            Map<String, Object> data = new HashMap<>();
            data.put("passengerVolume", item.get("passenger_volume"));
            data.put("freightVolume", item.get("freight_volume"));
            
            if ("大型车".equals(vehicleType)) {
                formattedData.put("large", data);
            } else if ("中型车".equals(vehicleType)) {
                formattedData.put("medium", data);
            } else if ("小型车".equals(vehicleType)) {
                formattedData.put("small", data);
            }
        }
        
        return formattedData;
    }
    
    private Map<String, Object> createDefaultMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("passengerVolume", 0L);
        data.put("freightVolume", 0L);
        return data;
    }
}