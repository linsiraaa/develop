package com.example.one.Controller;

import com.example.one.pojo.HighwayToll;
import com.example.one.Service.HighwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/highway")
public class HighwayController {

    @Autowired
    private HighwayService highwayService;

    // 获取收费金额最高的5条记录
    @GetMapping("/top5/toll")
    public List<HighwayToll> getTop5ByTollAmount() {
        return highwayService.getTop5ByTollAmount();
    }


}