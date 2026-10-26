package com.example.mock.scheduler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimeIdMappingTest {
    @Test
    void mapsMockTimeIdsToDashboardsMonthIndex() {
        // two.time_dim: 2025-01..2025-12 -> time_id 40..29 in descending order.
        for (int month = 1; month <= 12; month++) {
            assertEquals(month - 1, TimeIdMapping.monthIndexOf(41 - month), "time_id " + (41 - month));
        }
    }

    @Test
    void rejectsUnmappedIdsInsteadOfWritingToLatestMonth() {
        assertNull(TimeIdMapping.monthIndexOf(1));
        assertNull(TimeIdMapping.monthIndexOf(53));
    }
}
