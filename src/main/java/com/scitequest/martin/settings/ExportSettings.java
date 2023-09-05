package com.scitequest.martin.settings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;

import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;

public final class ExportSettings implements JsonExportable {

    private Optional<Path> exportDirectory = Optional.empty();
    private boolean exportTSV = true;
    private boolean exportJSON = true;
    private boolean saveAnnotatedImage = true;
    private boolean saveWholeImage = true;

    private ExportSettings() {
    }

    private ExportSettings(boolean exportTSV, boolean exportJSON, boolean saveAnnotatedImage, boolean saveWholeImage) {
        this.exportTSV = exportTSV;
        this.exportJSON = exportJSON;
        this.saveAnnotatedImage = saveAnnotatedImage;
        this.saveWholeImage = saveWholeImage;
    }

    static ExportSettings defaultSettings() {
        return new ExportSettings();
    }

    void copyInto(ExportSettings other) {
        this.exportDirectory = other.exportDirectory;
        this.exportTSV = other.exportTSV;
        this.exportJSON = other.exportJSON;
        this.saveAnnotatedImage = other.saveAnnotatedImage;
        this.saveWholeImage = other.saveWholeImage;
    }

    /**
     * Get the directory where to export measurements.
     *
     * @return the export directory
     */
    public Optional<Path> getExportDirectory() {
        return exportDirectory;
    }

    /**
     * Set the export directory
     *
     * The path must be absolute, exist and point to an directory.
     *
     * @param exportDirectory the export dirctory
     * @throws IllegalArgumentException if the path is invalid
     */
    public void setExportDirectory(Optional<Path> exportDirectory) throws IllegalArgumentException {
        // Validation
        exportDirectory.ifPresent(ed -> {
            if (!ed.isAbsolute()) {
                throw new IllegalArgumentException("Export directory must be an absolute path");
            }
            File file = ed.toFile();
            if (!file.exists()) {
                throw new IllegalArgumentException("Export directory does not exist");
            }
            if (!file.isDirectory()) {
                String msg = "Export directory path exists but does not point to a directory";
                throw new IllegalArgumentException(msg);
            }
        });
        this.exportDirectory = exportDirectory;
    }

    /**
     * Returns if the griddedImages are to be generated when measuring.
     *
     * @return boolean on griddedImages use.
     */
    public boolean isSaveAnnotatedImage() {
        return saveAnnotatedImage;
    }

    public boolean isExportTSV() {
        return exportTSV;
    }

    public void setExportTSV(boolean exportTSV) {
        this.exportTSV = exportTSV;
    }

    public boolean isExportJSON() {
        return exportJSON;
    }

    public void setExportJSON(boolean exportJSON) {
        this.exportJSON = exportJSON;
    }

    /**
     * Sets if griddedImages should be generated when measuring.
     * If false no image will be generated.
     *
     * @param saveAnnotatedImage generation of gridded images.
     */
    public void setSaveAnnotatedImage(boolean saveAnnotatedImage) {
        this.saveAnnotatedImage = saveAnnotatedImage;
    }

    public boolean isSaveWholeImage() {
        return saveWholeImage;
    }

    public void setSaveWholeImage(boolean saveWholeImage) {
        this.saveWholeImage = saveWholeImage;
    }

    static ExportSettings fromJson(JsonObject json) throws JsonParseException {
        try {
            ExportSettings exportSettings = new ExportSettings(
                    json.getBoolean("export_tsv"),
                    json.getBoolean("export_json"),
                    json.getBoolean("save_annotated_image"),
                    json.getBoolean("save_whole_image"));
            if (json.containsKey("export_directory")) {
                Path exportDirectory = Paths.get(json.getString("export_directory"));
                exportSettings.setExportDirectory(Optional.of(exportDirectory));
            }
            return exportSettings;
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid export directory", e);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        } catch (NullPointerException e) {
            throw new JsonParseException("Expected JSON key is missing", e);
        }
    }

    @Override
    public JsonStructure asJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        exportDirectory.ifPresent(path -> json.add("export_directory", path.toString()));
        json.add("export_tsv", exportTSV);
        json.add("export_json", exportJSON);
        json.add("save_annotated_image", saveAnnotatedImage);
        json.add("save_whole_image", saveWholeImage);
        return json.build();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exportDirectory == null) ? 0 : exportDirectory.hashCode());
        result = prime * result + (exportTSV ? 1231 : 1237);
        result = prime * result + (exportJSON ? 1231 : 1237);
        result = prime * result + (saveAnnotatedImage ? 1231 : 1237);
        result = prime * result + (saveWholeImage ? 1231 : 1237);
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
        ExportSettings other = (ExportSettings) obj;
        if (exportDirectory == null) {
            if (other.exportDirectory != null)
                return false;
        } else if (!exportDirectory.equals(other.exportDirectory))
            return false;
        if (exportTSV != other.exportTSV)
            return false;
        if (exportJSON != other.exportJSON)
            return false;
        if (saveAnnotatedImage != other.saveAnnotatedImage)
            return false;
        if (saveWholeImage != other.saveWholeImage)
            return false;
        return true;
    }

}
