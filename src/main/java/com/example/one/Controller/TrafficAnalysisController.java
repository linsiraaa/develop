package com.example.one.Controller;

import com.example.one.pojo.TrafficFlowDTO;
import com.example.one.Service.TrafficAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class TrafficAnalysisController {
    @Autowired
    private TrafficAnalysisService trafficAnalysisService;

    @GetMapping("/flow")
    public List<TrafficFlowDTO> getTrafficFlow() {
        return trafficAnalysisService.getTrafficFlowAnalysis();
    }
}
