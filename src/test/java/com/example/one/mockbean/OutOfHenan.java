package com.example.one.mockbean;

// OutOfHenan.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutOfHenan implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer ooId;
    private Integer timeId;
    private Integer fromCityId;
    private Integer toCityId;
    private Long vehicleCount;
}