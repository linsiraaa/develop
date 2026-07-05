package com.example.one.Controller;

import com.example.one.Service.TrafficAnalysisService;
import com.example.one.Service.adsTrafficAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    @Autowired
    private adsTrafficAnalysisService service;

    @GetMapping("/2025")
    public ResponseEntity<?> get2025Traffic() {
        Map<String, Object> data = service.get2025TrafficStatistics();
        return ResponseEntity.ok(new ApiResponse(200, "Success", data));
    }
}