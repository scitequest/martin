package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MeasurePointTest {

    @Test
    public void testEquals() {
        Measurepoint m1 = Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3);
        Measurepoint m2 = Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3);
        Measurepoint m3 = Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.1);

        assertTrue(m1.equals(m2));
        assertEquals(m1.hashCode(), m2.hashCode());

        assertFalse(m1.equals(m3));
        assertNotEquals(m1.hashCode(), m3.hashCode());
        assertFalse(m2.equals(m3));
        assertNotEquals(m2.hashCode(), m3.hashCode());
    }
}
