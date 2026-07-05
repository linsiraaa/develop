package com.example.one.mockbean;

// TrafficViolation.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficViolation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tvId;
    private Integer timeId;
    private Integer violationId;
    private Integer violationCount;
}