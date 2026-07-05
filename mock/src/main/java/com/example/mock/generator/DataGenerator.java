package com.example.mock.generator;

// DataGenerator.java







import com.example.mock.mock.DatabaseConfig;

import java.sql.*;
import java.util.Random;

public abstract class DataGenerator {
    protected final Random random = new Random();

    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    /**
     * 执行插入语句并返回生成的自增主键
     * INSERT IGNORE 跳过重复数据时返回 -1
     */
    protected int executeInsertAndGetId(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            int affectedRows = pstmt.executeUpdate();
            
            // INSERT IGNORE 跳过重复数据时 affectedRows 为 0
            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    public abstract void generate(int count) throws SQLException;
}
