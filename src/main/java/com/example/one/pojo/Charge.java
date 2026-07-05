package com.example.one.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ads_today_vehicle_info")
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "vehicle_total")
    private Integer vehicleTotal;

    @Column(name = "today_online")
    private Integer todayOnline;

    @Column(name = "today_alarm")
    private Integer todayAlarm;

    public Charge(String cityName, Integer vehicleTotal, Integer todayOnline, Integer todayAlarm) {
        this.cityName = cityName;
        this.vehicleTotal = vehicleTotal;
        this.todayOnline = todayOnline;
        this.todayAlarm = todayAlarm;
    }

    public Charge() {
    }

    // 手动添加getter方法（解决IDE Lombok问题）
    public Integer getVehicleTotal() {
        return vehicleTotal;
    }

    public Integer getTodayOnline() {
        return todayOnline;
    }

    public Integer getTodayAlarm() {
        return todayAlarm;
    }

    public String getCityName() {
        return cityName;
    }
}
