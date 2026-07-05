package com.example.one.Service;

import com.example.one.pojo.ViolationJan;
import com.example.one.repository.ViolationJanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



import com.example.one.pojo.ViolationJan;

import com.example.one.repository.ViolationJanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccidentService {

    @Autowired
    private ViolationJanRepository violationJanRepository;

    public Map<String, Object> getAccidentData() {
        Map<String, Object> result = new HashMap<>();

        // 获取最新月份数据
        Integer latestMonth = violationJanRepository.findLatestMonth();
        if (latestMonth == null) {
            return createDefaultResponse();
        }

        // 获取事件类型分布
        List<Object[]> eventTypeCountsArray = violationJanRepository.findViolationCountsByMonth(latestMonth);
        Map<String, Integer> eventTypeCounts = new HashMap<>();
        for (Object[] row : eventTypeCountsArray) {
            eventTypeCounts.put((String)row[0], ((Number)row[1]).intValue());
        }

        // 计算当前告警总数
        int currentAlertCount = eventTypeCounts.values().stream().mapToInt(Integer::intValue).sum();

        result.put("currentAlertCount", currentAlertCount);
        result.put("eventTypeCounts", eventTypeCounts);

        return result;
    }

    private Map<String, Object> createDefaultResponse() {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("currentAlertCount", 0);
        defaultData.put("eventTypeCounts", new HashMap<>());
        return defaultData;
    }
}