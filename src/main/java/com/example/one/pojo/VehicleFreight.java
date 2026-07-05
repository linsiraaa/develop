package com.example.one.pojo;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ads_passenger_freight")
@Data
public class VehicleFreight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_type")
    private String vehicleType;  // 车型（大型车、中型车、小型车）

    @Column(name = "passenger_volume")
    private Long passengerVolume;  // 修改为Long类型，匹配BIGINT

    @Column(name = "freight_volume")
    private Long freightVolume;    // 修改为Long类型，匹配BIGINT
}