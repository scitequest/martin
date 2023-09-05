package com.scitequest.martin;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scitequest.martin.Point.PointDeserializer;
import com.scitequest.martin.Point.PointSerializer;

/**
 * Objects of this class contain the basic information of any point on a
 * coordinate system, namely the x and y position of said point.
 */
@JsonSerialize(using = PointSerializer.class)
@JsonDeserialize(using = PointDeserializer.class)
public final class Point {
    /**
     * The x-coordinate of a point.
     */
    public final double x;
    /**
     * The y-coordinate of a point.
     */
    public final double y;

    /**
     * Creates a point with x and y coordinates. This point is final and can never
     * move, changing coordinates occurs by creating a new point object.
     *
     * @param x the x-position of our point.
     * @param y the y-position of our point.
     */
    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @JsonCreator
    public static Point of(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        return new Point(x, y);
    }

    public Point getCopy() {
        return new Point(x, y);
    }

    /**
     * Calculates the midpoint on an imaginary line between this and the given
     * point.
     *
     * @param other the other point
     * @return the midpoint
     */
    public Point midpoint(Point other) {
        double avgX = (this.x + other.x) / 2;
        double avgY = (this.y + other.y) / 2;

        return Point.of(avgX, avgY);
    }

    /**
     * Calculate the distance to the other point.
     *
     * @param other the point to calculate the distance to
     * @return the distance
     */
    public double distanceTo(Point other) {
        double deltaX = this.x - other.x;
        double deltaY = this.y - other.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Point other = (Point) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }

    public static final class PointSerializer extends JsonSerializer<Point> {

        @Override
        public void serialize(Point point,
                JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeNumber(point.x);
            jsonGenerator.writeNumber(point.y);
            jsonGenerator.writeEndArray();
        }
    }

    public static final class PointDeserializer extends JsonDeserializer<Point> {

        @Override
        public Point deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            double[] values = jsonParser.readValueAs(double[].class);
            if (values.length != 2) {
                throw new IOException("Invalid array length: " + values.length);
            }
            return Point.of(values[0], values[1]);
        }
    }
}
