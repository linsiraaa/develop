package com.example.one.mockbean;

// ViolationType.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer violationId;
    private String violationName;
}