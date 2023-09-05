package com.scitequest.martin.settings;

import java.awt.Color;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;

import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;

public final class DisplaySettings implements JsonExportable {

    /**
     * Represents a look and feel theme that can be selected.
     */
    public enum Theme {
        FLAT_LIGHT_LAF("Flat Light Laf", "com.formdev.flatlaf.FlatLightLaf"),
        FLAT_DARK_LAF("Flat Dark Laf", "com.formdev.flatlaf.FlatDarkLaf"),
        FLAT_INTELLIJ_LAF("Flat IntelliJ Laf", "com.formdev.flatlaf.FlatIntelliJLaf"),
        FLAT_DARCULA_LAF("Flat Darcula Laf", "com.formdev.flatlaf.FlatDarculaLaf"),
        FLAT_MAC_LIGHT_LAF("Flat Mac Light Laf", "com.formdev.flatlaf.themes.FlatMacLightLaf"),
        FLAT_MAC_DARK_LAF("Flat Mac Dark Laf", "com.formdev.flatlaf.themes.FlatMacDarkLaf");

        /** The name of the theme. */
        private final String displayName;
        /** The class name/path of the theme. */
        private final String className;

        private Theme(String displayName, String className) {
            this.displayName = displayName;
            this.className = className;
        }

        /**
         * Get the display name of the theme.
         *
         * @return the display name
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Get the class path of the theme.
         *
         * @return the class name
         */
        public String getClassName() {
            return className;
        }
    }

    private Theme theme = Theme.values()[0];

    private Color deletionRectangleColor = Color.RED;
    private Color halflineColor = Color.BLUE;
    private Color measureCircleColor = Color.YELLOW;
    private Color slideColor = Color.BLUE;
    private Color spotfieldColor = Color.GREEN;

    private boolean enableButtonHelp = true;
    private boolean showMeasureCircles = true;
    private boolean showSpotfieldGrids = true;

    private DisplaySettings() {
    }

    private DisplaySettings(Theme theme,
            Color deletionRectangleColor, Color halflineColor,
            Color measureCircleColor, Color slideColor, Color spotfieldColor,
            boolean enableButtonHelp, boolean showMeasureCircles, boolean showSpotfieldGrids) {
        this.theme = theme;
        this.deletionRectangleColor = deletionRectangleColor;
        this.halflineColor = halflineColor;
        this.measureCircleColor = measureCircleColor;
        this.slideColor = slideColor;
        this.spotfieldColor = spotfieldColor;
        this.enableButtonHelp = enableButtonHelp;
        this.showMeasureCircles = showMeasureCircles;
        this.showSpotfieldGrids = showSpotfieldGrids;
    }

    static DisplaySettings of(Theme theme,
            Color deletionRectangleColor, Color halflineColor,
            Color measureCircleColor, Color slideColor, Color spotfieldColor,
            boolean enableButtonHelp, boolean showMeasureCircles, boolean showSpotfieldGrids) {
        return new DisplaySettings(theme,
                deletionRectangleColor, halflineColor, measureCircleColor, slideColor,
                spotfieldColor, enableButtonHelp, showMeasureCircles, showSpotfieldGrids);
    }

    static DisplaySettings defaultSettings() {
        return new DisplaySettings();
    }

    void copyInto(DisplaySettings other) {
        this.theme = other.theme;
        this.deletionRectangleColor = other.deletionRectangleColor;
        this.halflineColor = other.halflineColor;
        this.measureCircleColor = other.measureCircleColor;
        this.slideColor = other.slideColor;
        this.spotfieldColor = other.spotfieldColor;
        this.enableButtonHelp = other.enableButtonHelp;
        this.showMeasureCircles = other.showMeasureCircles;
        this.showSpotfieldGrids = other.showSpotfieldGrids;
    }

    /**
     * Get the current selected theme.
     *
     * @return the theme class name
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Set the theme.
     *
     * @param theme the new theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * Returns the colour of the background-rectangles.
     *
     * @return colour of delRects.
     */
    public Color getDeletionRectangleColor() {
        return deletionRectangleColor;
    }

    /**
     * Sets colour of the background rectangles.
     *
     * @param deletionRectangleColor new colour of background rectangles.
     */
    public void setDeletionRectangleColor(Color deletionRectangleColor) {
        this.deletionRectangleColor = deletionRectangleColor;
    }

    /**
     * Returns the colour of the dividing line between both spot-fields.
     *
     * @return colour of dividing line.
     */
    public Color getHalflineColor() {
        return halflineColor;
    }

    /**
     * Sets the colour of the dividing line between both spot-fields.
     *
     * @param halflineColor new color of divide line.
     */
    public void setHalflineColor(Color halflineColor) {
        this.halflineColor = halflineColor;
    }

    /**
     * Returns the colour of the measuring circles.
     *
     * @return colour of mesuring circles.
     */
    public Color getMeasureCircleColor() {
        return measureCircleColor;
    }

    /**
     * Sets colour of measuring circles.
     *
     * @param measureCircleColor new colour of measuring circles.
     */
    public void setMeasureCircleColor(Color measureCircleColor) {
        this.measureCircleColor = measureCircleColor;
    }

    /**
     * Returns the colour of the outer frame of the slideMask.
     *
     * @return colour of outer frame of slideMask.
     */
    public Color getSlideColor() {
        return slideColor;
    }

    /**
     * Sets the colour of the outer frame of the slide-mask.
     *
     * @param slideColor new colour of the slide-mask frame.
     */
    public void setSlideColor(Color slideColor) {
        this.slideColor = slideColor;
    }

    /**
     * Returns the colour of the spot-fields.
     *
     * @return colour of the spot-fields.
     */
    public Color getSpotfieldColor() {
        return spotfieldColor;
    }

    /**
     * Sets the colour of the spot-field grid.
     *
     * @param spotfieldColor new colour of the spot-field grids.
     */
    public void setSpotfieldColor(Color spotfieldColor) {
        this.spotfieldColor = spotfieldColor;
    }

    /**
     * Returns if the gui is supposed to show button information when hovering over
     * said elements.
     *
     * @return if true button info is shown on main gui.
     */
    public boolean isEnableButtonHelp() {
        return enableButtonHelp;
    }

    /**
     * Sets if button information shall be shown when hovering over the relevant
     * elements.
     *
     * @param enableButtonHelp if true button info is shown on main gui.
     */
    public void setEnableButtonHelp(boolean enableButtonHelp) {
        this.enableButtonHelp = enableButtonHelp;
    }

    /**
     * Returns if the measure-circles are supposed to be shown.
     *
     * @return Setting if the measure-circles are to be shown.
     */
    public boolean isShowMeasureCircles() {
        return showMeasureCircles;
    }

    /**
     * Sets if the measure-circles is to be shown.
     *
     * @param showMeasureCircles setting if the measure-circles are to be depicted.
     */
    public void setShowMeasureCircles(boolean showMeasureCircles) {
        this.showMeasureCircles = showMeasureCircles;
    }

    /**
     * Returns if the spot-field is supposed to be shown.
     *
     * @return Setting if the spot-field is to be shown.
     */
    public boolean isShowSpotfieldGrids() {
        return showSpotfieldGrids;
    }

    /**
     * Sets if the spot-field is to be shown.
     *
     * @param showSpotfieldGrids setting if the spot-field is to be depicted.
     */
    public void setShowSpotfieldGrids(boolean showSpotfieldGrids) {
        this.showSpotfieldGrids = showSpotfieldGrids;
    }

    public static String color2hex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    static DisplaySettings fromJson(JsonObject obj) throws JsonParseException {
        try {
            String themeString = obj.getString("theme");
            Theme theme = Theme.valueOf(themeString);

            JsonObject colors = obj.get("colors").asJsonObject();
            Color deletionRectangesColor = Color.decode(colors.getString("deletion_rectangle"));
            Color halflineColor = Color.decode(colors.getString("halfline"));
            Color measureCircleColor = Color.decode(colors.getString("measure_circle"));
            Color slideColor = Color.decode(colors.getString("slide"));
            Color spotfieldsColor = Color.decode(colors.getString("spotfield"));

            boolean enableButtonHelp = obj.getBoolean("enable_button_help");
            boolean showMeasureCircles = obj.getBoolean("show_measure_circles");
            boolean showSpotfieldGrids = obj.getBoolean("show_spotfield_grids");

            DisplaySettings displaySettings = of(theme,
                    deletionRectangesColor, halflineColor, measureCircleColor,
                    slideColor, spotfieldsColor,
                    enableButtonHelp, showMeasureCircles, showSpotfieldGrids);
            return displaySettings;
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid hex color", e);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Illegal theme", e);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        } catch (NullPointerException e) {
            throw new JsonParseException("Expected JSON key is missing", e);
        }
    }

    @Override
    public JsonStructure asJson() {
        JsonObject json = Json.createObjectBuilder()
                .add("theme", theme.name())
                .add("colors", Json.createObjectBuilder()
                        .add("deletion_rectangle", color2hex(deletionRectangleColor))
                        .add("halfline", color2hex(halflineColor))
                        .add("measure_circle", color2hex(measureCircleColor))
                        .add("slide", color2hex(slideColor))
                        .add("spotfield", color2hex(spotfieldColor)))
                .add("enable_button_help", enableButtonHelp)
                .add("show_measure_circles", showMeasureCircles)
                .add("show_spotfield_grids", showSpotfieldGrids)
                .build();
        return json;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((theme == null) ? 0 : theme.hashCode());
        result = prime * result + ((deletionRectangleColor == null) ? 0 : deletionRectangleColor.hashCode());
        result = prime * result + ((halflineColor == null) ? 0 : halflineColor.hashCode());
        result = prime * result + ((measureCircleColor == null) ? 0 : measureCircleColor.hashCode());
        result = prime * result + ((slideColor == null) ? 0 : slideColor.hashCode());
        result = prime * result + ((spotfieldColor == null) ? 0 : spotfieldColor.hashCode());
        result = prime * result + (enableButtonHelp ? 1231 : 1237);
        result = prime * result + (showMeasureCircles ? 1231 : 1237);
        result = prime * result + (showSpotfieldGrids ? 1231 : 1237);
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
        DisplaySettings other = (DisplaySettings) obj;
        if (theme != other.theme)
            return false;
        if (deletionRectangleColor == null) {
            if (other.deletionRectangleColor != null)
                return false;
        } else if (!deletionRectangleColor.equals(other.deletionRectangleColor))
            return false;
        if (halflineColor == null) {
            if (other.halflineColor != null)
                return false;
        } else if (!halflineColor.equals(other.halflineColor))
            return false;
        if (measureCircleColor == null) {
            if (other.measureCircleColor != null)
                return false;
        } else if (!measureCircleColor.equals(other.measureCircleColor))
            return false;
        if (slideColor == null) {
            if (other.slideColor != null)
                return false;
        } else if (!slideColor.equals(other.slideColor))
            return false;
        if (spotfieldColor == null) {
            if (other.spotfieldColor != null)
                return false;
        } else if (!spotfieldColor.equals(other.spotfieldColor))
            return false;
        if (enableButtonHelp != other.enableButtonHelp)
            return false;
        if (showMeasureCircles != other.showMeasureCircles)
            return false;
        if (showSpotfieldGrids != other.showSpotfieldGrids)
            return false;
        return true;
    }
}
