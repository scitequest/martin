package com.scitequest.martin.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonStructure;

import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.settings.MaskSettings.MeasureShape;

public final class MaskExt implements JsonExportable {

    public static final String DEFAULT_NAME = "New slide mask";

    private final String name;
    private final String description;

    private final int slideWidth;
    private final int slideHeight;
    private final int xInset;
    private final int yInset;

    private final int spotFieldWidth;
    private final int spotFieldHeight;

    private final int superGridCols;
    private final int superGridRows;
    private final List<Integer> horizontalSpacing;
    private final List<Integer> verticalSpacing;

    private final MeasureShape shape;
    private final int measureFieldWidth;
    private final int measureFieldHeight;

    private final int spotFieldNColumns;
    private final int spotFieldNRows;

    private MaskExt(String name, String description,
            int slideWidth, int slideHeight,
            int xInset, int yInset,
            int superGridCols, int superGridRows,
            List<Integer> horizontalSpacing, List<Integer> verticalSpacing,
            int spotFieldWidth, int spotFieldHeight,
            int spotfieldNRows, int spotfieldNColumns,
            MeasureShape shape,
            int measureFieldWidth, int measureFieldHeight) {

        this.name = name;
        this.description = description;
        this.slideWidth = slideWidth;
        this.slideHeight = slideHeight;
        this.xInset = xInset;
        this.yInset = yInset;
        this.superGridCols = superGridCols;
        this.superGridRows = superGridRows;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.spotFieldWidth = spotFieldWidth;
        this.spotFieldHeight = spotFieldHeight;
        this.spotFieldNRows = spotfieldNRows;
        this.spotFieldNColumns = spotfieldNColumns;
        this.shape = shape;
        this.measureFieldWidth = measureFieldWidth;
        this.measureFieldHeight = measureFieldHeight;
    }

    public static MaskExt of(String name, String description,
            int slideWidth, int slideHeight,
            int xInset, int yInset,
            int superGridCols, int superGridRows,
            List<Integer> hSpacing, List<Integer> vSpacing,
            int spotFieldWidth, int spotFieldHeight,
            int spotfieldNRows, int spotfieldNColumns,
            MeasureShape shape, int measureFieldWidth, int measureFieldHeight) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Safety: Create lists that only we control
        List<Integer> horizontalSpacing = new ArrayList<>(hSpacing);
        List<Integer> verticalSpacing = new ArrayList<>(vSpacing);

        int vSpaceSum = verticalSpacing.stream().mapToInt(Integer::intValue).sum();
        int contentWidth = xInset + superGridCols * spotFieldWidth + vSpaceSum;
        if (slideWidth < contentWidth) {
            throw new IllegalArgumentException("The content is to big for the slide!");
        }

        int hSpaceSum = horizontalSpacing.stream().mapToInt(Integer::intValue).sum();
        int contentHeight = yInset + superGridRows * spotFieldHeight + hSpaceSum;
        if (slideHeight < contentHeight) {
            throw new IllegalArgumentException("The content is to big for the slide!");
        }
        int sFieldCellWidth = spotFieldWidth / spotfieldNColumns;
        if (measureFieldWidth > sFieldCellWidth) {
            throw new IllegalArgumentException("The measure fields do not fit in the spot field cells!");
        }
        int sFieldCellHeight = spotFieldHeight / spotfieldNRows;
        if (measureFieldHeight > sFieldCellHeight) {
            throw new IllegalArgumentException("The measure fields do not fit in the spot field cells!");
        }

        return new MaskExt(name, description, slideWidth, slideHeight, xInset,
                yInset, superGridCols,
                superGridRows, horizontalSpacing, verticalSpacing,
                spotFieldWidth, spotFieldHeight, spotfieldNRows, spotfieldNColumns,
                shape,
                measureFieldWidth,
                measureFieldHeight);
    }

    public static MaskExt defaultSettings() {
        return MaskExt.of(DEFAULT_NAME, "",
                2000, 1000,
                50, 60,
                2, 1,
                List.of(), List.of(13),
                465, 312,
                16, 24,
                MeasureShape.CIRCLE,
                14, 14);
    }

    /**
     * Return a copied instance with the new name set.
     *
     * @param newName the new project name
     * @return copied instance
     */
    public MaskExt withName(String newName) {
        return new MaskExt(newName, description,
                slideWidth, slideHeight, xInset, yInset,
                superGridCols, superGridRows, horizontalSpacing, verticalSpacing,
                spotFieldWidth, spotFieldHeight, spotFieldNRows, spotFieldNColumns,
                shape, measureFieldWidth, measureFieldHeight);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSlideWidth() {
        return slideWidth;
    }

    public int getSlideHeight() {
        return slideHeight;
    }

    public int getxInset() {
        return xInset;
    }

    public int getyInset() {
        return yInset;
    }

    public int getSpotFieldWidth() {
        return spotFieldWidth;
    }

    public int getSpotFieldHeight() {
        return spotFieldHeight;
    }

    public int getSuperGridCols() {
        return superGridCols;
    }

    public int getSuperGridRows() {
        return superGridRows;
    }

    public List<Integer> getHorizontalSpacing() {
        return Collections.unmodifiableList(horizontalSpacing);
    }

    public List<Integer> getVerticalSpacing() {
        return Collections.unmodifiableList(verticalSpacing);
    }

    public MeasureShape getShape() {
        return shape;
    }

    public int getMeasureFieldWidth() {
        return measureFieldWidth;
    }

    public int getMeasureFieldHeight() {
        return measureFieldHeight;
    }

    public int getSpotFieldNColumns() {
        return spotFieldNColumns;
    }

    public int getSpotFieldNRows() {
        return spotFieldNRows;
    }

    /**
     * Gets the maximum number of spots available in a spotfield.
     * This is identical to rows times columns.
     *
     * @return the maximum number of spots available in a spotfield
     */
    public int getMaxNumberOfSpotsPerSpotfield() {
        return spotFieldNRows * spotFieldNColumns;
    }

    /**
     * Returns the normalized name.
     *
     * The normalized name is lowercase and trimmed.
     *
     * @return the normalized name
     */
    public String getNormalizedName() {
        return name.trim().toLowerCase(Locale.ENGLISH);
    }

    static MaskExt fromJson(JsonObject obj) throws JsonParseException {
        try {
            String name = obj.getString("name");
            String description = obj.getString("description");
            int slideWidth = obj.getInt("slide_width");
            int slideHeight = obj.getInt("slide_height");
            int xInset = obj.getInt("x_inset");
            int yInset = obj.getInt("y_inset");
            int superGridCols = obj.getInt("super_grid_cols");
            int superGridRows = obj.getInt("super_grid_rows");

            JsonArray hSpace = obj.getJsonArray("horizontal_spot_field_spacing");
            ArrayList<Integer> horizontalSpacing = new ArrayList<Integer>();
            for (int i = 0; i < hSpace.size(); i++) {
                horizontalSpacing.add(hSpace.getInt(i));
            }
            JsonArray vSpace = obj.getJsonArray("vertical_spot_field_spacing");
            ArrayList<Integer> verticalSpacing = new ArrayList<Integer>();
            for (int i = 0; i < vSpace.size(); i++) {
                verticalSpacing.add(vSpace.getInt(i));
            }
            int spotFieldWidth = obj.getInt("spot_field_width");
            int spotFieldHeight = obj.getInt("spot_field_height");
            int spotFieldNRows = obj.getInt("spot_field_n_rows");
            int spotFieldNColumns = obj.getInt("spot_field_n_columns");
            MeasureShape shape = MeasureShape.valueOf(obj.getString("measure_field_shape"));
            int measureFieldWidth = obj.getInt("measure_field_width");
            int measureFieldHeight = obj.getInt("measure_field_height");

            return MaskExt.of(name, description,
                    slideWidth, slideHeight, xInset, yInset,
                    superGridCols, superGridRows, horizontalSpacing, verticalSpacing,
                    spotFieldWidth, spotFieldHeight, spotFieldNRows, spotFieldNColumns,
                    shape, measureFieldWidth, measureFieldHeight);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        } catch (NullPointerException e) {
            throw new JsonParseException("Expected JSON key is missing", e);
        }
    }

    @Override
    public JsonStructure asJson() {
        JsonArrayBuilder hSpaceBuilder = Json.createArrayBuilder();
        for (int hS : horizontalSpacing) {
            hSpaceBuilder.add(hS);
        }
        JsonArrayBuilder vSpaceBuilder = Json.createArrayBuilder();
        for (int vS : verticalSpacing) {
            vSpaceBuilder.add(vS);
        }

        JsonObject obj = Json.createObjectBuilder()
                .add("name", name)
                .add("description", description)
                .add("slide_width", slideWidth)
                .add("slide_height", slideHeight)
                .add("x_inset", xInset)
                .add("y_inset", yInset)
                .add("super_grid_cols", superGridCols)
                .add("super_grid_rows", superGridRows)
                .add("horizontal_spot_field_spacing", hSpaceBuilder)
                .add("vertical_spot_field_spacing", vSpaceBuilder)
                .add("spot_field_width", spotFieldWidth)
                .add("spot_field_height", spotFieldHeight)
                .add("spot_field_n_rows", spotFieldNRows)
                .add("spot_field_n_columns", spotFieldNColumns)
                .add("measure_field_shape", shape.name())
                .add("measure_field_width", measureFieldWidth)
                .add("measure_field_height", measureFieldHeight)
                .build();
        return obj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + slideWidth;
        result = prime * result + slideHeight;
        result = prime * result + xInset;
        result = prime * result + yInset;
        result = prime * result + spotFieldWidth;
        result = prime * result + spotFieldHeight;
        result = prime * result + superGridCols;
        result = prime * result + superGridRows;
        result = prime * result + ((horizontalSpacing == null) ? 0 : horizontalSpacing.hashCode());
        result = prime * result + ((verticalSpacing == null) ? 0 : verticalSpacing.hashCode());
        result = prime * result + ((shape == null) ? 0 : shape.hashCode());
        result = prime * result + measureFieldWidth;
        result = prime * result + measureFieldHeight;
        result = prime * result + spotFieldNColumns;
        result = prime * result + spotFieldNRows;
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
        MaskExt other = (MaskExt) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (slideWidth != other.slideWidth)
            return false;
        if (slideHeight != other.slideHeight)
            return false;
        if (xInset != other.xInset)
            return false;
        if (yInset != other.yInset)
            return false;
        if (spotFieldWidth != other.spotFieldWidth)
            return false;
        if (spotFieldHeight != other.spotFieldHeight)
            return false;
        if (superGridCols != other.superGridCols)
            return false;
        if (superGridRows != other.superGridRows)
            return false;
        if (horizontalSpacing == null) {
            if (other.horizontalSpacing != null)
                return false;
        } else if (!horizontalSpacing.equals(other.horizontalSpacing))
            return false;
        if (verticalSpacing == null) {
            if (other.verticalSpacing != null)
                return false;
        } else if (!verticalSpacing.equals(other.verticalSpacing))
            return false;
        if (shape != other.shape)
            return false;
        if (measureFieldWidth != other.measureFieldWidth)
            return false;
        if (measureFieldHeight != other.measureFieldHeight)
            return false;
        if (spotFieldNColumns != other.spotFieldNColumns)
            return false;
        if (spotFieldNRows != other.spotFieldNRows)
            return false;
        return true;
    }
}
