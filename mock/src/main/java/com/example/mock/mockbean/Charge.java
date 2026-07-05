package com.example.mock.mockbean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Charge implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** 关联城市ID（对应city表的city_id） */
    private Integer cityId;

    /** 时间ID（关联time_dim表的time_id） */
    private Integer timeId;

    /** 充电桩总数 */
    private Integer vehicleTotal;

    /** 今日在线数量 */
    private Integer todayOnline;

    /** 今日告警数量 */
    private Integer todayAlarm;
}
