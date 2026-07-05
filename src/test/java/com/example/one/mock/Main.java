package com.example.one.mock;

// Main.java

import com.example.one.generator.*;
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
            timeDimGenerator.generate(12); // 生成12个月的数据

            // 生成统计数据
            PassengerFreightGenerator passengerFreightGenerator = new PassengerFreightGenerator();
            passengerFreightGenerator.generate(12);

            TrafficFlowGenerator trafficFlowGenerator = new TrafficFlowGenerator();
            trafficFlowGenerator.generate(24); // 每种道路类型12个月

            VehicleTrafficGenerator vehicleTrafficGenerator = new VehicleTrafficGenerator();
            vehicleTrafficGenerator.generate(36); // 每种车型12个月

            RoadConditionGenerator roadConditionGenerator = new RoadConditionGenerator();
            roadConditionGenerator.generate(12);

            OutOfHenanGenerator outOfHenanGenerator = new OutOfHenanGenerator(cityGenerator);
            outOfHenanGenerator.generate(1000);

            InToHenanGenerator inToHenanGenerator = new InToHenanGenerator(cityGenerator);
            inToHenanGenerator.generate(1000);

            TollCollectionGenerator tollCollectionGenerator = new TollCollectionGenerator(cityGenerator);
            tollCollectionGenerator.generate(500);

            TrafficViolationGenerator trafficViolationGenerator = new TrafficViolationGenerator();
            trafficViolationGenerator.generate(120); // 每种违规类型12个月

            log.info("交通模拟数据生成完成！");
        } catch (SQLException e) {
            log.error("生成数据时发生SQL异常", e);
        } catch (Exception e) {
            log.error("生成数据时发生未知异常", e);
        }
    }
}