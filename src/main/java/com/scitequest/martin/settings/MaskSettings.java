package com.scitequest.martin.settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.scitequest.martin.Version;
import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.settings.exception.DuplicateElementException;
import com.scitequest.martin.settings.exception.OneElementRequiredException;

public final class MaskSettings implements JsonExportable {
    public enum MeasureShape {
        /**
         * A singular point instead of a proper shape.
         */
        POINT,
        /**
         * A circular shape width and height being the same.
         */
        CIRCLE,
        /**
         * A rectangle rotated by 45 degrees.
         * Has the same extreme positions as an oval.
         */
        DIAMOND,
        /**
         * Four sides, four corners.
         * Opposite sides are the same length.
         */
        RECTANGLE,
        /**
         * Connection of two points.
         */
        LINE
    }

    /** The current version of the file format specification. */
    public static final Version CURRENT_VERSION = Version.of(0, 2, 0);

    /** List of all masks. */
    private final List<MaskExt> masks;
    /** Set to keep track of duplicates in the actual mask list. */
    private final Set<String> maskNames;
    /** The index of the last used MaskSettingsExt. */
    private int lastUsedMaskIndex = -1;
    /** The index of the last measurepoint in the spotfield. */
    private int lastMeasurePointIndex;

    private MaskSettings(int lastUsedMaskParametersIndex, int lastMeasurepointIndex,
            List<MaskExt> maskParameterSets, Set<String> maskNames) {
        this.lastUsedMaskIndex = lastUsedMaskParametersIndex;
        this.lastMeasurePointIndex = lastMeasurepointIndex;
        this.masks = maskParameterSets;
        this.maskNames = maskNames;
    }

    /**
     * Create mask parameters from preexisting mask parameters.
     *
     * @param lastUsedMaskIndex     the index of the mask that was last used in the
     *                              masks parameter.
     * @param lastMeasurepointIndex the maximum index of measurepoints within a
     *                              spotfield of the currently selected mask by
     *                              {@code lastUsedMaskIndex}.
     * @param masks                 all mask parameters
     * @return the mask settings
     */
    static MaskSettings of(int lastUsedMaskIndex, int lastMeasurepointIndex,
            List<MaskExt> masks) {
        if (masks.isEmpty()) {
            throw new IllegalArgumentException("Masks cannot be empty");
        }
        if (lastUsedMaskIndex < 0 || lastUsedMaskIndex >= masks.size()) {
            throw new IllegalArgumentException("Last used mask index is out of bounds");
        }
        int maxSpotIndex = masks.get(lastUsedMaskIndex).getMaxNumberOfSpotsPerSpotfield() - 1;
        if (lastMeasurepointIndex < 0 || lastMeasurepointIndex > maxSpotIndex) {
            throw new IllegalArgumentException("Last measurepoint index is out of bounds");
        }
        Set<String> distinctNameSet = masksToDistinctNameSet(masks);
        if (masks.size() != distinctNameSet.size()) {
            throw new IllegalArgumentException("Masks list contains duplicate mask names");
        }

        return new MaskSettings(lastUsedMaskIndex, lastMeasurepointIndex,
                new ArrayList<>(masks), distinctNameSet);
    }

    /**
     * Initialize project settings with a default project.
     *
     * @return default mask parameters
     */
    static MaskSettings defaultSettings() {
        MaskExt defaultMask = MaskExt.defaultSettings();
        return MaskSettings.of(0,
                defaultMask.getMaxNumberOfSpotsPerSpotfield() - 1,
                List.of(defaultMask));
    }

    void copyInto(MaskSettings other) {
        masks.clear();
        masks.addAll(other.masks);
        maskNames.clear();
        maskNames.addAll(masksToDistinctNameSet(other.masks));
        lastUsedMaskIndex = other.lastUsedMaskIndex;
        lastMeasurePointIndex = other.lastMeasurePointIndex;
    }

    /**
     * Helper method to create a set of all normalized names of the mask names.
     *
     * @param masks the mask of which the normalized name set should be created
     * @return the distinct name set
     */
    private static Set<String> masksToDistinctNameSet(List<MaskExt> masks) {
        Set<String> distinctNameSet = masks.stream()
                .map(m -> m.getNormalizedName())
                .distinct()
                .collect(Collectors.toSet());
        return distinctNameSet;
    }

    public MaskExt importMask(Path path) throws IOException, JsonParseException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject obj = jsonReader.readObject();
            if (obj.containsKey("version")) {
                Version version = Version.of(obj.getString("version"));
                if (!CURRENT_VERSION.isCompatible(version)) {
                    throw new JsonParseException("Provided mask settings are incompatible");
                }
            }
            // Parse mask from JSON
            MaskExt mask = MaskExt.fromJson(obj);
            // Add the mask
            addMask(mask);
            return mask;
        } catch (JsonException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    /**
     * Export the specified mask as a JSON mask file.
     *
     * @param mask the mask to export
     * @param path the path to the file to be written
     * @throws IOException if the mask could not be exported
     */
    public static void exportMask(MaskExt mask, Path path) throws IOException {
        JsonObjectBuilder obj = Json.createObjectBuilder()
                .add("version", CURRENT_VERSION.toString());
        mask.asJson().asJsonObject().forEach((k, v) -> obj.add(k, v));

        Files.writeString(path, obj.build().toString(), StandardCharsets.UTF_8);
    }

    public int getNumberOfMasks() {
        return masks.size();
    }

    /**
     * Get a list of all mask names.
     *
     * @return mask names in a list
     */
    public List<String> getNameList() {
        return getNameList(masks);
    }

    /**
     * Get a list of all mask names.
     *
     * @param masks the masks of which the name list should be created
     * @return all mask names in a list
     */
    public static List<String> getNameList(List<MaskExt> masks) {
        return masks.stream().map(mask -> mask.getName()).collect(Collectors.toList());
    }

    /**
     * Get the mask at the specified index.
     *
     * @param index the index
     * @return the mask
     */
    public MaskExt getMask(int index) {
        return masks.get(index);
    }

    /**
     * Set the mask at the specified index.
     *
     * @param index the index of the mask that should be replaced
     * @param mask  the replacement mask
     */
    public void setMask(int index, MaskExt mask) {
        String oldNormalizedName = getMask(index).getNormalizedName();
        String newNormalizedName = mask.getNormalizedName();
        if (!oldNormalizedName.equals(newNormalizedName)
                && maskNames.contains(newNormalizedName)) {
            throw new DuplicateElementException();
        }
        masks.set(index, mask);
        maskNames.remove(oldNormalizedName);
        maskNames.add(newNormalizedName);
    }

    /**
     * Adds a new mask to the list of masks if the name is not a duplicate.
     *
     * @param mask the mask to add
     * @throws DuplicateElementException if the mask name is already in masks
     */
    public void addMask(MaskExt mask) {
        String normalizedName = mask.getNormalizedName();
        if (this.maskNames.contains(normalizedName)) {
            throw new DuplicateElementException();
        }
        masks.add(mask);
        maskNames.add(normalizedName);
    }

    /**
     * Remove a mask from the masks.
     *
     * @param index the index of the mask to remove
     * @throws OneElementRequiredException if attempted to remove the last mask in
     *                                     the list
     */
    public void removeMask(int index) {
        if (masks.size() <= 1) {
            throw new OneElementRequiredException();
        }
        MaskExt mask = masks.remove(index);
        maskNames.remove(mask.getNormalizedName());
        if (lastUsedMaskIndex == index) {
            // We as last used mask were deleted
            lastUsedMaskIndex = -1;
        } else if (lastUsedMaskIndex > index) {
            // The last used mask index must be shifted down since we deleted an element
            // before
            lastUsedMaskIndex--;
        }
    }

    /**
     * Returns the last used slideMask index.
     *
     * If there is no last used project set, the index is set to -1.
     *
     * @return last used project index or -1 if no last used project is set.
     */
    public int getLastUsedMaskIndex() {
        return lastUsedMaskIndex;
    }

    /**
     * Set the last used slideMask index directly.
     *
     * @param index the index
     * @throws IndexOutOfBoundsException if the index does not exist
     */
    public void setLastUsedMaskIndex(int index) {
        if (index < 0 || index >= masks.size()) {
            throw new IndexOutOfBoundsException(index);
        }
        this.lastUsedMaskIndex = index;
    }

    /**
     * Gets the last used mask.
     *
     * @return the last used mask
     */
    public MaskExt getLastUsedMask() {
        return masks.get(lastUsedMaskIndex);
    }

    public int getLastMeasurePointIndex() {
        return lastMeasurePointIndex;
    }

    /**
     * Sets the index of the last measure point.
     *
     * Please note, that there is almost no validation. If the provided index is
     * less than 0, it is set to 0. However, for positive numbers, anything can be
     * set and it has to be ensured outside that an index is within the current
     * mask its bounds.
     *
     * @param lastMeasurePointIndex the new last measure point index
     */
    public void setLastMeasurePointIndex(int lastMeasurePointIndex) {
        this.lastMeasurePointIndex = Math.max(0, lastMeasurePointIndex);
    }

    /**
     * Sets the last used measure point index of the last used mask to the maximum.
     */
    public void setLastUsedMaskLastMeasurePointIndexToMax() {
        lastMeasurePointIndex = masks.get(lastUsedMaskIndex)
                .getMaxNumberOfSpotsPerSpotfield() - 1;
    }

    @Override
    public JsonStructure asJson() {
        JsonArrayBuilder maskParametersJson = Json.createArrayBuilder();
        masks.stream().forEach(mask -> maskParametersJson.add(mask.asJson()));

        JsonObject obj = Json.createObjectBuilder()
                .add("last_used_mask_index", lastUsedMaskIndex)
                .add("spot_field_last_measurepoint_index", lastMeasurePointIndex)
                .add("masks", maskParametersJson)
                .build();
        return obj;
    }

    public static MaskSettings fromJson(JsonObject json) throws JsonParseException {
        try {
            int lastUsedMaskIndex = json.getInt("last_used_mask_index");
            int lastMeasurepointIndex = json.getInt("spot_field_last_measurepoint_index");
            List<MaskExt> masks = new ArrayList<>();
            for (JsonValue value : json.getJsonArray("masks")) {
                masks.add(MaskExt.fromJson(value.asJsonObject()));
            }
            return MaskSettings.of(lastUsedMaskIndex, lastMeasurepointIndex, masks);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid mask settings", e);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        } catch (NullPointerException e) {
            throw new JsonParseException("Expected JSON key is missing", e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((masks == null) ? 0 : masks.hashCode());
        result = prime * result + lastUsedMaskIndex;
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
        MaskSettings other = (MaskSettings) obj;
        if (masks == null) {
            if (other.masks != null)
                return false;
        } else if (!masks.equals(other.masks))
            return false;
        if (lastUsedMaskIndex != other.lastUsedMaskIndex)
            return false;
        return true;
    }
}
