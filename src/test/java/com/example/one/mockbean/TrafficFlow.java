package com.example.one.mockbean;

// TrafficFlow.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficFlow implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer flowId;
    private Integer timeId;
    private Integer typeId;
    private Long flowCount;
}