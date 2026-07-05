package com.example.one.Controller;

import com.example.one.Service.VehicleInfoService;
import com.example.one.pojo.AdsTodayVehicleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleInfoController {
    @Autowired
    private VehicleInfoService service;

    // 查询ID=1的数据
    @GetMapping("/{id}")
    public ResponseEntity<AdsTodayVehicleInfo> getDataById(
            @PathVariable int id
    ) {
        return service.getVehicleDataById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}


