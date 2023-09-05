package com.scitequest.martin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SearchField {
    /** Grid of this search area. */
    private final SearchArea searchArea;
    /**
     * PolyShape the measurements are taken in, here only used to check collision..
     */
    private PolyShape searchPoly;
    /** Radius of this polyShape, only relevant if shape == circle. */
    private final double radius;
    /** Last direction the field was moved to. Four beeing no movement.. */
    private int lastDir = 4;
    /** Mean signal strength of current position. */
    private double positionValue;
    /** List of adjacent searchFields. */
    private final ArrayList<SearchField> interlinkedFields;
    /** When on max do not check for intersections. */
    private boolean isOnMax = false;

    private SearchField(SearchArea searchArea, PolyShape searchPoly, double radius) {
        this.searchArea = searchArea;
        this.searchPoly = searchPoly;
        this.radius = radius;
        Point cPoint = searchPoly.getShapeCenter();
        this.positionValue = searchArea.getMeasureFieldMean(false, cPoint.x, cPoint.y);
        interlinkedFields = new ArrayList<SearchField>();
    }

    public static SearchField of(SearchArea grid, PolyShape searchPoly, double radius) {
        return new SearchField(grid, searchPoly, radius);
    }

    public void interlinkWith(SearchField other) {
        interlinkedFields.add(other);
    }

    /**
     * Searches in a one pixel-perimeter around current position for local max.
     * Position is set to the highest measured value and positionValue is updated
     * accordingly.
     *
     * Previous positions aren't checked, since they logically have a lower value.
     *
     * Returns false if a movement was performed.
     *
     * @return true if current position is the maximum.
     */
    public boolean moveToLocalMax() {
        double maxValue = positionValue;
        double measuredValue = 0;
        Point cPoint = this.getCenterPoint();
        int maxDir = 4;
        int currentDir = 0;

        for (int yRun = -1; yRun <= 1; yRun++) {
            for (int xRun = -1; xRun <= 1; xRun++) {
                currentDir = xyDirToMatrix(xRun, yRun);
                if (!isBacktrack(currentDir)) {
                    measuredValue = searchArea.getMeasureFieldMean(true,
                            cPoint.x + xRun, cPoint.y + yRun);
                    if (measuredValue > maxValue) {
                        maxValue = measuredValue;
                        maxDir = currentDir;
                    }
                }
            }
        }
        // No movement, local maximum found
        if (maxDir == 4) {
            this.isOnMax = true;
            return true;
        }

        this.positionValue = maxValue;

        int xMove = (maxDir % 3) - 1;
        int yMove = (maxDir / 3) - 1;

        this.searchPoly.movePoly(xMove, yMove);
        this.lastDir = maxDir;

        return false;
    }

    /**
     * This method checks if a given position was previously measured.
     * Since the searchField always moves to the directly adjacent position with
     * maximum value, some checks will be redundant.
     *
     * This is because it already is estabilshed that the current position is the
     * maximum for the previous search perimeter. This means any overlap between the
     * current search perimeter and the previous one can be disregarded.
     *
     * Since four is the current position it should never be checked,
     * this is the main reason for storing the current SearchField value.
     *
     * Consider the following to understand the numbers in the switch-case:
     * 0 1 2
     * 3 4 5
     * 6 7 8
     *
     * Four beeing the new center with last dir pointing in the opposite direction
     * of the old center. lastDir = 0 means the old center is eight.
     * This means that 4,5,7 and 8 are redundant checks (backtracking).
     *
     * @param dir direction to be checked for backtrack.
     * @return true if check in dir is redundant.
     */
    private boolean isBacktrack(int dir) {

        List<Integer> backtrackDirs;
        // Trust me on this one
        switch (lastDir) {
            case 0:
                backtrackDirs = Arrays.asList(4, 5, 7, 8);
                break;
            case 1:
                backtrackDirs = Arrays.asList(3, 4, 5, 6, 7, 8);
                break;
            case 2:
                backtrackDirs = Arrays.asList(3, 4, 6, 7);
                break;
            case 3:
                backtrackDirs = Arrays.asList(1, 2, 4, 5, 7, 8);
                break;
            case 5:
                backtrackDirs = Arrays.asList(0, 1, 3, 4, 6, 7);
                break;
            case 6:
                backtrackDirs = Arrays.asList(1, 2, 4, 5);
                break;
            case 7:
                backtrackDirs = Arrays.asList(0, 1, 2, 3, 4, 5);
                break;
            case 8:
                backtrackDirs = Arrays.asList(0, 1, 3, 4);
                break;
            default:
                backtrackDirs = Arrays.asList(4);
                break;
        }
        return backtrackDirs.contains(dir);
    }

    /**
     * dirX and dirY can be -1, 0 or 1.
     * Incrementing them by one makes 0, 1 or 2.
     *
     * The calculation below leads to the following matrix:
     * 0 1 2
     * 3 4 5
     * 6 7 8
     *
     * @param dirX movement direction in x.
     * @param dirY movement direction in y.
     * @return integer dennoting the direction moved to.
     */
    public int xyDirToMatrix(int dirX, int dirY) {
        return (dirX + 1) + 3 * (dirY + 1);
    }

    /**
     * Checks if an interlinkedSearchField is intersecting with this searchField.
     * If there are intersections the highest and second highest fields are
     * determined.
     *
     * If this instance is not the highest field all of its interlinked field
     * references are passed on to the highest field. If it is the highest field all
     * intersecting fields pass on their interlink-references to this field.
     *
     * All intersecting fields that are not the maximum intersection are stored on a
     * list, later to be deleted in SearchGrid.
     * Before that all interlink references to these fields are deleted.
     *
     * An in-between field is generated between the highest field and
     * second highest intersecting field. If the in-between position
     * has a higher value it replaces the highest field.
     *
     * @return list of intersecting searchFields of a lower value that are to be
     *         deleted.
     */
    public ArrayList<SearchField> reduceIntersections() {
        ArrayList<SearchField> intersectFields = new ArrayList<SearchField>();
        for (SearchField linkedField : interlinkedFields) {
            if (!linkedField.isOnMax) {
                if (this.searchPoly.polysIntersect(radius, linkedField.searchPoly)) {
                    intersectFields.add(linkedField);
                }
            }
        }
        ArrayList<SearchField> lowerFields = new ArrayList<SearchField>();

        SearchField maxField = this;
        double maxValue = positionValue;
        SearchField penultimateField = this;
        double penultimateValue = positionValue;
        for (SearchField iField : intersectFields) {
            if (maxValue < iField.positionValue) {
                penultimateField = maxField;
                penultimateValue = maxValue;
                maxField = iField;
                maxValue = iField.positionValue;
            } else {
                if (penultimateValue < iField.positionValue) {
                    penultimateField = iField;
                    penultimateValue = iField.positionValue;
                }
            }
        }
        if (!maxField.equals(this)) {
            lowerFields.add(this);
        }
        for (SearchField iField : intersectFields) {
            if (!maxField.equals(iField)) {
                lowerFields.add(iField);
            }
        }
        if (!maxField.equals(penultimateField)) {
            maxField.checkInBetweenPosition(penultimateField);
        }
        maxField.delink(lowerFields);
        return lowerFields;
    }

    private void checkInBetweenPosition(SearchField other) {
        PolyShape inBetweenPoly = this.searchPoly.averageCoords(other.searchPoly);
        Point iBCenter = inBetweenPoly.getShapeCenter();
        double inBetweenValue = searchArea.getMeasureFieldMean(false, iBCenter.x, iBCenter.y);

        if (inBetweenValue > this.positionValue) {
            this.positionValue = inBetweenValue;
            this.lastDir = 4;
            this.searchPoly = inBetweenPoly;
        }
    }

    private void delink(ArrayList<SearchField> lowerFields) {
        for (int i = interlinkedFields.size() - 1; i >= 0; i--) {
            if (lowerFields.contains(interlinkedFields.get(i))) {
                interlinkedFields.remove(i);
            }
        }
    }

    public int getLastDir() {
        return lastDir;
    }

    public double getPositionValue() {
        return positionValue;
    }

    public ArrayList<SearchField> getInterlinkedFields() {
        return interlinkedFields;
    }

    public Point getCenterPoint() {
        return searchPoly.getShapeCenter();
    }

    @Override
    public String toString() {
        return "SearchField: searchPoly = "
                + searchPoly.getShapeCenter().x + " / " + searchPoly.getShapeCenter().y
                + ", positionValue = " + positionValue;
    }

}
