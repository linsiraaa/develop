package com.example.one.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ads_traffic_violation")
@Data
public class ViolationJan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month")
    private Integer month;

    @Column(name = "violation_name")
    private String violationName;

    @Column(name = "violation_count")
    private Integer violationCount;
}