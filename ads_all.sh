#!/bin/bash

APP=default

if [ -n "$1" ] ;then
   do_date=$1
else
   do_date="2026-07"
fi

HIVE_EXEC="hive -e \"set hive.execution.engine=mr; set hive.exec.dynamic.partition.mode=nonstrict; use $APP;"

echo "======================================== START ADS ========================================"

echo ">>> 1/9 ads_in_to_henan"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_in_to_henan PARTITION (dt) SELECT c.city_name, c.latitude, c.longitude, SUM(i.vehicle_count), '$do_date' AS dt FROM dwd_in_to_henan i JOIN dwd_city c ON i.from_city_id = c.city_id WHERE i.dt='$do_date' AND c.dt='$do_date' GROUP BY c.city_name, c.latitude, c.longitude;\""
sleep 10

echo ">>> 2/9 ads_out_of_henan"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_out_of_henan PARTITION (stat_month) SELECT fc.city_name, fc.latitude, fc.longitude, tc.city_name, tc.latitude, tc.longitude, o.vehicle_count, '$do_date' AS dt, '$do_date' AS stat_month FROM dwd_out_of_henan o JOIN dwd_city fc ON o.from_city_id = fc.city_id JOIN dwd_city tc ON o.to_city_id = tc.city_id WHERE o.dt='$do_date' AND fc.dt='$do_date' AND tc.dt='$do_date';\""
sleep 10

echo ">>> 3/9 ads_passenger_freight"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_passenger_freight PARTITION (dt) SELECT v.vehicle_type, SUM(p.passenger_volume), SUM(p.freight_volume), '$do_date' AS dt FROM dwd_passenger_freight_by_vehicle p JOIN dwd_vehicle_type v ON p.vehicle_type_id = v.vehicle_type_id WHERE p.dt='$do_date' AND v.dt='$do_date' GROUP BY v.vehicle_type;\""
sleep 10

echo ">>> 4/9 ads_road_condition"
eval "${HIVE_EXEC} INSERT INTO TABLE ads_road_condition SELECT rc_id, time_id, alert_count, congestion_index, efficiency, abnormal_events, avg_speed, wait_time FROM dwd_road_condition WHERE dt='$do_date';\""
sleep 10

echo ">>> 5/9 ads_today_vehicle_info"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_today_vehicle_info PARTITION (dt) SELECT c.city_name, ch.vehicle_total, ch.today_online, ch.today_alarm, '$do_date' AS dt FROM dwd_charge ch JOIN dwd_city c ON ch.city_id = c.city_id WHERE ch.dt='$do_date' AND c.dt='$do_date';\""
sleep 10

echo ">>> 6/9 ads_toll_collection_rank"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_toll_collection_rank PARTITION (dt) SELECT station_name, SUM(toll_amount), '$do_date' AS dt FROM dwd_toll_collection WHERE dt='$do_date' GROUP BY station_name ORDER BY SUM(toll_amount) DESC;\""
sleep 10

echo ">>> 7/9 ads_traffic_flow_analysis"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_traffic_flow_analysis PARTITION (dt) SELECT td.year, td.month, rt.type_name, SUM(f.flow_count), NULL, NULL, NULL, NULL, HASH(td.year, td.month, rt.type_name), '$do_date' AS dt FROM dwd_traffic_flow f JOIN dwd_road_type rt ON f.type_id = rt.type_id JOIN dwd_time_dim td ON f.time_id = td.time_id WHERE f.dt='$do_date' AND rt.dt='$do_date' AND td.dt='$do_date' GROUP BY td.year, td.month, rt.type_name;\""
sleep 10

echo ">>> 8/9 ads_traffic_violation"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_traffic_violation PARTITION (dt) SELECT v.violation_name, SUM(tv.violation_count), '$do_date' AS dt FROM dwd_traffic_violation tv JOIN dwd_violation_type v ON tv.violation_id = v.violation_id WHERE tv.dt='$do_date' AND v.dt='$do_date' GROUP BY v.violation_name;\""
sleep 10

echo ">>> 9/9 ads_vehicle_traffic_by_type"
eval "${HIVE_EXEC} INSERT OVERWRITE TABLE ads_vehicle_traffic_by_type PARTITION (dt) SELECT td.year, td.month, vt.vehicle_type, SUM(vt2.vehicle_count), HASH(td.year, td.month, vt.vehicle_type), '$do_date' AS dt FROM dwd_vehicle_traffic vt2 JOIN dwd_vehicle_type vt ON vt2.vehicle_type_id = vt.vehicle_type_id JOIN dwd_time_dim td ON vt2.time_id = td.time_id WHERE vt2.dt='$do_date' AND vt.dt='$do_date' AND td.dt='$do_date' GROUP BY td.year, td.month, vt.vehicle_type;\""
sleep 10

echo "======================================== ADS DONE ========================================"
