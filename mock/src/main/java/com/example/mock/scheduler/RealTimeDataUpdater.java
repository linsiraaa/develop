package com.example.mock.scheduler;

import com.example.mock.mock.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalTime;
import java.util.Random;

/**
 * 实时数据更新
 * 为后续 Flink 实时计算提供数据源
 */
@Component
public class RealTimeDataUpdater {

    private static final Logger log = LoggerFactory.getLogger(RealTimeDataUpdater.class);
    private static final Random random = new Random();
    private int baseAlertCount = 60;
    private double baseCongestionIndex = 300;
    private double baseEfficiency = 80;
    private int baseAbnormalEvents = 5;
    private int baseAvgSpeed = 90;

    /**
     * 每30秒执行一次数据更新
     */
    @Scheduled(fixedRate = 5000)
    public void updateData() {
        log.info("开始实时数据更新...");
        
        try {
            log.info("正在更新：路况数据...");
            updateRoadCondition();
        } catch (Exception e) {
            log.error("[路况] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：收费站排行...");
            updateTollStationRank();
        } catch (Exception e) {
            log.error("[收费站] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：今日车辆统计...");
            updateTodayVehicleStats();
        } catch (Exception e) {
            log.error("[车辆统计] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：客货运量...");
            updatePassengerFreight();
        } catch (Exception e) {
            log.error("[客货运量] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：豫车辆数据...");
            updateVehicleToHenan();
        } catch (Exception e) {
            log.error("[访豫车辆] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：出省车辆数据...");
            updateOutOfHenan();
        } catch (Exception e) {
            log.error("[出省车辆] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：交通流量数据...");
            updateTrafficFlow();
        } catch (Exception e) {
            log.error("[交通流量] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：交通工具流量数据...");
            updateVehicleTraffic();
        } catch (Exception e) {
            log.error("[交通工具流量] 更新异常: {}", e.getMessage());
        }
        
        try {
            log.info("正在更新：交通违规事件...");
            updateTrafficViolation();
        } catch (Exception e) {
            log.error("[交通违规] 更新异常: {}", e.getMessage());
        }
        
        log.info("实时数据更新完成");
    }

    /**
     * 初始化车辆统计数据（应用启动时调用）
     */
    public void initializeVehicleStats() {
        String selectSql = "SELECT city_id FROM charge";
        String updateSql = "UPDATE charge SET vehicle_total=?, today_online=?, today_alarm=? WHERE city_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            log.info("[初始化各市区车辆统计数据]");
            while (rs.next()) {
                int city = rs.getInt("city_id");
                int vehicleTotal = 5000 + random.nextInt(15000);
                int todayOnline = 1000 + random.nextInt(5000);
                int todayAlarm = 50 + random.nextInt(200);

                updateStmt.setInt(1, vehicleTotal);
                updateStmt.setInt(2, todayOnline);
                updateStmt.setInt(3, todayAlarm);
                updateStmt.setInt(4, city);
                updateStmt.executeUpdate();
            }
            log.info("车辆统计初始化完成");
        } catch (Exception e) {
            log.warn("初始化车辆统计失败(表可能不存在): {}", e.getMessage());
        }
    }


    static int latestRoadConditionId(Connection conn) throws SQLException {
        String sql = "SELECT rc.rc_id FROM road_condition rc JOIN time_dim t ON rc.time_id=t.time_id " +
                "ORDER BY t.year DESC, t.month DESC, rc.rc_id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("rc_id") : -1;
        }
    }

    /**
     * 更新路况数据 (road_condition)
     */
    private void updateRoadCondition() {
        int hour = LocalTime.now().getHour();
        double peakFactor = getPeakFactor(hour);

        baseAlertCount = adjustInt(baseAlertCount, (int)(50 * peakFactor), (int)(120 * peakFactor), 0.1);
        baseCongestionIndex = adjustDouble(baseCongestionIndex, 150 * peakFactor, 800 * peakFactor, 0.1);
        baseEfficiency = adjustDouble(baseEfficiency, 95 - 30 * peakFactor, 95 - 10 * peakFactor, 0.05);
        baseAbnormalEvents = adjustInt(baseAbnormalEvents, (int)(3 * peakFactor), (int)(15 * peakFactor), 0.15);
        baseAvgSpeed = adjustInt(baseAvgSpeed, (int)(120 - 50 * peakFactor), (int)(120 - 20 * peakFactor), 0.08);

        String sql = "UPDATE road_condition SET alert_count=?, congestion_index=?, efficiency=?, abnormal_events=?, avg_speed=?, wait_time=? WHERE rc_id=?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            int rcId = latestRoadConditionId(conn);
            if (rcId < 0) {
                log.warn("[路况] 没有可更新的路况记录");
                return;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, baseAlertCount + random.nextInt(11) - 5);
            stmt.setDouble(2, baseCongestionIndex + (random.nextDouble() - 0.5) * 50);
            stmt.setDouble(3, Math.min(95, Math.max(50, baseEfficiency)));
            stmt.setInt(4, Math.max(0, baseAbnormalEvents + random.nextInt(5) - 2));
            stmt.setInt(5, Math.max(30, baseAvgSpeed + random.nextInt(11) - 5));
            stmt.setInt(6, random.nextInt(300));
            stmt.setInt(7, rcId);
            int rows = stmt.executeUpdate();
            log.info("[路况] 警情={} 拥堵={} 效率={}% (更新{}行)", baseAlertCount, (int)baseCongestionIndex, (int)baseEfficiency, rows);
            }
        } catch (Exception e) {
            log.warn("[路况] 更新失败: {}", e.getMessage());
        }
    }

    /**
     * 更新收费站排行 (toll_collection_rank)
     */
    private void updateTollStationRank() {
        String selectSql = "SELECT station_name, toll_amount FROM toll_collection";
        String updateSql = "UPDATE toll_collection SET toll_amount=? WHERE station_name=?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            int count = 0;
            while (rs.next()) {
                String name = rs.getString("station_name");
                double amount = rs.getDouble("toll_amount") + 100 + random.nextInt(401);
                updateStmt.setDouble(1, amount);
                updateStmt.setString(2, name);
                updateStmt.executeUpdate();
                count++;
            }
            log.info("[收费站] 更新{}个站点", count);
        } catch (Exception e) {
            log.warn("[收费站] 更新失败: {}", e.getMessage());
        }
    }

    /**
     * 更新今日车辆统计 (charge)
     */
    private void updateTodayVehicleStats() {
        String selectSql = "SELECT city_id, vehicle_total, today_online, today_alarm FROM charge";
        String updateSql = "UPDATE charge SET vehicle_total=?, today_online=?, today_alarm=? WHERE city_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            int count = 0;
            while (rs.next()) {
                int city = rs.getInt("city_id");
                int totalIncrement = 10 + random.nextInt(200);
                int onlineIncrement = 5 + random.nextInt(100);
                int alarmIncrement = random.nextInt(15);

                int total = rs.getInt("vehicle_total") + totalIncrement;
                int online = rs.getInt("today_online") + onlineIncrement;
                int alarm = rs.getInt("today_alarm") + alarmIncrement;

                updateStmt.setInt(1, total);
                updateStmt.setInt(2, online);
                updateStmt.setInt(3, alarm);
                updateStmt.setInt(4, city);
                updateStmt.executeUpdate();
                count++;
            }
            log.info("[车辆统计] 更新{}个城市", count);
        } catch (Exception e) {
            log.warn("[车辆统计] 更新失败: {}", e.getMessage());
        }
    }


    /**
     * 更新客货运量 (passenger_freight)
     */
    private void updatePassengerFreight() {
        // 1. 先查询车型名称+ID+客货运量
        String selectSql = "SELECT vt.vehicle_type_id, vt.vehicle_type,  pfv.passenger_volume,  pfv.freight_volume  FROM passenger_freight_by_vehicle pfv  INNER JOIN vehicle_type vt ON pfv.vehicle_type_id = vt.vehicle_type_id";
        String updateSql = "UPDATE passenger_freight_by_vehicle SET passenger_volume=?, freight_volume=? WHERE vehicle_type_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            int count = 0;
            while (rs.next()) {
                // 先获取vehicle_type_id（关键！）
                int vehicleTypeId = rs.getInt("vehicle_type_id");
                String vehicleType = rs.getString("vehicle_type");
                long passengerVolume = rs.getLong("passenger_volume");
                long freightVolume = rs.getLong("freight_volume");

                int passengerIncrement, freightIncrement;
                switch (vehicleType) {
                    case "大型车":
                        passengerIncrement = 50 + random.nextInt(150);
                        freightIncrement = 200 + random.nextInt(500);
                        break;
                    case "中型车":
                        passengerIncrement = 30 + random.nextInt(100);
                        freightIncrement = 100 + random.nextInt(300);
                        break;
                    case "小型车":
                        passengerIncrement = 80 + random.nextInt(200);
                        freightIncrement = 20 + random.nextInt(80);
                        break;
                    default:
                        passengerIncrement = 50 + random.nextInt(100);
                        freightIncrement = 50 + random.nextInt(100);
                }

                // 用int类型的vehicleTypeId作为条件（而非字符串）
                updateStmt.setLong(1, passengerVolume + passengerIncrement);
                updateStmt.setLong(2, freightVolume + freightIncrement);
                updateStmt.setInt(3, vehicleTypeId); // 关键修复：setInt而非setString
                updateStmt.executeUpdate();
                count++;
            }
            log.info("[客货运量] 更新{}种车型", count);
        } catch (Exception e) {
            // 关键：打印完整堆栈，而非仅getMessage
            log.warn("[客货运量] 更新失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新访豫车辆数据 - 优化版：每次只随机更新100条
     */
    private void updateVehicleToHenan() {
        // 使用批量更新，每次随机更新100条记录
        String updateSql = "UPDATE in_to_henan SET vehicle_count = vehicle_count + ? WHERE it_id = ?";
        String selectIdsSql = "SELECT it_id FROM in_to_henan ORDER BY RAND() LIMIT 100";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectIdsSql);
            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("it_id");
                int increment = 10 + random.nextInt(91);
                updateStmt.setInt(1, increment);
                updateStmt.setInt(2, id);
                updateStmt.addBatch();
                count++;
            }
            updateStmt.executeBatch();
            log.info("[访豫车辆] 更新{}条记录", count);
        } catch (Exception e) {
            log.warn("[访豫车辆] 更新失败: {}", e.getMessage());
        }
    }

    /**
     * 更新出省车辆数据 - 优化版：每次只随机更新100条
     */
    private void updateOutOfHenan() {
        String updateSql = "UPDATE out_of_henan SET vehicle_count = vehicle_count + ? WHERE oo_id = ?";
        String selectIdsSql = "SELECT oo_id FROM out_of_henan ORDER BY RAND() LIMIT 100";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectIdsSql);
            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("oo_id");
                int increment = 5 + random.nextInt(46);
                updateStmt.setInt(1, increment);
                updateStmt.setInt(2, id);
                updateStmt.addBatch();
                count++;
            }
            updateStmt.executeBatch();
            log.info("[出省车辆] 更新{}条记录", count);
        } catch (Exception e) {
            log.warn("[出省车辆] 更新失败: {}", e.getMessage());
        }
    }

    private double getPeakFactor(int hour) {
        if (hour >= 7 && hour <= 9) return 1.0;
        if (hour >= 17 && hour <= 19) return 0.95;
        if (hour >= 11 && hour <= 13) return 0.6;
        if (hour >= 22 || hour <= 5) return 0.3;
        return 0.5;
    }

    private int adjustInt(int current, int min, int max, double rate) {
        int target = min + random.nextInt(Math.max(1, max - min));
        return Math.max(min, Math.min(max, current + (int)((target - current) * rate)));
    }

    private double adjustDouble(double current, double min, double max, double rate) {
        double target = min + random.nextDouble() * (max - min);
        return Math.max(min, Math.min(max, current + (target - current) * rate));
    }

    /**
     * 更新交通流量数据 (traffic_flow)
     * 更新所有月份的数据，按公路类型（高速公路/城镇公路）更新流量
     */
    private void updateTrafficFlow() {
        // 更新所有月份的数据，不只是最新月份
        String selectSql = "SELECT flow_id, type_id, flow_count FROM traffic_flow";
        String updateSql = "UPDATE traffic_flow SET flow_count = ? WHERE flow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            int count = 0;
            while (rs.next()) {
                int flowId = rs.getInt("flow_id");
                int typeId = rs.getInt("type_id");
                long flowCount = rs.getLong("flow_count");

                // 根据公路类型设置不同的增量范围
                int increment;
                if (typeId == 1) { // 高速公路
                    increment = 500 + random.nextInt(2000);
                } else { // 城镇公路
                    increment = 300 + random.nextInt(1500);
                }

                updateStmt.setLong(1, flowCount + increment);
                updateStmt.setInt(2, flowId);
                updateStmt.addBatch();
                count++;
            }
            updateStmt.executeBatch();
            log.info("[交通流量] 更新{}条记录", count);
        } catch (Exception e) {
            log.warn("[交通流量] 更新失败: {}", e.getMessage());
        }
    }

    /**
     * 更新交通工具流量数据 (vehicle_traffic)
     * 更新所有月份的数据，按车型（小型车/中型车/大型车）更新流量
     */
    private void updateVehicleTraffic() {
        // 更新所有月份的数据，不只是最新月份
        String selectSql = "SELECT vt_id, vehicle_type_id, vehicle_count FROM vehicle_traffic";
        String updateSql = "UPDATE vehicle_traffic SET vehicle_count = ? WHERE vt_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            int count = 0;
            while (rs.next()) {
                int vtId = rs.getInt("vt_id");
                int vehicleTypeId = rs.getInt("vehicle_type_id");
                long vehicleCount = rs.getLong("vehicle_count");

                // 根据车型设置不同的增量范围
                int increment;
                switch (vehicleTypeId) {
                    case 1: // 大型车
                        increment = 100 + random.nextInt(500);
                        break;
                    case 2: // 中型车
                        increment = 200 + random.nextInt(800);
                        break;
                    case 3: // 小型车
                        increment = 300 + random.nextInt(1200);
                        break;
                    default:
                        increment = 150 + random.nextInt(600);
                }

                updateStmt.setLong(1, vehicleCount + increment);
                updateStmt.setInt(2, vtId);
                updateStmt.addBatch();
                count++;
            }
            updateStmt.executeBatch();
            log.info("[交通工具流量] 更新{}条记录", count);
        } catch (Exception e) {
            log.warn("[交通工具流量] 更新失败: {}", e.getMessage());
        }
    }

    /**
     * 更新交通违规事件数据 (traffic_violation)
     * 更新所有月份的数据
     */
    private void updateTrafficViolation() {
        String selectSql = "SELECT tv.tv_id, tv.violation_id, tv.violation_count " +
                          "FROM traffic_violation tv";
        String updateSql = "UPDATE traffic_violation SET violation_count = ? WHERE tv_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement selectStmt = conn.createStatement();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            ResultSet rs = selectStmt.executeQuery(selectSql);
            int count = 0;
            while (rs.next()) {
                int tvId = rs.getInt("tv_id");
                int violationCount = rs.getInt("violation_count");

                // 随机增加违规数量
                int increment = 1 + random.nextInt(10);
                updateStmt.setInt(1, violationCount + increment);
                updateStmt.setInt(2, tvId);
                updateStmt.addBatch();
                count++;
            }
            updateStmt.executeBatch();
            log.info("[交通违规] 更新{}条记录", count);
        } catch (Exception e) {
            log.warn("[交通违规] 更新失败: {}", e.getMessage());
        }
    }
}
