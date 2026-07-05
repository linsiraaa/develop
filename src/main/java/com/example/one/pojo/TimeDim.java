package com.example.one.pojo;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "time_dim")
public class TimeDim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer timeId;

    private Integer year;
    private Integer month;
    private String monthKey;

    // Getters and Setters
    public Integer getTimeId() {
        return timeId;
    }

    public void setTimeId(Integer timeId) {
        this.timeId = timeId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getMonthKey() {
        return monthKey;
    }

    public void setMonthKey(String monthKey) {
        this.monthKey = monthKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeDim timeDim = (TimeDim) o;
        return Objects.equals(timeId, timeDim.timeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeId);
    }
}
