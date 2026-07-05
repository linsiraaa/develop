package com.example.one.pojo;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "toll_station_data")
public class TollStationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;    // 收费站名称
    private String city;           // 所在城市

    // 日常收费
    private BigDecimal dailyRevenue;
    // 客运车辆收费
    private BigDecimal passengerRevenue;
    // 货运车辆收费
    private BigDecimal freightRevenue;

    private Date date;
    private Date createTime;
    private Date updateTime;
}