package com.example.mock.mockbean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerFreightByVehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer pfvId;
    private Integer timeId;
    private Integer vehicleTypeId;
    private Long passengerVolume;
    private Long freightVolume;
}