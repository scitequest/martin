package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scitequest.martin.Const;
import com.scitequest.martin.Point;

public class PolygonTest {

    @Test
    public void testCreation() {
        Polygon rect = Polygon.ofRectangle(
                Point.of(0.0, 1.0), Point.of(2.0, 3.0),
                Point.of(4.0, 5.0), Point.of(6.0, 7.0));
        List<Point> coords = rect.coordinates;
        assertEquals(0.0, coords.get(0).x, 0.0001);
        assertEquals(1.0, coords.get(0).y, 0.0001);
        assertEquals(2.0, coords.get(1).x, 0.0001);
        assertEquals(3.0, coords.get(1).y, 0.0001);
        assertEquals(4.0, coords.get(2).x, 0.0001);
        assertEquals(5.0, coords.get(2).y, 0.0001);
        assertEquals(6.0, coords.get(3).x, 0.0001);
        assertEquals(7.0, coords.get(3).y, 0.0001);
    }

    @Test
    public void testCoordinatesImmutable() {
        Polygon rect = Polygon.ofRectangle(
                Point.of(0.0, 1.0), Point.of(2.0, 3.0),
                Point.of(4.0, 5.0), Point.of(6.0, 7.0));
        assertThrows(UnsupportedOperationException.class,
                () -> rect.coordinates.clear());
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        Polygon rect = Polygon.ofRectangle(
                Point.of(0.0, 1.0), Point.of(2.0, 3.0),
                Point.of(4.0, 5.0), Point.of(6.0, 7.0));
        assertEquals("{\"type\":\"POLYGON\",\"coordinates\":[[0.0,1.0],[2.0,3.0],[4.0,5.0],[6.0,7.0]]}",
                Const.mapper.writeValueAsString(rect));
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        String json = "{\"type\":\"POLYGON\",\"coordinates\":[[0.0,1.0],[2.0,3.0],[4.0,5.0],[6.0,7.0]]}";
        Polygon expectedRectangle = Polygon.ofRectangle(
                Point.of(0.0, 1.0), Point.of(2.0, 3.0),
                Point.of(4.0, 5.0), Point.of(6.0, 7.0));
        Polygon actualRectangle = Const.mapper.readValue(json, Polygon.class);
        assertEquals(expectedRectangle, actualRectangle);
    }
}
