package com.scitequest.martin.settings;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;

import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;

public final class MeasurementSettings implements JsonExportable {

    private boolean invertLut = true;
    private boolean subtractBackground = true;

    private MeasurementSettings() {
    }

    private MeasurementSettings(boolean invertLut, boolean subtractBackground) {
        this.invertLut = invertLut;
        this.subtractBackground = subtractBackground;
    }

    static MeasurementSettings defaultSettings() {
        return new MeasurementSettings();
    }

    void copyInto(MeasurementSettings other) {
        this.invertLut = other.invertLut;
        this.subtractBackground = other.subtractBackground;
    }

    /**
     * Returns if the lookup table should be reversed when measuring.
     *
     * @return boolean on reversal of lookup table.
     */
    public boolean isInvertLut() {
        return invertLut;
    }

    /**
     * Sets if the lookup table should be reversed when measuring.
     *
     * @param invertLut boolean on reversal of lookup table.
     */
    public void setInvertLut(boolean invertLut) {
        this.invertLut = invertLut;
    }

    /**
     * Returns if the delRects are to be depicted and used for,
     * background subtraction.
     *
     * @return boolean on delRect use.
     */
    public boolean isSubtractBackground() {
        return subtractBackground;
    }

    /**
     * Sets the usage of delRects.
     * If false no measurement and subtraction of background is taking place.
     *
     * @param subtractBackground usage of delRects.
     */
    public void setSubtractBackground(boolean subtractBackground) {
        this.subtractBackground = subtractBackground;
    }

    static MeasurementSettings fromJson(JsonObject obj) throws JsonParseException {
        try {
            boolean invertLut = obj.getBoolean("invert_lut");
            boolean subtractBackground = obj.getBoolean("subtract_background");
            return new MeasurementSettings(invertLut, subtractBackground);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        } catch (NullPointerException e) {
            throw new JsonParseException("Expected JSON key is missing", e);
        }
    }

    @Override
    public JsonStructure asJson() {
        JsonObject obj = Json.createObjectBuilder()
                .add("invert_lut", invertLut)
                .add("subtract_background", subtractBackground)
                .build();
        return obj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (invertLut ? 1231 : 1237);
        result = prime * result + (subtractBackground ? 1231 : 1237);
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
        MeasurementSettings other = (MeasurementSettings) obj;
        if (invertLut != other.invertLut)
            return false;
        if (subtractBackground != other.subtractBackground)
            return false;
        return true;
    }
}
