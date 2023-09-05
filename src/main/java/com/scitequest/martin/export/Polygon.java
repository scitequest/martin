package com.scitequest.martin.export;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scitequest.martin.Point;

public class Polygon implements Geometry {
    public final List<Point> coordinates;

    private Polygon(List<Point> coordinates) {
        this.coordinates = coordinates;
    }

    @JsonCreator
    public static Polygon ofPolygon(@JsonProperty("coordinates") List<Point> coordinates) {
        return new Polygon(Collections.unmodifiableList(coordinates));
    }

    public static Polygon ofRectangle(Point p0, Point p1, Point p2, Point p3) {
        List<Point> coords = List.of(p0, p1, p2, p3);
        return new Polygon(coords);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
        return result;
    }

    @SuppressWarnings("checkstyle:NeedBraces")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Polygon other = (Polygon) obj;
        if (coordinates == null) {
            if (other.coordinates != null)
                return false;
        } else if (!coordinates.equals(other.coordinates))
            return false;
        return true;
    }
}
