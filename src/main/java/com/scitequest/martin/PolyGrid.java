package com.scitequest.martin;

import com.scitequest.martin.settings.MaskSettings.MeasureShape;

/**
 * Objects of this class contain a grid of PolyShapes. The dimensions (rows and
 * cols) of said grid are fully customizable, a one by one grid would also be
 * possible.
 */
public final class PolyGrid {

    /**
     * A grid of polyShapes defined by its number of rows and columns.
     */
    private PolyShape[][] grid;
    /**
     * Number of columns of the grid.
     */
    private int columns;
    /**
     * Number of rows of the grid.
     */
    private int rows;
    /**
     * The absolute position of the upper left corner of the grid.
     */
    private Point pos;
    /**
     * The total width of the grid (not including insets).
     */
    private double width;
    /**
     * The total height of the grid (not including insets).
     */
    private double height;
    /**
     * Inset in x direction for all polyShapes.
     */
    private double xInset;
    /**
     * Inset in y direction for all polyShapes.
     */
    private double yInset;
    /**
     * Width of the shape contained within each cell of the polyGrid.
     */
    private double shapeWidth;
    /**
     * Width of the shape contained within each cell of the polyGrid.
     */
    private double shapeHeight;
    /**
     * The shape of the polygons within our grid.
     */
    private MeasureShape shape;
    /**
     * Number of corners in a rectangle.
     */
    public static final int RECTANGULAR = 4;
    /**
     * Number of coordinates needed to describe a straight line.
     */
    public static final int POINT = 1;
    /**
     * Standard value for polyGrids without an inset.
     */
    public static final double NO_INSET = 0;

    PolyGrid(int cols, int rows,
            MeasureShape shape, int level, Point startPos,
            double width, double height,
            double xInset, double yInset,
            double shapeWidth, double shapeHeight) {
        this.columns = cols;
        this.rows = rows;
        this.shape = shape;
        this.width = width;
        this.height = height;
        this.xInset = xInset;
        this.yInset = yInset;
        this.shapeWidth = shapeWidth;
        this.shapeHeight = shapeHeight;
        this.pos = startPos;
        this.grid = new PolyShape[rows][cols];

        double xPos;
        double yPos = pos.y;
        Point[] points = {};

        double gridSpaceX = width / cols;
        double gridSpaceY = height / rows;

        for (int row = 0; row < grid.length; row++) {
            xPos = pos.x;
            for (int col = 0; col < grid[row].length; col++) {
                if (shape == MeasureShape.RECTANGLE || shape == MeasureShape.CIRCLE) {
                    points = new Point[] {
                            // Upper left
                            Point.of(xPos + xInset, yPos + yInset),
                            // Upper right
                            Point.of(xPos + shapeWidth + xInset, yPos + yInset),
                            // Lower right
                            Point.of(xPos + shapeWidth + xInset, yPos + shapeHeight + yInset),
                            // Lower left
                            Point.of(xPos + xInset, yPos + shapeHeight + yInset)
                    };
                } else if (shape == MeasureShape.DIAMOND) {
                    /*
                     * The following points are instantiated from the top in clockwise fashion.
                     */
                    points = new Point[] {
                            Point.of(xPos + xInset + shapeWidth / 2, yPos + yInset),
                            Point.of(xPos + xInset + shapeWidth, yPos + yInset + shapeHeight / 2),
                            Point.of(xPos + xInset + shapeWidth / 2, yPos + yInset + shapeHeight),
                            Point.of(xPos + xInset, yPos + yInset + shapeHeight / 2)
                    };

                } else if (shape == MeasureShape.POINT) {
                    points = new Point[] { Point.of(xPos + xInset, yPos + yInset) };
                }
                grid[row][col] = new PolyShape(points, level, shape);
                xPos += gridSpaceX;
            }
            yPos += gridSpaceY;
        }
    }

    /**
     * This method allows us to move all elements of the Grid linearly. If all
     * elements of a slide are moved the same invoking calculateOrbits is
     * unnecessary.
     *
     * @param deltaX change of Position on the X-Axis.
     * @param deltaY change of Position on the Y-Axis.
     */
    public void moveGrid(double deltaX, double deltaY) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col].movePoly(deltaX, deltaY);
            }
        }
    }

    /**
     * This method invokes Coordinate.calculateOrbit() for all coordinates contained
     * within a grid.
     *
     * @param rCenter the rotational center of the slide.
     */
    public void calculateGridOrbits(Point rCenter) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col].calculateOrbits(rCenter);
            }
        }
    }

    /**
     * This method invokes Coordinate.setAngle() for all coordinates in all
     * PolyShapes of the Grid.
     *
     * @param angle   the change in angle.
     * @param rCenter the rotational center of the slide.
     */
    public void rotateGridElements(double angle, Point rCenter) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col].setAngles(angle, rCenter);
            }
        }
    }

    public double[][][] getGridCoordinates() {
        int nCorners = 0;
        if (shape == MeasureShape.RECTANGLE) {
            nCorners = 4;
        }
        if (shape == MeasureShape.CIRCLE) {
            nCorners = 1;
        }
        double[][][] ret = new double[rows * columns][nCorners][nCorners];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                ret[row * columns + col] = grid[row][col].getPolyCoordinates();

            }
        }
        return ret;
    }

    public Point[][] getCentroids() {
        Point[][] centroids = new Point[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                centroids[row][col] = grid[row][col].getShapeCenter();
            }
        }

        return centroids;
    }

    /**
     * This method enables all PolyShapes (including) a set index and disables all
     * shapes below the same threshold.
     *
     * @param endOfEnabled index of the last enabled cell.
     */
    public void setEnabledZone(int endOfEnabled) {
        int i = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (i <= endOfEnabled) {
                    grid[row][col].setEnabled(true);
                } else {
                    grid[row][col].setEnabled(false);

                }
                i++;
            }
        }

    }

    public PolyShape getGridElement(int row, int col) {
        return grid[row][col];
    }

    public void setGridElementCoordinates(int row, int col, Point[] pointCoordinates) {
        grid[row][col].setCoordinates(pointCoordinates);
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(double x, double y) {
        this.pos = Point.of(x, y);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Deprecated
    public PolyShape[][] getGrid() {
        return grid;
    }

    @Deprecated
    public void setGrid(PolyShape[][] grid) {
        this.grid = grid;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public double getxInset() {
        return xInset;
    }

    public void setxInset(double xInset) {
        this.xInset = xInset;
    }

    public double getyInset() {
        return yInset;
    }

    public void setyInset(double yInset) {
        this.yInset = yInset;
    }

    public MeasureShape getShape() {
        return shape;
    }

    public void setShape(MeasureShape shape) {
        this.shape = shape;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public double getShapeWidth() {
        return shapeWidth;
    }

    public void setShapeWidth(double shapeWidth) {
        this.shapeWidth = shapeWidth;
    }

    public double getShapeHeight() {
        return shapeHeight;
    }

    public void setShapeHeight(double shapeHeight) {
        this.shapeHeight = shapeHeight;
    }

}
