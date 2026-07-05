package com.example.one.generator;

// DataGenerator.java


import com.example.one.mock.DatabaseConfig;

import java.sql.*;
import java.util.Random;

public abstract class DataGenerator {
    protected final Random random = new Random();

    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    /**
     * 执行插入语句并返回生成的自增主键
     */
    protected int executeInsertAndGetId(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("执行插入后未能获取生成的主键");
                }
            }
        }
    }

    public abstract void generate(int count) throws SQLException;
}
