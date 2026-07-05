package com.example.mock.generator;

// ViolationTypeGenerator.java


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ViolationTypeGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(ViolationTypeGenerator.class);
    private static final String INSERT_SQL = "INSERT IGNORE INTO violation_type (violation_name) VALUES (?)";

    @Override
    public void generate(int count) throws SQLException {
        String[] violationTypes = {
                "超速行驶", "闯红灯", "违规变道", "不按导向车道行驶", "占用应急车道",
                "酒后驾驶", "无证驾驶", "疲劳驾驶", "未系安全带", "遮挡号牌"
        };

        for (int i = 0; i < Math.min(count, violationTypes.length); i++) {
            executeInsertAndGetId(INSERT_SQL, violationTypes[i]);
        }

        log.info("成功生成 {} 条违规类型数据", Math.min(count, violationTypes.length));
    }
}