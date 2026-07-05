package com.example.one.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.one.pojo.ToolFlow;
import com.example.one.repository.ToolFlowRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.*;

    @Service
    public class TollFlowService {
        @Autowired
        private ToolFlowRepository repository;

        public Map<String, List<Double>> getTrafficDataByMonths(List<String> months) {
            List<Object[]> rawData = repository.findGroupedByMonthAndType(months);
            Map<String, List<Double>> result = new HashMap<>();

            // 初始化数据结构
            for (String type : Arrays.asList("小型车", "中型车", "大型车")) {
                result.put(type, new ArrayList<>());
            }

            // 解析数据
            for (Object[] row : rawData) {
                String type = (String) row[1];
                Double value = ((Number) row[0]).doubleValue();
                result.get(type).add(value);
            }

            return result;
        }
    }

