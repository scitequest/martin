package com.scitequest.martin;

/**
 * This class defines coordinates as points with an angle relative to a
 * rotational center. In contrast to objects of Point coordinates can "move"
 * linearly and "rotate" around their respective rotational center.
 */
public class Coordinates {
    /**
     * Object that contains the x and y position of a coordinate. Moving the
     * coordinate means generating a new instance of Point.
     */
    private Point point;
    /**
     * The current angle of our coordinate relative to a vector pointing straigth
     * down from rCenter. Angles are stored in degrees, negative values are
     * converted to positive ones (for example -1 to 359).
     */
    private double angle; // 0° is pointed downwards -> 1° to the right, 359° to the left
    /**
     * Radius is the distance between point and rCenter.
     */
    private double radius;

    /**
     * Creates a placeholder coordinate without information about its x or y
     * coordinates (point).
     */
    Coordinates() {
        this.point = Point.of(0, 0);
        this.angle = 0;
        this.radius = 0;
    }

    /**
     * Creates an instance of Coordinate. Any coordinate can be described by either:
     * - Their x and y position (contained in point) on a coordinate system or -
     * Their distance and angle relative to any other point (rCenter). Instances of
     * coordinate use both these definitions to allow linear and angular movement.
     *
     * @param point x and y values of our coordinate.
     */
    Coordinates(Point point) {
        this.point = point;
        this.angle = 0;
        this.radius = 0;
    }

    /**
     * This version of the constructor allows for the creation of a deep copy.
     * Normally angle and radius are calculated values that are not set manually.
     *
     * @param point  x and y values of our coordinate.
     * @param angle  the angle between our coordinate and the rotational center of
     *               its corresponding slide.
     * @param radius the distance between our coordinate and the rotational center
     *               of its corresponding slide.
     */
    Coordinates(Point point, double angle, double radius) {
        this.point = point;
        this.angle = angle;
        this.radius = radius;
    }

    /**
     * Given the position of two Points (point and rCenter), this method calculates
     * their distance (radius) to one another, as well as the relative angle between
     * a vector straight down from rCenter and point.
     *
     * @param rCenter the x and y position of the center of our slide.
     */
    public void calculateOrbit(Point rCenter) {
        double a = point.x - rCenter.x; // adjacent side of triangle
        double b = point.y - rCenter.y; // opposite side of triangle

        // a² + b² = c² -> distance from rotational center to this point
        radius = Math.sqrt(a * a + b * b);
        angle = (360.0 + Math.toDegrees(Math.atan2(a, b))) % 360.0;
    }

    /**
     * Provides a deep copy of a Coordinate object.
     *
     * @return a new instance of Coordinates containing the same values as the
     *         instance the method is called from.
     */
    public Coordinates deepCopy() {
        return new Coordinates(Point.of(point.x, point.y), angle, radius);
    }

    // Not sure which getters will be the most useful, may delete the useless ones
    // later on.

    /**
     * @return x-value of this coordinate.
     */
    public double getX() {
        return point.x;
    }

    /**
     * @return y-value of this coordinate.
     */
    public double getY() {
        return point.y;
    }

    /**
     * @return the x and y-values of this coordinate as a Point object.
     */
    public Point getPoint() {
        return point;
    }

    /**
     * @return angle between this coordinate and its center.
     */
    public double getAngle() {
        return angle;
    }

    /*
     * setX and setY should only ever be used if the rotational center is changed as
     * well - not sure if we set x or y separately at any point, just put this in to
     * be sure we can delete this if we don't need it
     */
    /**
     * Sets the x-value of our coordinate without changing y.
     *
     * @param x new x-value for our coordinate.
     */
    public void setX(double x) {
        this.point = Point.of(x, point.y);
    }

    /**
     * Sets the y-value of our coordinate without changing x.
     *
     * @param y new y-value for our coordinate.
     */
    public void setY(double y) {
        this.point = Point.of(point.x, y);
    }

    /**
     * Changes x and y by creating a new instance of point. Both angle and distance
     * have to be calculated anew because of this.
     *
     * @param x x-value of the new point.
     * @param y y-value of the new point.
     */
    public void setPoint(double x, double y) {
        this.point = Point.of(x, y);
    }

    /**
     * Changes the point of the coordinate (x and y values) Both angle and distance
     * have to be calculated anew because of this.
     *
     * @param p a point containing x and y values.
     */
    public void setPoint(Point p) {
        this.point = p;
    }

    /**
     * This method changes the angle of our coordinate to another positive value.
     * Both x and y values are then calculated via the new angle and the unchanged
     * radius, a new instance of Point is created as a result of this.
     *
     * @param newAngle rotational change in degrees.
     * @param rCenter  the x and y position of the center of our slide.
     */
    public void setAngle(double newAngle, Point rCenter) {
        this.angle = (360.0 + newAngle) % 360.0; // handles both angles > 360 and < 0

        // Any change to the angle changes x and y while the radius remains constant
        double x = rCenter.x + Math.sin(Math.toRadians(angle)) * radius;
        double y = rCenter.y + Math.cos(Math.toRadians(angle)) * radius;
        this.point = Point.of(x, y);

    }

    /**
     * Shows the content of all attributes (except rCenter) of its respective
     * objects. This method exists for testing purposes.
     */
    @Override
    public String toString() {
        return "Coordinate [x=" + point.x + ", y=" + point.y
                + ", angle=" + angle + ", radius=" + radius + "]";
    }

}
