package com.example.one.Service;

import com.example.one.pojo.AdsTodayVehicleInfo;
import com.example.one.repository.AdsTodayVehicleInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleInfoService {
    @Autowired
    private AdsTodayVehicleInfoRepository repository;

    // 根据ID查询数据（返回Optional包装）
    public Optional<AdsTodayVehicleInfo> getVehicleDataById(int id) {
        return Optional.ofNullable(repository.findFirstById(id));
    }
}

    // 新增按统计周期查询方法

