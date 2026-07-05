package com.example.one.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ads_traffic_flow_analysis")
@Data
public class AdsTrafficFlowAnalysis {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "current_flow")
    private Long currentFlow;

    @Column(name = "last_year_flow")
    private Long lastYearFlow;

    @Column(name = "yoy_growth_percent")
    private Double yoyGrowthPercent;

    @Column(name = "prev_month_flow")
    private Long prevMonthFlow;

    @Column(name = "mom_growth_percent")
    private Double momGrowthPercent;

    // 生成getter/setter
}