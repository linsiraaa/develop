package com.example.one.mockbean;

// PassengerFreight.java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerFreight implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer pfId;
    private Integer timeId;
    private Long passengerVolume;
    private Long freightVolume;
}