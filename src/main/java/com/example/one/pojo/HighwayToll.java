package com.example.one.pojo;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ads_toll_collection_rank")
public class HighwayToll {

    @Column(name = "station_name")
    private String stationName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toll_amount")
    private BigDecimal tollAmount;

    // Getters and Setters


    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }



    public BigDecimal getTollAmount() {
        return tollAmount;
    }

    public void setTollAmount(BigDecimal tollAmount) {
        this.tollAmount = tollAmount;
    }


}