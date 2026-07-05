package com.example.one.Controller;

import com.example.one.Service.DashboardDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardDataService dashboardDataService;

    @GetMapping("/top-data")
    public List<Map<String, Object>> getDashboardTopData() {
        return dashboardDataService.getDashboardTopData();
    }
}
