package com.example.one.Service;

import com.example.one.pojo.VehicleToHenan;
import com.example.one.repository.VehicleToHenanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class VehicleToHenanService {

    @Autowired
    private VehicleToHenanRepository repository;

    public List<VehicleToHenan> getAllVehicles() {
        return repository.findAllVehicles();
    }
}
