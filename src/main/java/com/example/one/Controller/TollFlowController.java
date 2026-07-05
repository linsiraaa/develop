package com.example.one.Controller;

import com.example.one.Service.TollFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/toll")
public class TollFlowController {
    @Autowired
    private TollFlowService toolFlowService;

    @GetMapping("/data")
    public Map<String, Object> getTrafficData() {
        List<String> months = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月");
        Map<String, List<Double>> data = toolFlowService.getTrafficDataByMonths(months);

        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("seriesData", data);
        return result;
    }
}