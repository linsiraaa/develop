package com.example.one.pojo;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ads_in_to_henan")
public class VehicleToHenan {

    @Id
    @Column(name = "from_city_name")
    private String fromCityName;

    @Column(name = "from_latitude")
    private BigDecimal fromLatitude;

    @Column(name = "from_longitude")
    private BigDecimal fromLongitude;

    @Column(name = "vehicle_count")
    private Long vehicleCount;

    @Column(name = "dt")
    private String dt;

    // Getters and Setters
    public String getFromCityName() {
        return fromCityName;
    }

    public void setFromCityName(String fromCityName) {
        this.fromCityName = fromCityName;
    }

    public BigDecimal getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(BigDecimal fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public BigDecimal getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(BigDecimal fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public Long getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Long vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}