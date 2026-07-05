package com.example.one.pojo;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ads_out_of_henan")
@Data
public class ADSOutOfHenan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 实际数据库没有，逻辑层不会用这个字段查询，只是为了 DTO 格式需要

    @Column(name = "from_city_name")
    private String fromCityName;

    @Column(name = "from_latitude")
    private BigDecimal fromLatitude;

    @Column(name = "from_longitude")
    private BigDecimal fromLongitude;

    @Column(name = "to_city_name")
    private String toCityName;

    @Column(name = "to_latitude")
    private BigDecimal toLatitude;

    @Column(name = "to_longitude")
    private BigDecimal toLongitude;

    @Column(name = "vehicle_count")
    private Long vehicleCount;

    @Column(name = "dt")
    private String dt;
}