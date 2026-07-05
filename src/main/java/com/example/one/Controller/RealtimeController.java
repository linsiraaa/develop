package com.example.one.Controller;

import com.example.one.Service.RealtimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/realtime")
public class RealtimeController {

    @Autowired
    private RealtimeDataService realtimeDataService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getRealtimeDashboard() {
        return ResponseEntity.ok(realtimeDataService.getRealtimeDashboard());
    }

    @GetMapping("/toll-rank")
    public ResponseEntity<?> getTollRank(@RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(realtimeDataService.getTollRank(topN));
    }

    @GetMapping("/vehicle-stats")
    public ResponseEntity<?> getRealtimeVehicleStats() {
        return ResponseEntity.ok(realtimeDataService.getRealtimeVehicleStats());
    }

    @GetMapping("/passenger-freight")
    public ResponseEntity<?> getPassengerFreight() {
        return ResponseEntity.ok(realtimeDataService.getPassengerFreight());
    }

    @GetMapping("/in-to-henan")
    public ResponseEntity<?> getInToHenan() {
        return ResponseEntity.ok(realtimeDataService.getInToHenan());
    }

    @GetMapping("/out-of-henan")
    public ResponseEntity<?> getOutOfHenan() {
        return ResponseEntity.ok(realtimeDataService.getOutOfHenan());
    }

    @GetMapping("/status")
    public ResponseEntity<?> getFlinkStatus() {
        return ResponseEntity.ok(realtimeDataService.isFlinkDataAvailable());
    }

    @GetMapping("/traffic-flow")
    public ResponseEntity<?> getTrafficFlow() {
        return ResponseEntity.ok(realtimeDataService.getTrafficFlow());
    }

    @GetMapping("/vehicle-traffic")
    public ResponseEntity<?> getVehicleTraffic() {
        return ResponseEntity.ok(realtimeDataService.getVehicleTraffic());
    }

    @GetMapping("/traffic-violation")
    public ResponseEntity<?> getTrafficViolation() {
        return ResponseEntity.ok(realtimeDataService.getTrafficViolation());
    }
}
