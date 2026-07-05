package com.example.one.pojo;

public class TrafficFlowDTO {
    private Integer id;
    private String fromCity;
    private Double fromLongitude;
    private Double fromLatitude;
    private Integer fromWeight = 5;
    private Integer fromSymbolSize;
    private String toCity;
    private Double toLongitude;
    private Double toLatitude;
    private Integer toWeight = 5;
    private Integer toSymbolSize;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }
    public Double getFromLongitude() { return fromLongitude; }
    public void setFromLongitude(Double fromLongitude) { this.fromLongitude = fromLongitude; }
    public Double getFromLatitude() { return fromLatitude; }
    public void setFromLatitude(Double fromLatitude) { this.fromLatitude = fromLatitude; }
    public Integer getFromWeight() { return fromWeight; }
    public void setFromWeight(Integer fromWeight) { this.fromWeight = fromWeight; }
    public Integer getFromSymbolSize() { return fromSymbolSize; }
    public void setFromSymbolSize(Integer fromSymbolSize) { this.fromSymbolSize = fromSymbolSize; }
    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }
    public Double getToLongitude() { return toLongitude; }
    public void setToLongitude(Double toLongitude) { this.toLongitude = toLongitude; }
    public Double getToLatitude() { return toLatitude; }
    public void setToLatitude(Double toLatitude) { this.toLatitude = toLatitude; }
    public Integer getToWeight() { return toWeight; }
    public void setToWeight(Integer toWeight) { this.toWeight = toWeight; }
    public Integer getToSymbolSize() { return toSymbolSize; }
    public void setToSymbolSize(Integer toSymbolSize) { this.toSymbolSize = toSymbolSize; }
}