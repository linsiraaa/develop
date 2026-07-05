package com.example.one.pojo;

import javax.persistence.*;

@Entity
@Table(name = "in_to_henan")
public class InToHenan {
    @Id
    @Column(name = "it_id")
    private Integer itId;

    @Column(name = "time_id")
    private Integer timeId;

    @Column(name = "from_city_id")
    private Integer fromCityId;

    @Column(name = "to_city_id")
    private Integer toCityId;

    @Column(name = "vehicle_count")
    private Long vehicleCount;

    // Getters and Setters
    public Integer getItId() { return itId; }
    public void setItId(Integer itId) { this.itId = itId; }
    public Integer getTimeId() { return timeId; }
    public void setTimeId(Integer timeId) { this.timeId = timeId; }
    public Integer getFromCityId() { return fromCityId; }
    public void setFromCityId(Integer fromCityId) { this.fromCityId = fromCityId; }
    public Integer getToCityId() { return toCityId; }
    public void setToCityId(Integer toCityId) { this.toCityId = toCityId; }
    public Long getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(Long vehicleCount) { this.vehicleCount = vehicleCount; }
}
