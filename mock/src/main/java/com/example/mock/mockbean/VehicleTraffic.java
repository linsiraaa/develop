package com.example.mock.mockbean;

// VehicleTraffic.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTraffic implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer vtId;
    private Integer timeId;
    private Integer vehicleTypeId;
    private Long vehicleCount;
}