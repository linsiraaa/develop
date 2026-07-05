package com.example.one.pojo;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ads_vehicle_traffic_by_type")
@Data
public class ToolFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month")
    private String month;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_count")
    private Double flowValue;

}
