package com.example.mock.mock;

import com.example.mock.generator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            log.info("开始生成交通模拟数据...");
            // 生成基础维度数据
            CityGenerator cityGenerator = new CityGenerator();
            cityGenerator.generate(30);
            RoadTypeGenerator roadTypeGenerator = new RoadTypeGenerator();
            roadTypeGenerator.generate(2);
            VehicleTypeGenerator vehicleTypeGenerator = new VehicleTypeGenerator();
            vehicleTypeGenerator.generate(3);

            ViolationTypeGenerator violationTypeGenerator = new ViolationTypeGenerator();
            violationTypeGenerator.generate(10);

            TimeDimGenerator timeDimGenerator = new TimeDimGenerator();
            timeDimGenerator.generate(24); // 生成两年48个月的数据

            // 生成统计数据 (总计约15000条)
            PassengerFreightByVehicleGenerator passengerFreightByVehicleGenerator = new PassengerFreightByVehicleGenerator();
            passengerFreightByVehicleGenerator.generate(36);  // 36条

            TrafficFlowGenerator trafficFlowGenerator = new TrafficFlowGenerator();
            trafficFlowGenerator.generate(200);  // 200条

            VehicleTrafficGenerator vehicleTrafficGenerator = new VehicleTrafficGenerator();
            vehicleTrafficGenerator.generate(150);  // 150条

            RoadConditionGenerator roadConditionGenerator = new RoadConditionGenerator();
            roadConditionGenerator.generate(14);  // 14条

            OutOfHenanGenerator outOfHenanGenerator = new OutOfHenanGenerator(cityGenerator);
            outOfHenanGenerator.generate(5000);  // 5000条

            InToHenanGenerator inToHenanGenerator = new InToHenanGenerator(cityGenerator);
            inToHenanGenerator.generate(5000);  // 5000条

            TollCollectionGenerator tollCollectionGenerator = new TollCollectionGenerator(cityGenerator);
            tollCollectionGenerator.generate(4000);  // 4000条

            TrafficViolationGenerator trafficViolationGenerator = new TrafficViolationGenerator();
            trafficViolationGenerator.generate(500);  // 500条

            com.example.mock.generator.ChargeGenerator chargeGenerator = new com.example.mock.generator.ChargeGenerator(cityGenerator);
            chargeGenerator.generate(1);  // 18条（河南18市）


            log.info("交通模拟数据生成完成！");
        } catch (SQLException e) {
            log.error("生成数据时发生SQL异常", e);
        } catch (Exception e) {
            log.error("生成数据时发生未知异常", e);
        }
    }
}