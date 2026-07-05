package com.example.one.Controller;

import com.example.one.Service.ChargeStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/charge")
public class ChargeStatsController {

    @Autowired
    private ChargeStatsService chargeStatsService;

    @GetMapping("/stats")
    public Map<String, Object> getChargeStats() {
        return chargeStatsService.getChargeStatistics();
    }
}
