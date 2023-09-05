package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DatapointStatisticsTest {
    @Test
    public void testEquals() {
        // Analogous to DatapointTest
        DatapointStatistics m1 = DatapointStatistics.of(0, 0, 100.0, 0.5, 2, 0.1, 0.05, 0.0001);
        DatapointStatistics m2 = DatapointStatistics.of(0, 0, 100.0, 0.5, 2, 0.1, 0.05, 0.0001);
        DatapointStatistics m3 = DatapointStatistics.of(1, 2, 50.0, 0.7, 5, 0.2, 0.05, 0.0001);

        assertTrue(m1.equals(m2));
        assertEquals(m1.hashCode(), m2.hashCode());
        assertFalse(m1.equals(m3));
        assertNotEquals(m1.hashCode(), m3.hashCode());
        assertFalse(m2.equals(m3));
        assertNotEquals(m2.hashCode(), m3.hashCode());
    }
}
