package com.example.one.pojo;

import javax.persistence.*;

@Entity
@Table(name = "ads_road_condition")
public class RoadCondition {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "alert_count")
    private Long alertCount;

    @Column(name = "congestion_index", precision = 5, scale = 2)
    private Double congestionIndex;

    @Column(name = "efficiency", precision = 5, scale = 2)
    private Double efficiency;

    @Column(name = "abnormal_events")
    private Long abnormalEvents;

    @Column(name = "avg_speed")
    private Integer avgSpeed;

    @Column(name = "wait_time")
    private Integer waitTime;

    @Column(name = "dt")
    private String dt;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getAlertCount() { return alertCount; }
    public void setAlertCount(Long alertCount) { this.alertCount = alertCount; }
    public Double getCongestionIndex() { return congestionIndex; }
    public void setCongestionIndex(Double congestionIndex) { this.congestionIndex = congestionIndex; }
    public Double getEfficiency() { return efficiency; }
    public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
    public Long getAbnormalEvents() { return abnormalEvents; }
    public void setAbnormalEvents(Long abnormalEvents) { this.abnormalEvents = abnormalEvents; }
    public Integer getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Integer avgSpeed) { this.avgSpeed = avgSpeed; }
    public Integer getWaitTime() { return waitTime; }
    public void setWaitTime(Integer waitTime) { this.waitTime = waitTime; }
    public String getDt() { return dt; }
    public void setDt(String dt) { this.dt = dt; }
}
