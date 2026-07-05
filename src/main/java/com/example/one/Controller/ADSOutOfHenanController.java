package com.example.one.Controller;

import com.example.one.Service.ADSOutOfHenanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ads-out-of-henan")
public class ADSOutOfHenanController {

    @Autowired
    private ADSOutOfHenanService adsOutOfHenanService;

    @GetMapping("/all")
    public List<Map<String, Object>> getADSOutOfHenanWithWeights() {
        return adsOutOfHenanService.getADSOutOfHenanWithWeights();
    }
}
