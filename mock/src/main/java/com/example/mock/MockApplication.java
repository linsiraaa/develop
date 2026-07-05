package com.example.mock;

import com.example.mock.scheduler.RealTimeDataUpdater;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
@EnableScheduling
public class MockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockApplication.class, args);
    }

    /**
     * 应用启动后初始化车辆统计数据
     */
    @Bean
    CommandLineRunner initData(RealTimeDataUpdater updater) {
        return args -> {
            // 1. 原有逻辑：初始化车辆统计数据
            updater.initializeVehicleStats();
            // 2. 新增：执行一次全量数据更新（调用所有updateXXX方法）
            updater.updateData();
            System.out.println("启动阶段全量数据初始化完成");
        };
    }
}
