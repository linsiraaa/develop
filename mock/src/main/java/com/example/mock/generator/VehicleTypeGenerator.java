package com.example.mock.generator;

// VehicleTypeGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class VehicleTypeGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(VehicleTypeGenerator.class);
    private static final String INSERT_SQL = "INSERT IGNORE INTO vehicle_type (vehicle_type) VALUES (?)";

    @Override
    public void generate(int count) throws SQLException {
        String[] vehicleTypes = {"小型车", "中型车", "大型车"};

        for (int i = 0; i < Math.min(count, vehicleTypes.length); i++) {
            executeInsertAndGetId(INSERT_SQL, vehicleTypes[i]);
        }

        log.info("成功生成 {} 条交通工具类型数据", Math.min(count, vehicleTypes.length));
    }
}