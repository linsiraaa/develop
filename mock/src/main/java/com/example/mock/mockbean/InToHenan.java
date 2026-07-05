package com.example.mock.mockbean;

// InToHenan.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InToHenan implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer itId;
    private Integer timeId;
    private Integer fromCityId;
    private Integer toCityId;
    private Long vehicleCount;
}