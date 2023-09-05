package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DatapointTest {
    @Test
    public void testEquals() {
        Measurepoint m1 = Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3);
        Measurepoint m2 = Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3);
        Measurepoint m3 = Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.1);

        Datapoint d1 = Datapoint.of(m1, 10, 1);
        Datapoint d2 = Datapoint.of(m1, 10, 1);
        Datapoint d3 = Datapoint.of(m2, 10, 1);
        Datapoint d4 = Datapoint.of(m3, 5, 0.5);

        assertTrue(d1.equals(d2));
        assertEquals(d1.hashCode(), d2.hashCode());

        assertTrue(d1.equals(d3));
        assertEquals(d1.hashCode(), d3.hashCode());

        assertTrue(d3.equals(d2));
        assertEquals(d3.hashCode(), d2.hashCode());

        assertFalse(d1.equals(d4));
        assertNotEquals(d1.hashCode(), d4.hashCode());

        assertFalse(d2.equals(d4));
        assertNotEquals(d2.hashCode(), d4.hashCode());

        assertFalse(d3.equals(d4));
        assertNotEquals(d3.hashCode(), d4.hashCode());
    }
}
