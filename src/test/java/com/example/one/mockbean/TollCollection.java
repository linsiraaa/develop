package com.example.one.mockbean;

// TollCollection.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TollCollection implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tcId;
    private Integer timeId;
    private String stationName;
    private Integer stationCityId;
    private Double tollAmount;
    private Long vehicleCount;
}
