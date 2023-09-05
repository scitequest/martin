package com.scitequest.martin.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import com.scitequest.martin.Version;
import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;

public final class Settings implements JsonExportable {

    /** The current version of the file format specification. */
    public static final Version CURRENT_VERSION = Version.of(0, 1, 1);

    private final SettingsHolder editable = SettingsHolder.defaultSettings();
    private final SettingsHolder checkpoint = SettingsHolder.defaultSettings();
    private Optional<Path> path = Optional.empty();
    private boolean isSaved = true;

    private Settings() {
    }

    private Settings(SettingsHolder holder) {
        this.checkpoint.copyInto(holder);
        this.editable.copyInto(holder);
    }

    public static Settings defaultSettings() {
        return new Settings();
    }

    public static Settings load(Path path) throws IOException, JsonParseException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject obj = jsonReader.readObject();
            if (obj.containsKey("version")) {
                Version version = Version.of(obj.getString("version"));
                if (!CURRENT_VERSION.isCompatible(version)) {
                    throw new JsonParseException("Provided settings are incompatible");
                }
            }
            Settings settings = fromJson(obj);
            settings.path = Optional.of(path);
            return settings;
        } catch (JsonException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    /**
     * Load settings from the speciefied file or get the default settings if the
     * file does not exist.
     *
     * @param path the path to the JSON file
     * @return the settings object
     * @throws IOException        if the file could not be read from the filesystem
     * @throws JsonParseException if the JSON could not be parsed
     */
    public static Settings loadOrDefault(Path path) throws IOException, JsonParseException {
        try {
            return Settings.load(path);
        } catch (NoSuchFileException e) {
            Settings settings = Settings.defaultSettings();
            settings.path = Optional.of(path);
            return settings;
        }
    }

    public void store() {
        if (isDirty()) {
            isSaved = false;
        }
        checkpoint.copyInto(editable);
    }

    public void abort() {
        editable.copyInto(checkpoint);
    }

    public void save() throws IllegalArgumentException, IOException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Default save path is not set");
        }
        save(path.get());
    }

    public boolean isDirty() {
        return !checkpoint.equals(editable);
    }

    public boolean isSaved() {
        return isSaved && !isDirty();
    }

    public int getCheckpointMaskIndex() {
        return checkpoint.maskSettings.getLastUsedMaskIndex();
    }

    public void save(Path savePath) throws IOException {
        // Create base directory if it does not exist
        Path parent = savePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        var jsonConfig = Map.of(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(jsonConfig);

        try (BufferedWriter writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8);
                JsonWriter jsonWriter = jsonWriterFactory.createWriter(writer)) {
            jsonWriter.write(this.asJson());
        }

        isSaved = true;
        this.path = Optional.of(savePath);
    }

    public DisplaySettings getDisplaySettings() {
        return editable.displaySettings;
    }

    public ExportSettings getExportSettings() {
        return editable.exportSettings;
    }

    public MaskSettings getMaskSettings() {
        return editable.maskSettings;
    }

    public MeasurementSettings getMeasurementSettings() {
        return editable.measurementSettings;
    }

    /**
     * Get the current project settings.
     *
     * @return project settings
     */
    public ProjectSettings getProjectSettings() {
        return editable.projectSettings;
    }

    public static Settings fromJson(JsonObject obj) throws JsonParseException {
        try {
            var displaySettings = DisplaySettings.fromJson(obj.getJsonObject("display"));
            var exportSettings = ExportSettings.fromJson(obj.getJsonObject("export"));
            var maskSettings = MaskSettings.fromJson(obj.getJsonObject("mask"));
            var measurementSettings = MeasurementSettings.fromJson(
                    obj.getJsonObject("measurement"));
            var projectSettings = ProjectSettings.fromJson(obj.getJsonObject("project"));
            var holder = SettingsHolder.of(displaySettings, exportSettings, maskSettings,
                    measurementSettings, projectSettings);
            return new Settings(holder);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        }
    }

    @Override
    public JsonStructure asJson() {
        JsonObject obj = Json.createObjectBuilder()
                .add("version", CURRENT_VERSION.toString())
                .add("display", checkpoint.displaySettings.asJson())
                .add("export", checkpoint.exportSettings.asJson())
                .add("mask", checkpoint.maskSettings.asJson())
                .add("measurement", checkpoint.measurementSettings.asJson())
                .add("project", checkpoint.projectSettings.asJson())
                .build();
        return obj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((editable == null) ? 0 : editable.hashCode());
        result = prime * result + ((checkpoint == null) ? 0 : checkpoint.hashCode());
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
        Settings other = (Settings) obj;
        if (editable == null) {
            if (other.editable != null)
                return false;
        } else if (!editable.equals(other.editable))
            return false;
        if (checkpoint == null) {
            if (other.checkpoint != null)
                return false;
        } else if (!checkpoint.equals(other.checkpoint))
            return false;
        return true;
    }
}
