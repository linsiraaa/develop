package com.example.one.pojo;

import lombok.Data;

import java.util.List;

@Data
public class LineData {
    private String name;
    private List<Integer> data;

    public LineData(String 环比, List<Integer> list) {
    }
}