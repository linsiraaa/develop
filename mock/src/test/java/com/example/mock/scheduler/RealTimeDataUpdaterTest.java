package com.example.mock.scheduler;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RealTimeDataUpdaterTest {
    @Test
    void selectsExistingLatestRoadConditionInsteadOfAssumingIdOne() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet rows = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT rc.rc_id FROM road_condition rc JOIN time_dim t ON rc.time_id=t.time_id ORDER BY t.year DESC, t.month DESC, rc.rc_id DESC LIMIT 1"))
                .thenReturn(query);
        when(query.executeQuery()).thenReturn(rows);
        when(rows.next()).thenReturn(true);
        when(rows.getInt("rc_id")).thenReturn(35);

        assertEquals(35, RealTimeDataUpdater.latestRoadConditionId(connection));
        verify(query).executeQuery();
    }
}
