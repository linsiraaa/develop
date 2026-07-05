package com.example.one.mockbean;

// RoadCondition.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer rcId;
    private Integer timeId;
    private Integer alertCount;
    private Double congestionIndex;
    private Double efficiency;
    private Integer abnormalEvents;
}
