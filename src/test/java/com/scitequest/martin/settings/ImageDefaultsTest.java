package com.scitequest.martin.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.time.Duration;

import javax.json.Json;
import javax.json.JsonReader;

import org.junit.Test;

public class ImageDefaultsTest {

    @Test
    public void testEquals() {
        ImageDefaults i1 = ImageDefaults.of("imager1", 2, Duration.ofSeconds(15));
        ImageDefaults i2 = ImageDefaults.of("imager1", 2, Duration.ofSeconds(15));
        ImageDefaults i3 = ImageDefaults.of("Other imager", 2, Duration.ofSeconds(15));

        assertTrue(i1.equals(i2));
        assertEquals(i1.hashCode(), i2.hashCode());

        assertFalse(i1.equals(i3));
        assertNotEquals(i1.hashCode(), i3.hashCode());
        assertFalse(i2.equals(i3));
        assertNotEquals(i2.hashCode(), i3.hashCode());
    }

    @Test
    public void testPixelBinningValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> ImageDefaults.of("imager", 0, Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class,
                () -> ImageDefaults.of("imager", -1, Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class,
                () -> ImageDefaults.of("imager", -100, Duration.ofSeconds(0)));
        assertEquals(1, ImageDefaults.of("imager", 1, Duration.ofSeconds(0)).getPixelBinning());
        assertEquals(5, ImageDefaults.of("imager", 5, Duration.ofSeconds(0)).getPixelBinning());
    }

    @Test
    public void testFromJson() {
        String json = "{\"imager\":"
                + " \"test imager\", \"pixel_binning\": 2,"
                + " \"exposure_time\": \"PT6S\"}";
        JsonReader reader = Json.createReader(new StringReader(json));
        ImageDefaults image = ImageDefaults.fromJson(reader.readObject());
        assertEquals(ImageDefaults.of("test imager", 2, Duration.ofSeconds(6)), image);
    }
}
