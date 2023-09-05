package com.scitequest.martin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class VersionTest {

    @Test
    public void versionFromProperties() throws IOException {
        Version version = Version.fromProperties();
        assertNotNull(version);
        assertTrue(version.getMajor() > 0 || version.getMinor() > 0 || version.getPatch() > 0);
    }

    @Test
    public void versionToString() {
        Version version = Version.of(1, 2, 3);
        assertEquals("1.2.3", version.toString());
    }

    @Test
    public void versionFromString() {
        Version version = Version.of("  1.20.3");
        assertEquals(1, version.getMajor());
        assertEquals(20, version.getMinor());
        assertEquals(3, version.getPatch());
    }

    @Test
    public void negativeVersionNumbers() {
        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Version.of(-1, 0, 0);
            }
        });
        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Version.of("0.-1.0");
            }
        });
    }

    @Test
    public void versionEquals() {
        Version v1 = Version.of(1, 2, 3);
        Version v2 = Version.of(1, 2, 3);
        Version v3 = Version.of(1, 2, 4);
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
        assertNotEquals(v1.hashCode(), v3.hashCode());
        assertNotEquals(v2, v3);
        assertNotEquals(v2.hashCode(), v3.hashCode());
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, Version.of("0.1.0").compareTo(Version.of("0.1.0")));
        assertEquals(0, Version.of("1.1.0").compareTo(Version.of("1.1.0")));

        assertEquals(1, Version.of("1.1.1").compareTo(Version.of("1.1.0")));
        assertEquals(1, Version.of("1.2.0").compareTo(Version.of("1.1.0")));
        assertEquals(1, Version.of("2.1.0").compareTo(Version.of("1.1.0")));

        assertEquals(-1, Version.of("1.1.0").compareTo(Version.of("1.1.1")));
        assertEquals(-1, Version.of("1.1.0").compareTo(Version.of("1.2.0")));
        assertEquals(-1, Version.of("1.1.0").compareTo(Version.of("2.1.0")));
    }

    @Test
    public void testIsCompatible() {
        assertTrue(Version.of("0.1.0").isCompatible(Version.of("0.1.0")));
        assertTrue(Version.of("0.1.0").isCompatible(Version.of("0.1.3")));
        assertFalse(Version.of("0.1.3").isCompatible(Version.of("0.1.0")));
        assertFalse(Version.of("0.1.0").isCompatible(Version.of("0.2.0")));
        assertFalse(Version.of("0.1.2").isCompatible(Version.of("0.2.0")));

        assertTrue(Version.of("2.0.0").isCompatible(Version.of("2.0.1")));
        assertTrue(Version.of("2.0.0").isCompatible(Version.of("2.1.0")));
        assertTrue(Version.of("2.0.0").isCompatible(Version.of("2.1.1")));

        assertFalse(Version.of("2.1.1").isCompatible(Version.of("2.1.0")));
        assertFalse(Version.of("1.0.0").isCompatible(Version.of("2.0.0")));
    }
}
