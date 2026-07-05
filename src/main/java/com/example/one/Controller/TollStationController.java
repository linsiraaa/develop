package com.example.one.Controller;

import com.example.one.Service.TollStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
@RequestMapping("/api/tollstation")
public class TollStationController {

    @Autowired
    private TollStationService tollStationService;

    @GetMapping("/ranking/{city}")
    public Map<String, Object> getTollStationRanking(@PathVariable String city) {
        return tollStationService.getTollStationRanking(city);
    }

    @GetMapping("/comparison")
    public Map<String, Object> getTollStationComparison() {
        return tollStationService.getTollStationComparison();
    }
}