package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.junit.Test;

public class ImageTest {

    @Test
    public void testEquals() {
        ZonedDateTime created = ZonedDateTime.now();
        Image i1 = Image.of(created, "imager1", 2, Duration.ofSeconds(15));
        Image i2 = Image.of(created, "imager1", 2, Duration.ofSeconds(15));
        Image i3 = Image.of(created, "Other imager", 2, Duration.ofSeconds(15));

        assertTrue(i1.equals(i2));
        assertEquals(i1.hashCode(), i2.hashCode());

        assertFalse(i1.equals(i3));
        assertNotEquals(i1.hashCode(), i3.hashCode());
        assertFalse(i2.equals(i3));
        assertNotEquals(i2.hashCode(), i3.hashCode());
    }

    @Test
    public void testPixelBinningValidation() {
        ZonedDateTime created = ZonedDateTime.now();
        assertThrows(IllegalArgumentException.class,
                () -> Image.of(created, "imager", 0, Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class,
                () -> Image.of(created, "imager", -1, Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class,
                () -> Image.of(created, "imager", -100, Duration.ofSeconds(0)));
        assertEquals(1, Image.of(created, "imager", 1, Duration.ofSeconds(0)).getPixelBinning());
        assertEquals(5, Image.of(created, "imager", 5, Duration.ofSeconds(0)).getPixelBinning());
    }
}
