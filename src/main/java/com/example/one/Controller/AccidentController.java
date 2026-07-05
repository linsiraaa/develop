package com.example.one.Controller;

import com.example.one.Service.AccidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/accident")
public class AccidentController {

    @Autowired
    private AccidentService accidentService;

    @GetMapping("/stats")
    public Map<String, Object> getAccidentStats() {
        return accidentService.getAccidentData();
    }
}