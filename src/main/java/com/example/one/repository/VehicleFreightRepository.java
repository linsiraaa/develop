package com.example.one.repository;

import com.example.one.pojo.VehicleFreight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleFreightRepository extends JpaRepository<VehicleFreight, String> {

    List<VehicleFreight> findByVehicleTypeIn(List<String> vehicleTypes);
}