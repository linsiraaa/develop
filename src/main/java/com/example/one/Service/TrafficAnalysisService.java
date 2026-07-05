package com.example.one.Service;

import com.example.one.pojo.*;
import com.example.one.repository.TrafficRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrafficAnalysisService {
    private static final int MAX_CONNECTIONS_PER_NON_HENAN_CITY = 3;
    private static final int MIN_VEHICLE_COUNT = 100;
    private static final int MAX_VEHICLE_COUNT = 1000;

    @Autowired
    private TrafficRepository trafficRepository;

    public List<TrafficFlowDTO> getTrafficFlowAnalysis() {
        // 1. 获取所有城市并建立映射
        List<City> cities = trafficRepository.findAll();
        Map<Integer, City> cityMap = cities.stream()
                .collect(Collectors.toMap(City::getCityId, city -> city));

        // 2. 获取河南城市和非河南城市列表
        List<City> henanCities = cities.stream()
                .filter(c -> "河南".equals(c.getProvince()))
                .collect(Collectors.toList());

        List<City> nonHenanCities = cities.stream()
                .filter(c -> !"河南".equals(c.getProvince()))
                .collect(Collectors.toList());

        // 3. 处理入豫数据 (外省→河南)
        Map<Integer, List<InToHenan>> inFlowMap = processInFlows(henanCities, nonHenanCities);

        // 4. 处理出豫数据 (河南→外省)
        Map<Integer, List<OutOfHenan>> outFlowMap = processOutFlows(henanCities, nonHenanCities);

        // 5. 计算城市权重和出现次数
        Map<Integer, Integer> fromCityCount = calculateCityCounts(inFlowMap, outFlowMap, true);
        Map<Integer, Integer> toCityCount = calculateCityCounts(inFlowMap, outFlowMap, false);

        // 6. 获取最大出现次数用于计算symbolSize
        int maxFromCount = fromCityCount.values().stream().max(Integer::compare).orElse(1);
        int maxToCount = toCityCount.values().stream().max(Integer::compare).orElse(1);

        // 7. 构建最终结果
        return buildResult(inFlowMap, outFlowMap, cityMap,
                fromCityCount, toCityCount, maxFromCount, maxToCount);
    }

    private Map<Integer, List<InToHenan>> processInFlows(List<City> henanCities, List<City> nonHenanCities) {
        Map<Integer, List<InToHenan>> flowMap = new HashMap<>();
        Random random = new Random();

        // 为每个外省城市分配1-3个河南城市连接
        for (City fromCity : nonHenanCities) {
            List<City> connectedHenanCities = new ArrayList<>(henanCities);
            Collections.shuffle(connectedHenanCities);

            int connections = 1 + random.nextInt(MAX_CONNECTIONS_PER_NON_HENAN_CITY);
            connections = Math.min(connections, connectedHenanCities.size());

            List<InToHenan> flows = new ArrayList<>();
            for (int i = 0; i < connections; i++) {
                City toCity = connectedHenanCities.get(i);
                flows.add(createInToHenanFlow(fromCity.getCityId(), toCity.getCityId()));
            }
            flowMap.put(fromCity.getCityId(), flows);
        }
        return flowMap;
    }

    private InToHenan createInToHenanFlow(int fromCityId, int toCityId) {
        Random random = new Random();
        InToHenan flow = new InToHenan();
        flow.setFromCityId(fromCityId);
        flow.setToCityId(toCityId);
        flow.setVehicleCount(MIN_VEHICLE_COUNT +
                (long)(random.nextDouble() * (MAX_VEHICLE_COUNT - MIN_VEHICLE_COUNT + 1)));
        return flow;
    }

    private Map<Integer, List<OutOfHenan>> processOutFlows(List<City> henanCities, List<City> nonHenanCities) {
        Map<Integer, List<OutOfHenan>> flowMap = new HashMap<>();
        Random random = new Random();

        // 每个河南城市可以连接多个外省城市
        for (City fromCity : henanCities) {
            List<City> connectedNonHenanCities = new ArrayList<>(nonHenanCities);
            Collections.shuffle(connectedNonHenanCities);

            int connections = 3 + random.nextInt(5); // 3-7个连接
            connections = Math.min(connections, connectedNonHenanCities.size());

            List<OutOfHenan> flows = new ArrayList<>();
            for (int i = 0; i < connections; i++) {
                City toCity = connectedNonHenanCities.get(i);
                flows.add(createOutOfHenanFlow(fromCity.getCityId(), toCity.getCityId()));
            }
            flowMap.put(fromCity.getCityId(), flows);
        }
        return flowMap;
    }

    private OutOfHenan createOutOfHenanFlow(int fromCityId, int toCityId) {
        Random random = new Random();
        OutOfHenan flow = new OutOfHenan();
        flow.setFromCityId(fromCityId);
        flow.setToCityId(toCityId);
        flow.setVehicleCount(MIN_VEHICLE_COUNT +
                (long)(random.nextDouble() * (MAX_VEHICLE_COUNT - MIN_VEHICLE_COUNT + 1)));
        return flow;
    }

    private Map<Integer, Integer> calculateCityCounts(
            Map<Integer, List<InToHenan>> inFlowMap,
            Map<Integer, List<OutOfHenan>> outFlowMap,
            boolean isFromCity) {

        Map<Integer, Integer> countMap = new HashMap<>();

        // 统计入豫数据
        inFlowMap.forEach((fromCityId, flows) -> {
            flows.forEach(flow -> {
                int cityId = isFromCity ? flow.getFromCityId() : flow.getToCityId();
                countMap.merge(cityId, 1, Integer::sum);
            });
        });

        // 统计出豫数据
        outFlowMap.forEach((fromCityId, flows) -> {
            flows.forEach(flow -> {
                int cityId = isFromCity ? flow.getFromCityId() : flow.getToCityId();
                countMap.merge(cityId, 1, Integer::sum);
            });
        });

        return countMap;
    }

    private List<TrafficFlowDTO> buildResult(
            Map<Integer, List<InToHenan>> inFlowMap,
            Map<Integer, List<OutOfHenan>> outFlowMap,
            Map<Integer, City> cityMap,
            Map<Integer, Integer> fromCityCount,
            Map<Integer, Integer> toCityCount,
            int maxFromCount,
            int maxToCount) {

        List<TrafficFlowDTO> result = new ArrayList<>();
        int id = 1;

        // 添加入豫数据
        for (Map.Entry<Integer, List<InToHenan>> entry : inFlowMap.entrySet()) {
            Integer fromCityId = entry.getKey();
            List<InToHenan> flows = entry.getValue();

            for (InToHenan flow : flows) {
                City fromCity = cityMap.get(flow.getFromCityId());
                City toCity = cityMap.get(flow.getToCityId());

                if (fromCity != null && toCity != null) {
                    TrafficFlowDTO dto = new TrafficFlowDTO();
                    dto.setId(id++);
                    dto.setFromCity(fromCity.getCityName());
                    dto.setFromLongitude(fromCity.getLongitude());
                    dto.setFromLatitude(fromCity.getLatitude());
                    dto.setFromWeight(5);
                    dto.setFromSymbolSize(calculateSymbolSize(
                            fromCityCount.getOrDefault(fromCity.getCityId(), 1),
                            maxFromCount));

                    dto.setToCity(toCity.getCityName());
                    dto.setToLongitude(toCity.getLongitude());
                    dto.setToLatitude(toCity.getLatitude());
                    dto.setToWeight(5);
                    dto.setToSymbolSize(calculateSymbolSize(
                            toCityCount.getOrDefault(toCity.getCityId(), 1),
                            maxToCount));

                    result.add(dto);
                }
            }
        }

        // 添加出豫数据
        for (Map.Entry<Integer, List<OutOfHenan>> entry : outFlowMap.entrySet()) {
            Integer fromCityId = entry.getKey();
            List<OutOfHenan> flows = entry.getValue();

            for (OutOfHenan flow : flows) {
                City fromCity = cityMap.get(flow.getFromCityId());
                City toCity = cityMap.get(flow.getToCityId());

                if (fromCity != null && toCity != null) {
                    TrafficFlowDTO dto = new TrafficFlowDTO();
                    dto.setId(id++);
                    dto.setFromCity(fromCity.getCityName());
                    dto.setFromLongitude(fromCity.getLongitude());
                    dto.setFromLatitude(fromCity.getLatitude());
                    dto.setFromWeight(5);
                    dto.setFromSymbolSize(calculateSymbolSize(
                            fromCityCount.getOrDefault(fromCity.getCityId(), 1),
                            maxFromCount));

                    dto.setToCity(toCity.getCityName());
                    dto.setToLongitude(toCity.getLongitude());
                    dto.setToLatitude(toCity.getLatitude());
                    dto.setToWeight(5);
                    dto.setToSymbolSize(calculateSymbolSize(
                            toCityCount.getOrDefault(toCity.getCityId(), 1),
                            maxToCount));

                    result.add(dto);
                }
            }
        }

        return result;
    }

    private int calculateSymbolSize(int count, int maxCount) {
        if (maxCount == 0) return 5;
        // 基础大小4，最大10，按比例计算
        return 4 + (int)(6 * ((double)count / maxCount));
    }
}
