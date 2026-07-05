package com.example.one.Service;

import com.example.one.pojo.TollStationData;
import com.example.one.repository.TollStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TollStationService {

    @Autowired
    private TollStationRepository tollStationRepository;

    public Map<String, Object> getTollStationRanking(String city) {
        Map<String, Object> result = new HashMap<>();

        List<TollStationData> topStations = tollStationRepository.findTop10ByCity(city);

        List<String> stationNames = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();

        for (TollStationData station : topStations) {
            stationNames.add(station.getStationName());
            revenues.add(station.getDailyRevenue());
        }

        result.put("stationNames", stationNames);
        result.put("revenues", revenues);

        return result;
    }

    public Map<String, Object> getTollStationComparison() {
        Map<String, Object> result = new HashMap<>();

        List<String> cities = Arrays.asList("郑州", "洛阳", "开封", "信阳", "南阳");
        List<Double> passengerRevenues = new ArrayList<>();
        List<Double> freightRevenues = new ArrayList<>();

        for (String city : cities) {
            List<TollStationData> stations = tollStationRepository.findByCityOrderByDailyRevenueDesc(city);
            if (!stations.isEmpty()) {
                // 计算该城市客运收入总和
                double passengerSum = stations.stream()
                        .mapToDouble(s -> s.getPassengerRevenue().doubleValue())
                        .sum();
                passengerRevenues.add(passengerSum);

                // 计算该城市货运收入总和
                double freightSum = stations.stream()
                        .mapToDouble(s -> s.getFreightRevenue().doubleValue())
                        .sum();
                freightRevenues.add(freightSum);
            } else {
                passengerRevenues.add(0.0);
                freightRevenues.add(0.0);
            }
        }

        result.put("cities", cities);
        result.put("passengerRevenues", passengerRevenues);
        result.put("freightRevenues", freightRevenues);

        return result;
    }
}