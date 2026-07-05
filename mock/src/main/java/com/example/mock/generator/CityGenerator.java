package com.example.mock.generator;

import com.example.mock.generator.DataGenerator;

import com.example.mock.mockbean.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityGenerator extends DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(CityGenerator.class);
    private static final String INSERT_SQL =
            "INSERT IGNORE INTO city (city_name, province, latitude, longitude) VALUES (?, ?, ?, ?)";

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

    // 预定义的周边省份及城市数据（包含30个精选外省城市）
    private static final Map<String, Map<String, double[]>> SURROUNDING_PROVINCES = new HashMap<>();
    static {
        // 河北省（5个城市）
        Map<String, double[]> hebeiCities = new HashMap<>();
        hebeiCities.put("石家庄", new double[]{38.0373, 114.5149});
        hebeiCities.put("邯郸", new double[]{36.6182, 114.4917});
        hebeiCities.put("唐山", new double[]{39.6309, 118.1802});
        hebeiCities.put("秦皇岛", new double[]{39.9354, 119.6005});
        hebeiCities.put("廊坊", new double[]{39.5380, 116.6838});

        // 山东省（5个城市）
        Map<String, double[]> shandongCities = new HashMap<>();
        shandongCities.put("济南", new double[]{36.6512, 117.1200});
        shandongCities.put("青岛", new double[]{36.0671, 120.3826});
        shandongCities.put("烟台", new double[]{37.4638, 121.4479});
        shandongCities.put("威海", new double[]{37.5131, 122.1204});
        shandongCities.put("潍坊", new double[]{36.7068, 119.1618});

        // 安徽省（2个城市）
        Map<String, double[]> anhuiCities = new HashMap<>();
        anhuiCities.put("合肥", new double[]{31.8611, 117.2856});
        anhuiCities.put("阜阳", new double[]{32.8996, 115.8173});

        // 湖北省（2个城市）
        Map<String, double[]> hubeiCities = new HashMap<>();
        hubeiCities.put("武汉", new double[]{30.5928, 114.3055});
        hubeiCities.put("襄阳", new double[]{32.0420, 112.1350});

        // 陕西省（2个城市）
        Map<String, double[]> shaanxiCities = new HashMap<>();
        shaanxiCities.put("西安", new double[]{34.3416, 108.9398});
        shaanxiCities.put("渭南", new double[]{34.5090, 109.5090});

        // 江苏省（3个城市）
        Map<String, double[]> jiangsuCities = new HashMap<>();
        jiangsuCities.put("南京", new double[]{32.0603, 118.7969});
        jiangsuCities.put("苏州", new double[]{31.2989, 120.5853});
        jiangsuCities.put("无锡", new double[]{31.4912, 120.3119});

        // 浙江省（2个城市）
        Map<String, double[]> zhejiangCities = new HashMap<>();
        zhejiangCities.put("杭州", new double[]{30.2741, 120.1551});
        zhejiangCities.put("宁波", new double[]{29.8746, 121.5504});

        // 广东省（2个城市）
        Map<String, double[]> guangdongCities = new HashMap<>();
        guangdongCities.put("广州", new double[]{23.1292, 113.2644});
        guangdongCities.put("深圳", new double[]{22.5431, 114.0579});

        // 辽宁省（2个城市）
        Map<String, double[]> liaoningCities = new HashMap<>();
        liaoningCities.put("沈阳", new double[]{41.8057, 123.4315});
        liaoningCities.put("大连", new double[]{38.9140, 121.6147});

        // 四川省（1个城市）
        Map<String, double[]> sichuanCities = new HashMap<>();
        sichuanCities.put("成都", new double[]{30.5723, 104.0665});

        // 山西省（2个城市）
        Map<String, double[]> shanxiCities = new HashMap<>();
        shanxiCities.put("太原", new double[]{37.8706, 112.5489});
        shanxiCities.put("大同", new double[]{40.0768, 113.3001});

        // 湖南省（2个城市）
        Map<String, double[]> hunanCities = new HashMap<>();
        hunanCities.put("长沙", new double[]{28.2282, 112.9388});
        hunanCities.put("岳阳", new double[]{29.3571, 113.1292});

        // 添加到省份映射
        SURROUNDING_PROVINCES.put("河北", hebeiCities);
        SURROUNDING_PROVINCES.put("山东", shandongCities);
        SURROUNDING_PROVINCES.put("安徽", anhuiCities);
        SURROUNDING_PROVINCES.put("湖北", hubeiCities);
        SURROUNDING_PROVINCES.put("陕西", shaanxiCities);
        SURROUNDING_PROVINCES.put("江苏", jiangsuCities);
        SURROUNDING_PROVINCES.put("浙江", zhejiangCities);
        SURROUNDING_PROVINCES.put("广东", guangdongCities);
        SURROUNDING_PROVINCES.put("辽宁", liaoningCities);
        SURROUNDING_PROVINCES.put("四川", sichuanCities);
        SURROUNDING_PROVINCES.put("山西", shanxiCities);
        SURROUNDING_PROVINCES.put("湖南", hunanCities);
    }

    private List<City> cities = new ArrayList<>();

    @Override
    public void generate(int count) throws SQLException {
        cities.clear();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // 插入河南省地级市
            for (Map.Entry<String, double[]> entry : HENAN_CITIES.entrySet()) {
                String cityName = entry.getKey();
                double[] coords = entry.getValue();

                pstmt.setString(1, cityName);
                pstmt.setString(2, "河南");
                pstmt.setDouble(3, coords[0]);
                pstmt.setDouble(4, coords[1]);
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int cityId = rs.getInt(1);
                        City city = City.builder()
                                .cityId(cityId)
                                .cityName(cityName)
                                .province("河南")
                                .latitude(coords[0])
                                .longitude(coords[1])
                                .build();
                        cities.add(city);
                        log.debug("添加河南城市: {}", cityName);
                    }
                }
            }

            // 插入周边省份城市（共30个）
            int added = 0;
            for (Map.Entry<String, Map<String, double[]>> provinceEntry : SURROUNDING_PROVINCES.entrySet()) {
                String province = provinceEntry.getKey();
                Map<String, double[]> citiesInProvince = provinceEntry.getValue();

                for (Map.Entry<String, double[]> cityEntry : citiesInProvince.entrySet()) {
                    if (added >= 30) break;

                    String cityName = cityEntry.getKey();
                    double[] coords = cityEntry.getValue();

                    pstmt.setString(1, cityName);
                    pstmt.setString(2, province);
                    pstmt.setDouble(3, coords[0]);
                    pstmt.setDouble(4, coords[1]);
                    pstmt.executeUpdate();

                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int cityId = rs.getInt(1);
                            City city = City.builder()
                                    .cityId(cityId)
                                    .cityName(cityName)
                                    .province(province)
                                    .latitude(coords[0])
                                    .longitude(coords[1])
                                    .build();
                            cities.add(city);
                            added++;
                            log.debug("添加外省城市: {} ({}), 已添加 {}/30", cityName, province, added);
                        }
                    }
                }
            }

            log.info("成功生成 {} 个城市数据 (河南城市: {}, 外省城市: {})",
                    cities.size(), HENAN_CITIES.size(), added);
        }
        
        // 从数据库加载所有城市（包括已存在的）
        loadCitiesFromDatabase();
    }
    
    /**
     * 从数据库加载所有城市数据
     */
    private void loadCitiesFromDatabase() throws SQLException {
        cities.clear();
        String sql = "SELECT city_id, city_name, province, latitude, longitude FROM city";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                City city = City.builder()
                        .cityId(rs.getInt("city_id"))
                        .cityName(rs.getString("city_name"))
                        .province(rs.getString("province"))
                        .latitude(rs.getDouble("latitude"))
                        .longitude(rs.getDouble("longitude"))
                        .build();
                cities.add(city);
            }
        }
        log.info("从数据库加载了 {} 个城市 (河南: {}, 外省: {})", 
                cities.size(), getHenanCities().size(), getNonHenanCities().size());
    }

    public List<City> getCities() {
        return Collections.unmodifiableList(cities);
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

    private int executeInsertAndGetId(String sql, String cityName, String province,
                                      double latitude, double longitude) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cityName);
            pstmt.setString(2, province);
            pstmt.setDouble(3, latitude);
            pstmt.setDouble(4, longitude);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("无法获取生成的ID");
    }
}