package com.example.one.Controller;

import com.example.one.Service.VehicleFreightService;
import com.example.one.pojo.VehicleFreight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleFreightController {

    @Autowired
    private VehicleFreightService vehicleFreightService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getVehicleFreightSummary() {
        Map<String, Object> data = vehicleFreightService.getFormattedVehicleFreightSummary();
        return ResponseEntity.ok(data);
    }
}