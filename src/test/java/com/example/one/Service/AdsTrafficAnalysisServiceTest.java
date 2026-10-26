package com.example.one.Service;

import com.example.one.pojo.AdsTrafficFlowAnalysis;
import com.example.one.repository.AdsTrafficFlowAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdsTrafficAnalysisServiceTest {
    @Test
    void missingComparisonValuesDoNotBreakTrafficStatistics() {
        AdsTrafficFlowAnalysis row = new AdsTrafficFlowAnalysis();
        row.setId(1L);
        row.setYear(2025);
        row.setMonth(1);
        row.setTypeName("高速公路");
        row.setCurrentFlow(1000L);
        // The existing ADS records can have NULL comparison values.
        row.setLastYearFlow(null);
        row.setPrevMonthFlow(null);
        row.setYoyGrowthPercent(null);
        AdsTrafficFlowAnalysisRepository repository = mock(AdsTrafficFlowAnalysisRepository.class);
        when(repository.find2025Data()).thenReturn(Collections.singletonList(row));
        adsTrafficAnalysisService service = new adsTrafficAnalysisService();
        ReflectionTestUtils.setField(service, "repository", repository);

        Map<String, Object> result = service.get2025TrafficStatistics();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> data = (Map<String, Map<String, Double>>) result.get("data");
        assertEquals(1.0, data.get("2025-01-高速公路").get("current"));
        assertEquals(0.0, data.get("2025-01-高速公路").get("yoy"));
        assertEquals(0.0, data.get("2025-01-高速公路").get("mom"));
    }
}
