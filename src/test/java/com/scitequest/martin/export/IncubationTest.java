package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.time.Duration;

import javax.json.Json;
import javax.json.JsonReader;

import org.junit.Test;

public class IncubationTest {

    @Test
    public void testEquals() {
        Incubation i1 = Incubation.of("Potassium",
                Quantity.fromPercent(100.0), Quantity.fromMicroMolPerLitre(20.0),
                Duration.ofSeconds(15));
        Incubation i2 = Incubation.of("Potassium",
                Quantity.fromPercent(100.0), Quantity.fromMicroMolPerLitre(20.0),
                Duration.ofSeconds(15));
        Incubation i3 = Incubation.of("Potassium",
                Quantity.fromPercent(100.0), Quantity.fromMicroMolPerLitre(5.8),
                Duration.ofSeconds(15));

        assertTrue(i1.equals(i2));
        assertEquals(i1.hashCode(), i2.hashCode());

        assertFalse(i1.equals(i3));
        assertNotEquals(i1.hashCode(), i3.hashCode());
        assertFalse(i2.equals(i3));
        assertNotEquals(i2.hashCode(), i3.hashCode());
    }

    @Test
    public void testFromJson() {
        String json = "{"
                + "\"solution\": \"Potassium\", "
                + "\"stock_concentration\": { \"value\": 1.1, \"unit\": \"percent\" }, "
                + "\"final_concentration\": { \"value\": 1.8, \"unit\": \"nano_mol_per_litre\" }, "
                + "\"incubation_time\": \"PT6S\""
                + "}";
        JsonReader reader = Json.createReader(new StringReader(json));
        Incubation actual = Incubation.fromJson(reader.readObject());
        Incubation expected = Incubation.of("Potassium",
                Quantity.fromPercent(1.1), Quantity.fromNanoMolPerLitre(1.8),
                Duration.ofSeconds(6));
        assertEquals(expected, actual);
    }
}
