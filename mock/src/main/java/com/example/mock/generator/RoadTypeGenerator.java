package com.example.mock.generator;

// RoadTypeGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class RoadTypeGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(RoadTypeGenerator.class);
    private static final String INSERT_SQL = "INSERT IGNORE INTO road_type (type_name) VALUES (?)";

    @Override
    public void generate(int count) throws SQLException {
        String[] roadTypes = {"高速公路", "城镇公路"};

        for (int i = 0; i < Math.min(count, roadTypes.length); i++) {
            executeInsertAndGetId(INSERT_SQL, roadTypes[i]);
        }

        log.info("成功生成 {} 条公路类型数据", Math.min(count, roadTypes.length));
    }
}