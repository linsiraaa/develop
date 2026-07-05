package com.example.one.repository;

import com.example.one.pojo.VehicleToHenan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleToHenanRepository extends JpaRepository<VehicleToHenan, String> {

    @Query(value = "SELECT * FROM ads_in_to_henan", nativeQuery = true)
    List<VehicleToHenan> findAllVehicles();
}
