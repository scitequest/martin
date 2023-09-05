package com.scitequest.martin;

import java.util.ArrayList;
import java.util.List;

import com.scitequest.martin.settings.MaskSettings.MeasureShape;

import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.Analyzer;
import ij.process.FloatPolygon;

public final class SearchArea {
    private final Control control;
    private final ImagePlus iPlus;
    private final PolyShape searchPerimeter;
    private final PolyShape measureField;
    private final double radius;
    private final double hWidth;
    private final double hHeight;
    private final double widthRatio;
    private final double heightRatio;

    private final Roi scanField;
    private final ArrayList<SearchField> searchFields = new ArrayList<SearchField>();
    private int rows;
    private int cols;

    private SearchArea(Control control, ImagePlus iPlus,
            PolyShape searchPerimeter, PolyShape measureField,
            double radius,
            double hWidth, double hHeight,
            double widthRatio, double heightRatio) {
        this.control = control;
        this.iPlus = iPlus;
        this.searchPerimeter = searchPerimeter;
        this.measureField = measureField;
        this.radius = radius;
        this.hWidth = hWidth;
        this.hHeight = hHeight;
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;

        /*
         * We generate the polygon once in a random spot, since it is moveable in its
         * entirety. After this we just shift its position for measurements.
         */
        if (measureField.getShape() == MeasureShape.CIRCLE) {
            this.scanField = new OvalRoi(0, 0, radius * 2, radius * 2);
        } else {
            FloatPolygon roiPoly = new FloatPolygon();
            double[][] measureElementCoords = measureField.getPolyCoordinates();
            for (int i = 0; i < measureElementCoords[0].length; i++) {
                roiPoly.addPoint(measureElementCoords[0][i], measureElementCoords[1][i]);
            }
            this.scanField = new PolygonRoi(roiPoly, Roi.POLYGON);
        }
        // tightPopulation();
        loosePopulation();
    }

    public static SearchArea of(Control control, ImagePlus iPlus, PolyShape searchPerimeter,
            PolyShape measureGridElement,
            double radius, double hWidth, double hHeight, double widthRatio, double heightRatio) {
        return new SearchArea(control, iPlus,
                searchPerimeter, measureGridElement,
                radius, hWidth, hHeight,
                widthRatio, heightRatio);
    }

    public double getMeasureFieldMean(boolean respectBounds, double x, double y) {
        // Prevents going out of bounds
        if (respectBounds && searchPerimeter.isClickInsidePoly(x, y) == null) {
            return -1;
        }
        scanField.setLocation(x - hWidth, y - hHeight);

        iPlus.setRoi(scanField);
        double mean = iPlus.getStatistics(Analyzer.getMeasurements()).mean;
        iPlus.killRoi();

        return mean;
    }

    private void loosePopulation() {
        int horizontalRatio = (int) widthRatio;
        int verticalRatio = (int) heightRatio;
        populatePerimeter(horizontalRatio, verticalRatio);
        interlinkFields();
    }

    private void roundedPopulation() {
        int horizontalRatio = (int) Math.round(widthRatio);
        int verticalRatio = (int) Math.round(heightRatio);
        populatePerimeter(horizontalRatio, verticalRatio);
        interlinkFields();
    }

    private void tightPopulation() {
        int horizontalRatio = (int) Math.ceil(widthRatio);
        int verticalRatio = (int) Math.ceil(heightRatio);
        populatePerimeter(horizontalRatio, verticalRatio);
        interlinkFields();
    }

    private void populatePerimeter(int horizontalDistanceRatio,
            int verticalDistanceRatio) {

        Point[] cPoints = searchPerimeter.getCornersAsPoints();
        // First populate a Vector (line of spotField-cell rectangle)
        ArrayList<Point> vectorFields = new ArrayList<Point>();
        vectorFields.addAll(populateVector(cPoints[0], cPoints[1], horizontalDistanceRatio));
        cols = vectorFields.size();
        // Then populate the opposite Vector (line of spotField-cell rectangle)
        ArrayList<Point> oppositeVectorFields = new ArrayList<Point>();
        oppositeVectorFields
                .addAll(populateVector(cPoints[3], cPoints[2], horizontalDistanceRatio));

        // Finally populate each field of a line with its counterpart
        ArrayList<Point> centerPoints = new ArrayList<Point>();
        for (int i = 0; i < vectorFields.size(); i++) {
            List<Point> populatedVec = populateVector(
                    vectorFields.get(i), oppositeVectorFields.get(i), verticalDistanceRatio);
            centerPoints.addAll(populatedVec);
        }
        rows = centerPoints.size() / cols;

        pointsToSearchFields(centerPoints);
    }

    /**
     * A vector is formulated by to Points, start and end.
     * This method populates the space in between these points with a set number of
     * additional instances of Point.
     *
     * @param startPoint    start of the vector.
     * @param endPoint      end of the vector.
     * @param distanceRatio number of spots that shall be added in between
     *                      start and endpoint.
     * @return a list containing points on a defined vector.
     */
    private ArrayList<Point> populateVector(Point startPoint, Point endPoint, int distanceRatio) {
        ArrayList<Point> populatedVector = new ArrayList<Point>();
        populatedVector.add(startPoint);

        if (distanceRatio >= 3) {
            double inBetweenX;
            double inBetweenY;
            double deltaFract;
            distanceRatio--;
            for (int i = 1; i < distanceRatio; i++) {
                deltaFract = i / (double) distanceRatio;
                inBetweenX = startPoint.x + (endPoint.x - startPoint.x) * deltaFract;
                inBetweenY = startPoint.y + (endPoint.y - startPoint.y) * deltaFract;

                populatedVector.add(Point.of(inBetweenX, inBetweenY));
            }
        }
        populatedVector.add(endPoint);

        return populatedVector;
    }

    /**
     * Adds searchFields to the corresponding ArrayList of this instance,
     * by using a list of centerPoints.
     *
     * @param centerPoints list of points the individualSearchFields are based upon.
     */
    private void pointsToSearchFields(ArrayList<Point> centerPoints) {
        for (Point centerPoint : centerPoints) {
            PolyShape copyShape = measureField.getCopy();
            copyShape.moveToCenter(centerPoint);

            searchFields.add(SearchField.of(this, copyShape, radius));
        }
    }

    /**
     * Establishes a one-directional association between two searchFields.
     * Later on this is used to efficiently check for intersections.
     *
     * Interlinking is done from top to bottom - left to right, there are no double
     * associations.
     * This means that theres no interlinking with fields of a lower index.
     *
     * When checking for intersections later on this prevents any redundancies.
     */
    public void interlinkFields() {
        for (int i = 0; i < searchFields.size(); i++) {
            // Interlink with next field in col if there is one
            boolean lastRow = (i + 1) % rows == 0;
            if (!lastRow) {
                searchFields.get(i).interlinkWith(searchFields.get(i + 1));

                // diagonally downwards-left
                if (i / rows > 0) {
                    searchFields.get(i).interlinkWith(searchFields.get(i - rows + 1));
                }
            }
            // Check if this is not the last col
            if ((i / rows) < (cols - 1)) {
                // If so interlink with the field directly to the right
                searchFields.get(i).interlinkWith(searchFields.get(i + rows));

                // To the diafonally downwards-right if this searchField is not in the last
                // column
                if (!lastRow) {
                    searchFields.get(i).interlinkWith(searchFields.get(i + rows + 1));
                }

            }
        }
    }

    public void reduceIntersections() {
        for (int i = 0; i < searchFields.size(); i++) {
            ArrayList<SearchField> delQueue = searchFields.get(i).reduceIntersections();
            // Incase current searchField is deleted
            if (delQueue.contains(searchFields.get(i))) {
                i--;
            }
            searchFields.removeAll(delQueue);
        }
    }

    private void snapshot() {
        ArrayList<Point> pos = new ArrayList<Point>();
        for (SearchField sField : searchFields) {
            pos.add(sField.getCenterPoint());
        }

        control.recordSearchAlgo(searchPerimeter, pos, hWidth, hHeight).show();
    }

    public Point searchForAbsoluteMax() {
        SearchField maxField = searchFields.get(0);
        double maxValue = 0;

        while (searchFields.size() > 0) {
            reduceIntersections();
            // snapshot();
            for (int i = 0; i < searchFields.size(); i++) {
                if (searchFields.get(i).moveToLocalMax()) {
                    if (maxValue < searchFields.get(i).getPositionValue()) {
                        maxValue = searchFields.get(i).getPositionValue();
                        maxField = searchFields.get(i);
                    }
                    // snapshot();
                    searchFields.remove(i);
                    i--;
                }
            }
        }

        return maxField.getCenterPoint();
    }
}
