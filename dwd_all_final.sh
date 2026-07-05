#!/bin/bash

APP=default

if [ -n "$1" ] ;then
   do_date=$1
else
   do_date="2026-07"
fi

HIVE_EXEC="hive -e \"
set hive.execution.engine=mr;
set hive.exec.dynamic.partition.mode=nonstrict;
use $APP;"

echo "==================== 🚀 开始逐个刷新 DWD 表 ===================="

# 1
echo "👉 正在清洗：dwd_charge..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_charge PARTITION (dt) SELECT id, city_id, vehicle_total, today_online, today_alarm, '$do_date' AS dt FROM ods_charge;\""
sleep 10

# 2
echo "👉 正在清洗：dwd_traffic_flow..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_traffic_flow PARTITION (dt) SELECT flow_id, time_id, type_id, flow_count, '$do_date' AS dt FROM ods_traffic_flow;\""
sleep 10

# 3
echo "👉 正在清洗：dwd_road_condition..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_road_condition PARTITION (dt) SELECT rc_id, time_id, alert_count, congestion_index, efficiency, abnormal_events, avg_speed, wait_time, '$do_date' AS dt FROM ods_road_condition;\""
sleep 10

# 4
echo "👉 正在清洗：dwd_toll_collection..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_toll_collection PARTITION (dt) SELECT tc_id, time_id, station_name, station_city_id, toll_amount, vehicle_count, '$do_date' AS dt FROM ods_toll_collection;\""
sleep 10

# 5
echo "👉 正在清洗：dwd_traffic_violation..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_traffic_violation PARTITION (dt) SELECT tv_id, time_id, violation_id, violation_count, '$do_date' AS dt FROM ods_traffic_violation;\""
sleep 10

# 6
echo "👉 正在清洗：dwd_vehicle_traffic..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_vehicle_traffic PARTITION (dt) SELECT vt_id, time_id, vehicle_type_id, vehicle_count, '$do_date' AS dt FROM ods_vehicle_traffic;\""
sleep 10

# 7
echo "👉 正在清洗：dwd_in_to_henan..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_in_to_henan PARTITION (dt) SELECT it_id, time_id, from_city_id, to_city_id, vehicle_count, '$do_date' AS dt FROM ods_in_to_henan;\""
sleep 10

# 8
echo "👉 正在清洗：dwd_out_of_henan..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_out_of_henan PARTITION (dt) SELECT oo_id, time_id, from_city_id, to_city_id, vehicle_count, '$do_date' AS dt FROM ods_out_of_henan;\""
sleep 10

# 9
echo "👉 正在清洗：dwd_passenger_freight_by_vehicle..."
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE dwd_passenger_freight_by_vehicle PARTITION (dt) SELECT pfv_id, time_id, vehicle_type_id, passenger_volume, freight_volume, '$do_date' AS dt FROM ods_passenger_freight_by_vehicle;\""
sleep 10

# 10-14 维度表
echo "👉 正在清洗：10-14 维度表群..."
hive -e "
set hive.execution.engine=mr;
set hive.exec.dynamic.partition.mode=nonstrict;
use $APP;
INSERT OVERWRITE TABLE dwd_city PARTITION (dt) SELECT city_id, city_name, province, latitude, longitude, '$do_date' AS dt FROM ods_city;
INSERT OVERWRITE TABLE dwd_road_type PARTITION (dt) SELECT type_id, type_name, '$do_date' AS dt FROM ods_road_type;
INSERT OVERWRITE TABLE dwd_time_dim PARTITION (dt) SELECT time_id, year, month, month_key, '$do_date' AS dt FROM ods_time_dim;
INSERT OVERWRITE TABLE dwd_vehicle_type PARTITION (dt) SELECT vehicle_type_id, vehicle_type, '$do_date' AS dt FROM ods_vehicle_type;
INSERT OVERWRITE TABLE dwd_violation_type PARTITION (dt) SELECT violation_id, violation_name, '$do_date' AS dt FROM ods_violation_type;
"

echo "==================== 🎉 14张表全盘大复活！ ===================="
