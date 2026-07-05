package com.example.one.pojo;

import javax.persistence.*;

@Entity
@Table(name = "out_of_henan")
public class OutOfHenan {
    @Id
    @Column(name = "oo_id")
    private Integer ooId;

    @Column(name = "time_id")
    private Integer timeId;

    @Column(name = "from_city_id")
    private Integer fromCityId;

    @Column(name = "to_city_id")
    private Integer toCityId;

    @Column(name = "vehicle_count")
    private Long vehicleCount;

    // Getters and Setters
    public Integer getOoId() { return ooId; }
    public void setOoId(Integer ooId) { this.ooId = ooId; }
    public Integer getTimeId() { return timeId; }
    public void setTimeId(Integer timeId) { this.timeId = timeId; }
    public Integer getFromCityId() { return fromCityId; }
    public void setFromCityId(Integer fromCityId) { this.fromCityId = fromCityId; }
    public Integer getToCityId() { return toCityId; }
    public void setToCityId(Integer toCityId) { this.toCityId = toCityId; }
    public Long getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(Long vehicleCount) { this.vehicleCount = vehicleCount; }
}
