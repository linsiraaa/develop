package com.example.mock.mockbean;

// City.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer cityId;
    private String cityName;
    private String province;
    private Double latitude;
    private Double longitude;
}