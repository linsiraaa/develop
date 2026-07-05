package com.traffic.flink;

import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 河南省交通大数据 Flink 实时处理作业
 * 从基础表读取数据，实时计算聚合指标，推送到Redis
 */
public class TrafficRealtimeJob {

    // Kafka配置
    private static final String KAFKA_BOOTSTRAP_SERVERS = "hadoop102:9092";
    private static final String KAFKA_TOPIC = "maxwell";
    private static final String KAFKA_GROUP_ID = "traffic-flink-group";

    // Redis配置
    private static final String REDIS_HOST = "hadoop102";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = null;

    // 侧输出标签 - 基础表数据
    private static final OutputTag<JSONObject> TOLL_COLLECTION_TAG = new OutputTag<JSONObject>("toll_collection") {};
    private static final OutputTag<JSONObject> ROAD_CONDITION_TAG = new OutputTag<JSONObject>("road_condition") {};
    private static final OutputTag<JSONObject> CHARGE_TAG = new OutputTag<JSONObject>("charge") {};
    private static final OutputTag<JSONObject> IN_TO_HENAN_TAG = new OutputTag<JSONObject>("in_to_henan") {};
    private static final OutputTag<JSONObject> OUT_OF_HENAN_TAG = new OutputTag<JSONObject>("out_of_henan") {};
    private static final OutputTag<JSONObject> PASSENGER_FREIGHT_TAG = new OutputTag<JSONObject>("passenger_freight") {};
    private static final OutputTag<JSONObject> TRAFFIC_FLOW_TAG = new OutputTag<JSONObject>("traffic_flow") {};
    private static final OutputTag<JSONObject> VEHICLE_TRAFFIC_TAG = new OutputTag<JSONObject>("vehicle_traffic") {};
    private static final OutputTag<JSONObject> TRAFFIC_VIOLATION_TAG = new OutputTag<JSONObject>("traffic_violation") {};

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // Kafka Source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(KAFKA_BOOTSTRAP_SERVERS)
                .setTopics(KAFKA_TOPIC)
                .setGroupId(KAFKA_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> kafkaStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source");

        // 解析JSON并路由到不同的侧输出
        SingleOutputStreamOperator<JSONObject> mainStream = kafkaStream
                .map((MapFunction<String, JSONObject>) value -> {
                    try { return JSON.parseObject(value); } catch (Exception e) { return null; }
                })
                .filter(json -> json != null)
                .process(new TableRouterFunction());

        // 1. 收费站排行榜 - 从toll_collection聚合
        mainStream.getSideOutput(TOLL_COLLECTION_TAG)
                .keyBy(json -> json.getJSONObject("data").getString("station_name"))
                .process(new TollCollectionAggregator())
                .addSink(new RedisSink("traffic:toll_rank"))
                .name("Toll Rank Redis Sink");

        // 2. 路况指标 - 从road_condition聚合
        mainStream.getSideOutput(ROAD_CONDITION_TAG)
                .keyBy(json -> "global")
                .process(new RoadConditionAggregator())
                .addSink(new RedisSink("traffic:road_condition"))
                .name("Road Condition Redis Sink");

        // 3. 城市车辆统计 - 从charge表聚合
        mainStream.getSideOutput(CHARGE_TAG)
                .keyBy(json -> json.getJSONObject("data").getIntValue("city_id"))
                .process(new ChargeAggregator())
                .addSink(new RedisSink("traffic:vehicle_info"))
                .name("Vehicle Info Redis Sink");

        // 4. 访豫车辆统计 - 从in_to_henan聚合
        mainStream.getSideOutput(IN_TO_HENAN_TAG)
                .keyBy(json -> json.getJSONObject("data").getIntValue("from_city_id"))
                .process(new InToHenanAggregator())
                .addSink(new RedisSink("traffic:in_to_henan"))
                .name("In To Henan Redis Sink");

        // 5. 出省车辆统计 - 从out_of_henan聚合
        mainStream.getSideOutput(OUT_OF_HENAN_TAG)
                .keyBy(json -> {
                    JSONObject data = json.getJSONObject("data");
                    return data.getIntValue("from_city_id") + "_" + data.getIntValue("to_city_id");
                })
                .process(new OutOfHenanAggregator())
                .addSink(new RedisSink("traffic:out_of_henan"))
                .name("Out Of Henan Redis Sink");

        // 6. 客货运量统计 - 从passenger_freight_by_vehicle聚合
        mainStream.getSideOutput(PASSENGER_FREIGHT_TAG)
                .keyBy(json -> json.getJSONObject("data").getIntValue("vehicle_type_id"))
                .process(new PassengerFreightAggregator())
                .addSink(new RedisSink("traffic:passenger_freight"))
                .name("Passenger Freight Redis Sink");

        // 7. 交通流量统计 - 从traffic_flow聚合
        mainStream.getSideOutput(TRAFFIC_FLOW_TAG)
                .keyBy(json -> json.getJSONObject("data").getIntValue("type_id"))
                .process(new TrafficFlowAggregator())
                .addSink(new RedisSink("traffic:traffic_flow"))
                .name("Traffic Flow Redis Sink");

        // 8. 交通工具流量统计 - 从vehicle_traffic聚合
        mainStream.getSideOutput(VEHICLE_TRAFFIC_TAG)
                .keyBy(json -> json.getJSONObject("data").getIntValue("vehicle_type_id"))
                .process(new VehicleTrafficAggregator())
                .addSink(new RedisSink("traffic:vehicle_traffic"))
                .name("Vehicle Traffic Redis Sink");

        // 9. 交通违规事件统计 - 从traffic_violation聚合
        mainStream.getSideOutput(TRAFFIC_VIOLATION_TAG)
                .keyBy(json -> json.getJSONObject("data").getIntValue("violation_id"))
                .process(new TrafficViolationAggregator())
                .addSink(new RedisSink("traffic:traffic_violation"))
                .name("Traffic Violation Redis Sink");
        env.execute("Traffic Realtime Compute Job");
    }


    /**
     * 表路由函数 - 将不同表的数据路由到对应的侧输出
     * 支持基础表和ADS表两种命名方式
     */
    public static class TableRouterFunction extends ProcessFunction<JSONObject, JSONObject> {
        @Override
        public void processElement(JSONObject value, Context ctx, Collector<JSONObject> out) {
            String table = value.getString("table");
            String type = value.getString("type");
            if (!"insert".equals(type) && !"update".equals(type)) return;
            if (table == null) return;

            switch (table) {
                // 基础表
                case "toll_collection":
                case "ads_toll_collection_rank":
                    ctx.output(TOLL_COLLECTION_TAG, value);
                    break;
                case "road_condition":
                case "ads_road_condition":
                    ctx.output(ROAD_CONDITION_TAG, value);
                    break;
                case "charge":
                case "ads_today_vehicle_info":
                    ctx.output(CHARGE_TAG, value);
                    break;
                case "in_to_henan":
                case "ads_in_to_henan":
                    ctx.output(IN_TO_HENAN_TAG, value);
                    break;
                case "out_of_henan":
                case "ads_out_of_henan":
                    ctx.output(OUT_OF_HENAN_TAG, value);
                    break;
                case "passenger_freight_by_vehicle":
                case "ads_passenger_freight":
                    ctx.output(PASSENGER_FREIGHT_TAG, value);
                    break;
                case "traffic_flow":
                case "ads_traffic_flow_analysis":
                    ctx.output(TRAFFIC_FLOW_TAG, value);
                    break;
                case "vehicle_traffic":
                case "ads_vehicle_traffic_by_type":
                    ctx.output(VEHICLE_TRAFFIC_TAG, value);
                    break;
                case "traffic_violation":
                case "ads_traffic_violation":
                    ctx.output(TRAFFIC_VIOLATION_TAG, value);
                    break;
            }
        }
    }

    /**
     * 收费站聚合器 - 按收费站名称聚合收费金额
     */
    public static class TollCollectionAggregator extends KeyedProcessFunction<String, JSONObject, Tuple2<String, String>> {
        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            String stationName = data.getString("station_name");
            double tollAmount = data.getDoubleValue("toll_amount");

            // 直接输出最新值（Mock已经累加过了）
            JSONObject result = new JSONObject();
            result.put("station_name", stationName);
            result.put("toll_amount", tollAmount);
            out.collect(Tuple2.of(stationName, result.toJSONString()));
        }
    }

    /**
     * 路况聚合器
     */
    public static class RoadConditionAggregator extends KeyedProcessFunction<String, JSONObject, Tuple2<String, String>> {
        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            JSONObject result = new JSONObject();
            result.put("alert_count", data.getIntValue("alert_count"));
            result.put("congestion_index", data.getDoubleValue("congestion_index"));
            result.put("efficiency", data.getDoubleValue("efficiency"));
            result.put("abnormal_events", data.getIntValue("abnormal_events"));
            result.put("avg_speed", data.getIntValue("avg_speed"));
            result.put("wait_time", data.getIntValue("wait_time"));

            out.collect(Tuple2.of("global", result.toJSONString()));
        }
    }


    /**
     * 城市车辆统计聚合器
     */
    public static class ChargeAggregator extends KeyedProcessFunction<Integer, JSONObject, Tuple2<String, String>> {
        // 城市ID到城市名称的映射
        private static final Map<Integer, String> CITY_MAP = new HashMap<>();
        static {
            CITY_MAP.put(1, "安阳"); CITY_MAP.put(2, "许昌"); CITY_MAP.put(3, "漯河");
            CITY_MAP.put(4, "开封"); CITY_MAP.put(5, "濮阳"); CITY_MAP.put(6, "鹤壁");
            CITY_MAP.put(7, "济源"); CITY_MAP.put(8, "驻马店"); CITY_MAP.put(9, "周口");
            CITY_MAP.put(10, "洛阳"); CITY_MAP.put(11, "三门峡"); CITY_MAP.put(12, "南阳");
            CITY_MAP.put(13, "焦作"); CITY_MAP.put(14, "商丘"); CITY_MAP.put(15, "信阳");
            CITY_MAP.put(16, "平顶山"); CITY_MAP.put(17, "郑州"); CITY_MAP.put(18, "新乡");
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int cityId = data.getIntValue("city_id");
            String cityName = CITY_MAP.getOrDefault(cityId, "未知");

            // 直接输出最新值
            JSONObject result = new JSONObject();
            result.put("city_name", cityName);
            result.put("vehicle_total", data.getIntValue("vehicle_total"));
            result.put("today_online", data.getIntValue("today_online"));
            result.put("today_alarm", data.getIntValue("today_alarm"));

            out.collect(Tuple2.of(cityName, result.toJSONString()));
        }
    }

    /**
     * 访豫车辆聚合器
     */
    public static class InToHenanAggregator extends KeyedProcessFunction<Integer, JSONObject, Tuple2<String, String>> {
        // 外省城市ID到城市名称和经纬度的映射
        private static final Map<Integer, String[]> CITY_INFO = new HashMap<>();
        static {
            CITY_INFO.put(19, new String[]{"潍坊", "36.706800", "119.161800"});
            CITY_INFO.put(20, new String[]{"济南", "36.651200", "117.120000"});
            CITY_INFO.put(21, new String[]{"青岛", "36.067100", "120.382600"});
            CITY_INFO.put(22, new String[]{"烟台", "37.463800", "121.447900"});
            CITY_INFO.put(23, new String[]{"威海", "37.513100", "122.120400"});
            CITY_INFO.put(24, new String[]{"沈阳", "41.805700", "123.431500"});
            CITY_INFO.put(25, new String[]{"大连", "38.914000", "121.614700"});
            CITY_INFO.put(26, new String[]{"太原", "37.870600", "112.548900"});
            CITY_INFO.put(27, new String[]{"大同", "40.076800", "113.300100"});
            CITY_INFO.put(28, new String[]{"广州", "23.129200", "113.264400"});
            CITY_INFO.put(29, new String[]{"深圳", "22.543100", "114.057900"});
            CITY_INFO.put(30, new String[]{"渭南", "34.509000", "109.509000"});
            CITY_INFO.put(31, new String[]{"西安", "34.341600", "108.939800"});
            CITY_INFO.put(32, new String[]{"成都", "30.572300", "104.066500"});
            CITY_INFO.put(33, new String[]{"秦皇岛", "39.935400", "119.600500"});
            CITY_INFO.put(34, new String[]{"邯郸", "36.618200", "114.491700"});
            CITY_INFO.put(35, new String[]{"唐山", "39.630900", "118.180200"});
            CITY_INFO.put(36, new String[]{"廊坊", "39.538000", "116.683800"});
            CITY_INFO.put(37, new String[]{"石家庄", "38.037300", "114.514900"});
            CITY_INFO.put(38, new String[]{"宁波", "29.874600", "121.550400"});
            CITY_INFO.put(39, new String[]{"杭州", "30.274100", "120.155100"});
            CITY_INFO.put(40, new String[]{"武汉", "30.592800", "114.305500"});
            CITY_INFO.put(41, new String[]{"襄阳", "32.042000", "112.135000"});
            CITY_INFO.put(42, new String[]{"长沙", "28.228200", "112.938800"});
            CITY_INFO.put(43, new String[]{"岳阳", "29.357100", "113.129200"});
            CITY_INFO.put(44, new String[]{"无锡", "31.491200", "120.311900"});
            CITY_INFO.put(45, new String[]{"南京", "32.060300", "118.796900"});
            CITY_INFO.put(46, new String[]{"苏州", "31.298900", "120.585300"});
            CITY_INFO.put(47, new String[]{"阜阳", "32.899600", "115.817300"});
            CITY_INFO.put(48, new String[]{"合肥", "31.861100", "117.285600"});
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int fromCityId = data.getIntValue("from_city_id");
            long vehicleCount = data.getLongValue("vehicle_count");

            String[] cityInfo = CITY_INFO.get(fromCityId);
            if (cityInfo == null) return;

            // 直接输出最新值
            JSONObject result = new JSONObject();
            result.put("from_city_name", cityInfo[0]);
            result.put("from_latitude", Double.parseDouble(cityInfo[1]));
            result.put("from_longitude", Double.parseDouble(cityInfo[2]));
            result.put("vehicle_count", vehicleCount);

            out.collect(Tuple2.of(cityInfo[0], result.toJSONString()));
        }
    }


    /**
     * 出省车辆聚合器
     */
    public static class OutOfHenanAggregator extends KeyedProcessFunction<String, JSONObject, Tuple2<String, String>> {
        // 河南城市ID映射
        private static final Map<Integer, String[]> HENAN_CITY = new HashMap<>();
        // 外省城市ID映射
        private static final Map<Integer, String[]> OUT_CITY = new HashMap<>();
        static {
            HENAN_CITY.put(1, new String[]{"安阳", "36.061800", "114.359100"});
            HENAN_CITY.put(2, new String[]{"许昌", "34.021200", "113.835200"});
            HENAN_CITY.put(3, new String[]{"漯河", "33.573200", "114.022200"});
            HENAN_CITY.put(4, new String[]{"开封", "34.797800", "114.352900"});
            HENAN_CITY.put(5, new String[]{"濮阳", "35.768400", "115.051800"});
            HENAN_CITY.put(6, new String[]{"鹤壁", "35.749500", "114.174200"});
            HENAN_CITY.put(7, new String[]{"济源", "35.069600", "112.588400"});
            HENAN_CITY.put(8, new String[]{"驻马店", "32.981200", "114.022100"});
            HENAN_CITY.put(9, new String[]{"周口", "33.620700", "114.639300"});
            HENAN_CITY.put(10, new String[]{"洛阳", "34.660400", "112.422700"});
            HENAN_CITY.put(11, new String[]{"三门峡", "34.777100", "111.190600"});
            HENAN_CITY.put(12, new String[]{"南阳", "33.015600", "112.532200"});
            HENAN_CITY.put(13, new String[]{"焦作", "35.233900", "113.246600"});
            HENAN_CITY.put(14, new String[]{"商丘", "34.436700", "115.657700"});
            HENAN_CITY.put(15, new String[]{"信阳", "32.137400", "114.092400"});
            HENAN_CITY.put(16, new String[]{"平顶山", "33.732800", "113.294900"});
            HENAN_CITY.put(17, new String[]{"郑州", "34.746600", "113.625300"});
            HENAN_CITY.put(18, new String[]{"新乡", "35.303900", "113.880300"});

            OUT_CITY.put(22, new String[]{"烟台", "37.463800", "121.447900"});
            OUT_CITY.put(26, new String[]{"太原", "37.870600", "112.548900"});
            OUT_CITY.put(27, new String[]{"大同", "40.076800", "113.300100"});
            OUT_CITY.put(31, new String[]{"西安", "34.341600", "108.939800"});
            OUT_CITY.put(42, new String[]{"长沙", "28.228200", "112.938800"});
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int fromCityId = data.getIntValue("from_city_id");
            int toCityId = data.getIntValue("to_city_id");
            long vehicleCount = data.getLongValue("vehicle_count");

            String[] fromCity = HENAN_CITY.get(fromCityId);
            String[] toCity = OUT_CITY.get(toCityId);
            if (fromCity == null || toCity == null) return;

            // 直接输出最新值
            JSONObject result = new JSONObject();
            result.put("from_city_name", fromCity[0]);
            result.put("from_latitude", Double.parseDouble(fromCity[1]));
            result.put("from_longitude", Double.parseDouble(fromCity[2]));
            result.put("to_city_name", toCity[0]);
            result.put("to_latitude", Double.parseDouble(toCity[1]));
            result.put("to_longitude", Double.parseDouble(toCity[2]));
            result.put("vehicle_count", vehicleCount);

            String key = fromCity[0] + "_" + toCity[0];
            out.collect(Tuple2.of(key, result.toJSONString()));
        }
    }

    /**
     * 客货运量聚合器
     */
    public static class PassengerFreightAggregator extends KeyedProcessFunction<Integer, JSONObject, Tuple2<String, String>> {
        private static final Map<Integer, String> VEHICLE_TYPE_MAP = new HashMap<>();
        static {
            VEHICLE_TYPE_MAP.put(1, "大型车");
            VEHICLE_TYPE_MAP.put(2, "中型车");
            VEHICLE_TYPE_MAP.put(3, "小型车");
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int vehicleTypeId = data.getIntValue("vehicle_type_id");
            long passengerVolume = data.getLongValue("passenger_volume");
            long freightVolume = data.getLongValue("freight_volume");

            String vehicleType = VEHICLE_TYPE_MAP.getOrDefault(vehicleTypeId, "未知");

            // 直接输出最新值
            JSONObject result = new JSONObject();
            result.put("vehicle_type", vehicleType);
            result.put("passenger_volume", passengerVolume);
            result.put("freight_volume", freightVolume);

            out.collect(Tuple2.of(vehicleType, result.toJSONString()));
        }
    }

    /**
     * 交通流量聚合器 - 按公路类型和月份聚合流量
     * Redis Key格式: traffic:traffic_flow:{time_id}_{type_id}
     */
    public static class TrafficFlowAggregator extends KeyedProcessFunction<Integer, JSONObject, Tuple2<String, String>> {
        private static final Map<Integer, String> ROAD_TYPE_MAP = new HashMap<>();
        static {
            ROAD_TYPE_MAP.put(1, "高速公路");
            ROAD_TYPE_MAP.put(2, "城镇公路");
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int typeId = data.getIntValue("type_id");
            long flowCount = data.getLongValue("flow_count");
            int timeId = data.getIntValue("time_id");

            String typeName = ROAD_TYPE_MAP.getOrDefault(typeId, "未知");

            // 输出最新值，key包含time_id以区分不同月份
            JSONObject result = new JSONObject();
            result.put("type_id", typeId);
            result.put("type_name", typeName);
            result.put("flow_count", flowCount);
            result.put("time_id", timeId);

            // 使用 time_id_type_id 作为key，支持多月份数据
            String key = timeId + "_" + typeId;
            out.collect(Tuple2.of(key, result.toJSONString()));
        }
    }

    /**
     * 交通工具流量聚合器 - 按车型和月份聚合流量
     * Redis Key格式: traffic:vehicle_traffic:{time_id}_{vehicle_type_id}
     */
    public static class VehicleTrafficAggregator extends KeyedProcessFunction<Integer, JSONObject, Tuple2<String, String>> {
        private static final Map<Integer, String> VEHICLE_TYPE_MAP = new HashMap<>();
        static {
            VEHICLE_TYPE_MAP.put(1, "大型车");
            VEHICLE_TYPE_MAP.put(2, "中型车");
            VEHICLE_TYPE_MAP.put(3, "小型车");
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int vehicleTypeId = data.getIntValue("vehicle_type_id");
            long vehicleCount = data.getLongValue("vehicle_count");
            int timeId = data.getIntValue("time_id");

            String vehicleType = VEHICLE_TYPE_MAP.getOrDefault(vehicleTypeId, "未知");

            // 输出最新值，key包含time_id以区分不同月份
            JSONObject result = new JSONObject();
            result.put("vehicle_type_id", vehicleTypeId);
            result.put("vehicle_type", vehicleType);
            result.put("vehicle_count", vehicleCount);
            result.put("time_id", timeId);

            // 使用 time_id_vehicle_type_id 作为key，支持多月份数据
            String key = timeId + "_" + vehicleTypeId;
            out.collect(Tuple2.of(key, result.toJSONString()));
        }
    }

    /**
     * 交通违规事件聚合器 - 按违规类型和月份聚合
     * Redis Key格式: traffic:traffic_violation:{time_id}_{violation_id}
     */
    public static class TrafficViolationAggregator extends KeyedProcessFunction<Integer, JSONObject, Tuple2<String, String>> {
        private static final Map<Integer, String> VIOLATION_TYPE_MAP = new HashMap<>();
        static {
            VIOLATION_TYPE_MAP.put(1, "超速行驶");
            VIOLATION_TYPE_MAP.put(2, "闯红灯");
            VIOLATION_TYPE_MAP.put(3, "违规变道");
            VIOLATION_TYPE_MAP.put(4, "违规停车");
            VIOLATION_TYPE_MAP.put(5, "酒驾");
            VIOLATION_TYPE_MAP.put(6, "疲劳驾驶");
            VIOLATION_TYPE_MAP.put(7, "超载");
            VIOLATION_TYPE_MAP.put(8, "未系安全带");
            VIOLATION_TYPE_MAP.put(9, "逆行");
            VIOLATION_TYPE_MAP.put(10, "其他违规");
        }

        @Override
        public void processElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
            JSONObject data = value.getJSONObject("data");
            int violationId = data.getIntValue("violation_id");
            int violationCount = data.getIntValue("violation_count");
            int timeId = data.getIntValue("time_id");

            String violationName = VIOLATION_TYPE_MAP.getOrDefault(violationId, "其他违规");

            // 输出最新值，key包含time_id以区分不同月份
            JSONObject result = new JSONObject();
            result.put("violation_id", violationId);
            result.put("violation_name", violationName);
            result.put("violation_count", violationCount);
            result.put("time_id", timeId);

            // 使用 time_id_violation_id 作为key，支持多月份数据
            String key = timeId + "_" + violationId;
            out.collect(Tuple2.of(key, result.toJSONString()));
        }
    }

    /**
     * Redis Sink - 将计算结果写入Redis
     */
    public static class RedisSink extends RichSinkFunction<Tuple2<String, String>> {
        private final String keyPrefix;
        private transient JedisPool jedisPool;

        public RedisSink(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        @Override
        public void open(Configuration parameters) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            
            if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
                jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 2000, REDIS_PASSWORD);
            } else {
                jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 2000);
            }
        }

        @Override
        public void invoke(Tuple2<String, String> value, Context context) {
            try (Jedis jedis = jedisPool.getResource()) {
                String key = keyPrefix + ":" + value.f0;
                jedis.set(key, value.f1);
                // 同时更新到Hash结构，方便批量获取
                jedis.hset(keyPrefix, value.f0, value.f1);
            } catch (Exception e) {
                System.err.println("Redis写入失败: " + e.getMessage());
            }
        }

        @Override
        public void close() {
            if (jedisPool != null) {
                jedisPool.close();
            }
        }
    }
}
