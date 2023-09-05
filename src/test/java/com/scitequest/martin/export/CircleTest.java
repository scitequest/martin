package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scitequest.martin.Const;
import com.scitequest.martin.Point;

public class CircleTest {
    @Test
    public void testSerialization() throws JsonProcessingException {
        Circle circle = Circle.of(Point.of(0.0, 1.0), 14);
        assertEquals("{\"type\":\"CIRCLE\",\"position\":[0.0,1.0],\"diameter\":14}",
                Const.mapper.writeValueAsString(circle));
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        String json = "{\"type\":\"CIRCLE\",\"position\":[0.0,1.0],\"diameter\":14}";
        Circle circle = Circle.of(Point.of(0.0, 1.0), 14);
        assertEquals(Const.mapper.readValue(json, Circle.class), circle);
    }
}
