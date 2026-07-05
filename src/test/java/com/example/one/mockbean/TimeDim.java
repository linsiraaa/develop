package com.example.one.mockbean;

// TimeDim.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeDim implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer timeId;
    private Integer year;
    private Integer month;
    private String monthKey;
}