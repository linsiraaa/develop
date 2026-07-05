package com.example.mock.generator;

import com.example.mock.generator.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeDimGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(TimeDimGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO time_dim (year, month, month_key) VALUES (?, ?, ?)";

    private static final int REQUIRED_RECORDS = 20; // 修改为生成20条记录
    private List<Integer> timeIds = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        // 忽略传入的count参数，固定生成20条
        LocalDate currentDate = LocalDate.now();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < REQUIRED_RECORDS; i++) {
                LocalDate date = currentDate.minusMonths(i);
                int year = date.getYear();
                int month = date.getMonthValue();
                String monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                pstmt.setInt(1, year);
                pstmt.setInt(2, month);
                pstmt.setString(3, monthKey);
                pstmt.addBatch();
            }

            pstmt.executeBatch();

            // 获取生成的ID
            ResultSet rs = pstmt.getGeneratedKeys();
            while (rs.next()) {
                timeIds.add(rs.getInt(1));
            }
            rs.close();

            log.info("成功生成 {} 条时间维度数据（20个月数据）", REQUIRED_RECORDS); // 更新日志信息
        }
    }

    public List<Integer> getTimeIds() {
        return timeIds;
    }
}