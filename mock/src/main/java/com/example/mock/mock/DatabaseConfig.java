package com.example.mock.mock;

// DatabaseConfig.java


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DatabaseConfig {
    private static final String URL ;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String DRIVER_CLASS;

    static {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (inputStream == null) {
                throw new IOException("无法加载数据库配置文件: db.properties");
            }
            properties.load(inputStream);

            URL = properties.getProperty("jdbc.url");
            USERNAME = properties.getProperty("jdbc.username");
            PASSWORD = properties.getProperty("jdbc.password");
            DRIVER_CLASS = properties.getProperty("jdbc.driverClassName");

            // 注册驱动
            Class.forName(DRIVER_CLASS);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("初始化数据库配置失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        // 设置连接超时为10秒
        DriverManager.setLoginTimeout(10);
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        // 设置查询超时
        conn.setNetworkTimeout(java.util.concurrent.Executors.newSingleThreadExecutor(), 30000);
        return conn;
    }
}