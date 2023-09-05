package com.scitequest.martin.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scitequest.martin.Point;
import com.scitequest.martin.PolyGrid;
import com.scitequest.martin.SlideMask;
import com.scitequest.martin.Version;
import com.scitequest.martin.settings.MaskExt;
import com.scitequest.martin.settings.Settings;
import com.scitequest.martin.settings.MaskSettings.MeasureShape;

public final class Parameters {

    /** The current version of the file format specification. */
    public static final Version CURRENT_VERSION = Version.of(0, 2, 0);

    @JsonProperty("invert_lut")
    private final boolean invertLut;
    @JsonProperty("subract_background")
    private final boolean subtractBackground;
    @JsonProperty("number_of_spotfields")
    private final int numberOfSpotfields;
    @JsonProperty("columns_per_spotfield")
    private final int columnsPerSpotfield;
    @JsonProperty("spots_per_spotfield")
    private final int spotsPerSpotfield;
    /** The geometries used to measure spots. */
    @JsonProperty("spots")
    private final List<Geometry> spots;
    /** The rectangles used to subtract the background. */
    @JsonProperty("background_rectangles")
    private final List<Polygon> backgroundRectangles;

    @JsonCreator
    public Parameters(
            @JsonProperty("invert_lut") boolean invertLut,
            @JsonProperty("subract_background") boolean subtractBackground,
            @JsonProperty("number_of_spotfields") int numberOfSpotfields,
            @JsonProperty("columns_per_spotfield") int columnsPerSpotfield,
            @JsonProperty("spots_per_spotfield") int spotsPerSpotfield,
            @JsonProperty("spots") List<Geometry> spots,
            @JsonProperty("background_rectangles") List<Polygon> backgroundRectangles) {
        this.invertLut = invertLut;
        this.subtractBackground = subtractBackground;
        this.numberOfSpotfields = numberOfSpotfields;
        this.columnsPerSpotfield = columnsPerSpotfield;
        this.spotsPerSpotfield = spotsPerSpotfield;
        // Safety: Wrap to prevent outside modification
        this.spots = Collections.unmodifiableList(spots);
        // Safety: Wrap to prevent outside modification
        this.backgroundRectangles = Collections.unmodifiableList(backgroundRectangles);
    }

    public static Parameters fromSettingsAndSlide(Settings settings, SlideMask slide) {
        // Collect background rectangles
        List<Polygon> backgroundRectangles = new ArrayList<Polygon>();
        for (double[][] bRect : slide.getBackgroundRectCoordinates()) {
            backgroundRectangles.add(Polygon.ofRectangle(
                    Point.of(bRect[0][0], bRect[1][0]),
                    Point.of(bRect[0][1], bRect[1][1]),
                    Point.of(bRect[0][2], bRect[1][2]),
                    Point.of(bRect[0][3], bRect[1][3])));
        }

        MaskExt mask = settings.getMaskSettings().getLastUsedMask();
        int diameter = mask.getMeasureFieldWidth();

        List<PolyGrid> measureFields = slide.getMeasureFields();
        List<Geometry> spots = measureFields.stream()
                .map(grid -> grid.getGrid())
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .map(polyShape -> {
                    if (polyShape.getShape() == MeasureShape.CIRCLE) {
                        return Circle.of(polyShape.getShapeCenter(), diameter);
                    }
                    double[][] arrayCoords = polyShape.getPolyCoordinates();
                    List<Point> coords = new ArrayList<>();
                    for (int i = 0; i < arrayCoords[0].length; i++) {
                        coords.add(Point.of(arrayCoords[0][i], arrayCoords[1][i]));
                    }
                    return Polygon.ofPolygon(coords);
                })
                .collect(Collectors.toList());

        return new Parameters(
                settings.getMeasurementSettings().isInvertLut(),
                settings.getMeasurementSettings().isSubtractBackground(),
                measureFields.size(),
                mask.getSpotFieldNColumns(),
                settings.getMaskSettings().getLastMeasurePointIndex() + 1,
                spots,
                backgroundRectangles);
    }

    public boolean isInvertLut() {
        return invertLut;
    }

    public boolean isSubtractBackground() {
        return subtractBackground;
    }

    public int getNumberOfSpotfields() {
        return numberOfSpotfields;
    }

    public int getColumnsPerSpotfield() {
        return columnsPerSpotfield;
    }

    public int getSpotsPerSpotfield() {
        return spotsPerSpotfield;
    }

    public List<Geometry> getSpots() {
        return spots;
    }

    public List<Polygon> getBackgroundRectangles() {
        return backgroundRectangles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (invertLut ? 1231 : 1237);
        result = prime * result + (subtractBackground ? 1231 : 1237);
        result = prime * result + numberOfSpotfields;
        result = prime * result + columnsPerSpotfield;
        result = prime * result + spotsPerSpotfield;
        result = prime * result + ((spots == null) ? 0 : spots.hashCode());
        result = prime * result + ((backgroundRectangles == null) ? 0 : backgroundRectangles.hashCode());
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
        Parameters other = (Parameters) obj;
        if (invertLut != other.invertLut)
            return false;
        if (subtractBackground != other.subtractBackground)
            return false;
        if (numberOfSpotfields != other.numberOfSpotfields)
            return false;
        if (columnsPerSpotfield != other.columnsPerSpotfield)
            return false;
        if (spotsPerSpotfield != other.spotsPerSpotfield)
            return false;
        if (spots == null) {
            if (other.spots != null)
                return false;
        } else if (!spots.equals(other.spots))
            return false;
        if (backgroundRectangles == null) {
            if (other.backgroundRectangles != null)
                return false;
        } else if (!backgroundRectangles.equals(other.backgroundRectangles))
            return false;
        return true;
    }
}
