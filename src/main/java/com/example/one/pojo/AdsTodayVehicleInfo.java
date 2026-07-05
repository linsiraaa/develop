package com.example.one.pojo;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ads_road_condition")
public class AdsTodayVehicleInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 假设新增自增主键
    private int id;

    @Column(name = "alert_count")
    private Long alertCount;

    @Column(name = "congestion_index")
    private BigDecimal congestionIndex;

    @Column(name = "efficiency")
    private BigDecimal efficiency;

    @Column(name = "abnormal_events")
    private Long abnormalEvents;

    @Column(name = "avg_speed")
    private Integer avgSpeed;

    @Column(name = "wait_time")
    private Integer waitTime;

}