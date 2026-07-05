package com.example.mock.mockbean;

// RoadCondition.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer rcId;
    private Integer timeId;
    private Integer alertCount;       // 道路警情数
    private Double congestionIndex;   // 拥堵指数
    private Double efficiency;        // 通行效率
    private Integer abnormalEvents;   // 异常事件数
    private Integer avgSpeed;         // 新增：平均速度(km/h)
    private Integer waitingTime;      // 新增：等待时长(分钟)
}
