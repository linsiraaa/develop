package com.example.one.Service;

import com.example.one.pojo.HighwayToll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HighwayService {

    @Autowired
    private RedisDataService redisDataService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<HighwayToll> getTop5ByTollAmount() {
        // 优先从Redis获取实时数据
        try {
            List<Map<String, Object>> redisData = redisDataService.getTollRankTop(5);
            if (redisData != null && !redisData.isEmpty()) {
                List<HighwayToll> result = new ArrayList<>();
                for (Map<String, Object> item : redisData) {
                    HighwayToll toll = new HighwayToll();
                    toll.setStationName((String) item.get("station_name"));
                    Object amount = item.get("toll_amount");
                    if (amount instanceof Double) {
                        toll.setTollAmount(BigDecimal.valueOf((Double) amount));
                    } else if (amount instanceof Number) {
                        toll.setTollAmount(BigDecimal.valueOf(((Number) amount).doubleValue()));
                    }
                    result.add(toll);
                }
                if (!result.isEmpty()) {
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println("从Redis获取收费站排行失败: " + e.getMessage());
        }
        
        // Redis无数据时从MySQL的toll_collection表获取
        return getFromMySQL();
    }
    
    private List<HighwayToll> getFromMySQL() {
        List<HighwayToll> result = new ArrayList<>();
        try {
            String sql = "SELECT station_name, toll_amount FROM toll_collection " +
                        "ORDER BY toll_amount DESC LIMIT 5";
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> row : rows) {
                HighwayToll toll = new HighwayToll();
                toll.setStationName((String) row.get("station_name"));
                Object amount = row.get("toll_amount");
                if (amount instanceof BigDecimal) {
                    toll.setTollAmount((BigDecimal) amount);
                } else if (amount instanceof Number) {
                    toll.setTollAmount(BigDecimal.valueOf(((Number) amount).doubleValue()));
                }
                result.add(toll);
            }
        } catch (Exception e) {
            System.err.println("从MySQL获取收费站排行失败: " + e.getMessage());
        }
        return result;
    }
}
