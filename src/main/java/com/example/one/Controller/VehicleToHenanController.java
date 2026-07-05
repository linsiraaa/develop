package com.example.one.Controller;

import com.example.one.pojo.VehicleToHenan;
import com.example.one.Service.VehicleToHenanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleToHenanController {

    @Autowired
    private VehicleToHenanService vehicleService;

    @GetMapping("/all")
    public List<VehicleToHenan> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
}