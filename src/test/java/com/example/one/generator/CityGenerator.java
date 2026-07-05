package com.example.one.generator;

// CityGenerator.java

import com.example.one.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CityGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(CityGenerator.class);
    private static final String INSERT_SQL =
            "INSERT INTO city (city_name, province, latitude, longitude) VALUES (?, ?, ?, ?)";

    // 预定义的河南省地级市数据
    private static final Map<String, double[]> HENAN_CITIES = new HashMap<>();
    static {
        HENAN_CITIES.put("郑州", new double[]{34.7466, 113.6253});
        HENAN_CITIES.put("开封", new double[]{34.7978, 114.3529});
        HENAN_CITIES.put("洛阳", new double[]{34.6604, 112.4227});
        HENAN_CITIES.put("平顶山", new double[]{33.7328, 113.2949});
        HENAN_CITIES.put("安阳", new double[]{36.0618, 114.3591});
        HENAN_CITIES.put("鹤壁", new double[]{35.7495, 114.1742});
        HENAN_CITIES.put("新乡", new double[]{35.3039, 113.8803});
        HENAN_CITIES.put("焦作", new double[]{35.2339, 113.2466});
        HENAN_CITIES.put("濮阳", new double[]{35.7684, 115.0518});
        HENAN_CITIES.put("许昌", new double[]{34.0212, 113.8352});
        HENAN_CITIES.put("漯河", new double[]{33.5732, 114.0222});
        HENAN_CITIES.put("三门峡", new double[]{34.7771, 111.1906});
        HENAN_CITIES.put("南阳", new double[]{33.0156, 112.5322});
        HENAN_CITIES.put("商丘", new double[]{34.4367, 115.6577});
        HENAN_CITIES.put("信阳", new double[]{32.1374, 114.0924});
        HENAN_CITIES.put("周口", new double[]{33.6207, 114.6393});
        HENAN_CITIES.put("驻马店", new double[]{32.9812, 114.0221});
        HENAN_CITIES.put("济源", new double[]{35.0696, 112.5884});
    }

    // 预定义的周边省份及城市数据
    private static final Map<String, Map<String, double[]>> SURROUNDING_PROVINCES = new HashMap<>();
    static {
        Map<String, double[]> hebeiCities = new HashMap<>();
        hebeiCities.put("石家庄", new double[]{38.0373, 114.5149});
        hebeiCities.put("邯郸", new double[]{36.6182, 114.4917});

        Map<String, double[]> shandongCities = new HashMap<>();
        shandongCities.put("济南", new double[]{36.6512, 117.1200});
        shandongCities.put("菏泽", new double[]{35.2400, 115.4800});

        Map<String, double[]> anhuiCities = new HashMap<>();
        anhuiCities.put("合肥", new double[]{31.8611, 117.2856});
        anhuiCities.put("阜阳", new double[]{32.8996, 115.8173});

        Map<String, double[]> hubeiCities = new HashMap<>();
        hubeiCities.put("武汉", new double[]{30.5928, 114.3055});
        hubeiCities.put("襄阳", new double[]{32.0420, 112.1350});

        Map<String, double[]> shaanxiCities = new HashMap<>();
        shaanxiCities.put("西安", new double[]{34.3416, 108.9398});
        shaanxiCities.put("渭南", new double[]{34.5090, 109.5090});

        SURROUNDING_PROVINCES.put("河北", hebeiCities);
        SURROUNDING_PROVINCES.put("山东", shandongCities);
        SURROUNDING_PROVINCES.put("安徽", anhuiCities);
        SURROUNDING_PROVINCES.put("湖北", hubeiCities);
        SURROUNDING_PROVINCES.put("陕西", shaanxiCities);
    }

    private List<City> cities = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        cities.clear();

        try (Connection conn = getConnection()) {
            // 插入河南省地级市
            for (Map.Entry<String, double[]> entry : HENAN_CITIES.entrySet()) {
                String cityName = entry.getKey();
                double[] coords = entry.getValue();

                int cityId = executeInsertAndGetId(
                        INSERT_SQL, cityName, "河南", coords[0], coords[1]
                );

                City city = City.builder()
                        .cityId(cityId)
                        .cityName(cityName)
                        .province("河南")
                        .latitude(coords[0])
                        .longitude(coords[1])
                        .build();
                cities.add(city);
            }

            // 插入周边省份城市
            int surroundingCityCount = Math.min(count - HENAN_CITIES.size(), 20);
            int added = 0;

            for (Map.Entry<String, Map<String, double[]>> provinceEntry : SURROUNDING_PROVINCES.entrySet()) {
                String province = provinceEntry.getKey();
                Map<String, double[]> citiesInProvince = provinceEntry.getValue();

                for (Map.Entry<String, double[]> cityEntry : citiesInProvince.entrySet()) {
                    if (added >= surroundingCityCount) break;

                    String cityName = cityEntry.getKey();
                    double[] coords = cityEntry.getValue();

                    int cityId = executeInsertAndGetId(
                            INSERT_SQL, cityName, province, coords[0], coords[1]
                    );

                    City city = City.builder()
                            .cityId(cityId)
                            .cityName(cityName)
                            .province(province)
                            .latitude(coords[0])
                            .longitude(coords[1])
                            .build();
                    cities.add(city);

                    added++;
                }

                if (added >= surroundingCityCount) break;
            }

            log.info("成功生成 {} 个城市数据", cities.size());
        }
    }

    public List<City> getCities() {
        return cities;
    }

    public List<City> getHenanCities() {
        return cities.stream()
                .filter(city -> "河南".equals(city.getProvince()))
                .collect(Collectors.toList());
    }

    public List<City> getNonHenanCities() {
        return cities.stream()
                .filter(city -> !"河南".equals(city.getProvince()))
                .collect(Collectors.toList());
    }
}