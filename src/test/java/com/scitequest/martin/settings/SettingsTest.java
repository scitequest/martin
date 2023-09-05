package com.scitequest.martin.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.scitequest.martin.export.JsonParseException;

public class SettingsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Path copyDefaultSettingsFile() throws IOException {
        Path settingsJsonPath = Paths.get("src/test/resources/settings/settings.json");
        String settingsJson = Files.readString(settingsJsonPath);
        File file = folder.newFile();
        Files.writeString(file.toPath(), settingsJson, StandardOpenOption.WRITE);
        return file.toPath();
    }

    @Test
    public void saveSettings() throws IOException, JsonParseException {
        Path path = copyDefaultSettingsFile();
        Settings settings = Settings.load(path);
        // Assume invert LUT is set for the test
        assertTrue(settings.getMeasurementSettings().isInvertLut());
        // Change the setting temporarily
        settings.getMeasurementSettings().setInvertLut(false);
        assertFalse(settings.getMeasurementSettings().isInvertLut());
        // Aborting changes resets to last save
        settings.abort();
        assertTrue(settings.getMeasurementSettings().isInvertLut());
        // Actually store settings
        settings.getMeasurementSettings().setInvertLut(false);
        settings.store();
        settings.abort();
        assertFalse(settings.getMeasurementSettings().isInvertLut());
        // But they are not yet saved to disk
        assertTrue(Settings.load(path).getMeasurementSettings().isInvertLut());
        // Write actual changes to disk, alternative would be load
        settings.save(path);
        // Loading the same file again now has the settings
        assertFalse(Settings.load(path).getMeasurementSettings().isInvertLut());
    }

    @Test
    public void testMaskLoadAndSaveRoundtrip() throws IOException, JsonParseException {
        Settings compare = Settings.defaultSettings();
        MaskExt compMask = MaskExt.defaultSettings().withName("noDuplicate");
        compare.getMaskSettings().addMask(compMask);

        Settings trip = Settings.defaultSettings();

        Path path = folder.getRoot().toPath().resolve("mask.json");

        MaskSettings.exportMask(compMask, path);
        assertTrue(!Files.readString(path).isBlank());
        trip.getMaskSettings().importMask(path);

        assertEquals(compare.getMaskSettings().asJson().toString(),
                trip.getMaskSettings().asJson().toString());
        assertEquals(compare, trip);
    }

    @Test
    public void testExportDirectory() throws IOException {
        ExportSettings exportSettings = ExportSettings.defaultSettings();

        // Test unsetting
        exportSettings.setExportDirectory(Optional.empty());
        assertEquals(Optional.empty(), exportSettings.getExportDirectory());

        // Set to valid directory
        Path validExportDir = folder.getRoot().toPath().resolve("export-directory");
        Files.createDirectories(validExportDir);
        exportSettings.setExportDirectory(Optional.of(validExportDir));
        assertEquals(Optional.of(validExportDir), exportSettings.getExportDirectory());

        // Now set invalid paths
        // The directory does not yet exist
        Path nonexistentDir = folder.getRoot().toPath().resolve("verymuchdoesnotexist");
        Exception nonexistentDirEx = assertThrows(IllegalArgumentException.class,
                () -> exportSettings.setExportDirectory(Optional.of(nonexistentDir)));
        assertTrue(nonexistentDirEx.getMessage().contains("does not exist"));

        // Path exists but it points to a file
        Path existsButIsFile = folder.getRoot().toPath().resolve("file.txt");
        Files.writeString(existsButIsFile, "content", StandardOpenOption.CREATE);
        Exception existsButIsFileEx = assertThrows(IllegalArgumentException.class,
                () -> exportSettings.setExportDirectory(Optional.of(existsButIsFile)));
        assertTrue(existsButIsFileEx.getMessage().contains("does not point to a directory"));

        // The path is not absolute
        Path notAbsolutePath = validExportDir
                .getParent()
                .toAbsolutePath()
                .relativize(validExportDir);
        Exception notAbsolutePathEx = assertThrows(IllegalArgumentException.class,
                () -> exportSettings.setExportDirectory(Optional.of(notAbsolutePath)));
        assertTrue(notAbsolutePathEx.getMessage().contains("absolute"));

        // Check that we are still valid
        assertEquals(Optional.of(validExportDir), exportSettings.getExportDirectory());

        // Just try setting it to empty once again to be sure
        exportSettings.setExportDirectory(Optional.empty());
        assertEquals(Optional.empty(), exportSettings.getExportDirectory());
    }

    @Test
    public void testStoreAndLoadSaveAnnotatedImage() throws IOException, JsonParseException {
        Path path = copyDefaultSettingsFile();
        Settings settings = Settings.load(path);

        boolean saveAnnotatedImage = settings.getExportSettings().isSaveAnnotatedImage();
        settings.getExportSettings().setSaveAnnotatedImage(!saveAnnotatedImage);
        settings.store();
        settings.save();
        settings = Settings.load(path);

        assertEquals(!saveAnnotatedImage,
                settings.getExportSettings().isSaveAnnotatedImage());
    }

    @Test
    public void testLoadWrongVersion() throws IOException, JsonParseException {
        Path path = copyDefaultSettingsFile();
        String json;
        JsonParseException ex;

        json = Settings.defaultSettings().asJson().toString().replace("0.1.1", "0.2.0");
        Files.writeString(path, json, StandardCharsets.UTF_8);
        ex = assertThrows(JsonParseException.class, () -> Settings.load(path));
        assertTrue(ex.getMessage().contains("incompatible"));

        json = Settings.defaultSettings().asJson().toString().replace("0.1.1", "0.1.3");
        Files.writeString(path, json, StandardCharsets.UTF_8);
        Settings.load(path);

        json = Settings.defaultSettings().asJson().toString().replace("0.1.1", "0.0.9");
        Files.writeString(path, json, StandardCharsets.UTF_8);
        ex = assertThrows(JsonParseException.class, () -> Settings.load(path));
        assertTrue(ex.getMessage().contains("incompatible"));
    }

    @Test
    public void testInvalidatingReferencesBeforeAbort() {
        Settings settings = Settings.defaultSettings();
        MeasurementSettings measurementSettings = settings.getMeasurementSettings();
        boolean invertedInvertLut = !measurementSettings.isInvertLut();
        measurementSettings.setInvertLut(invertedInvertLut);

        settings.abort();

        assertNotEquals(invertedInvertLut, settings.getMeasurementSettings().isInvertLut());
        assertNotEquals(invertedInvertLut, measurementSettings.isInvertLut());
    }

    @Test
    public void testInvalidatingReferencesAfterAbort() {
        Settings settings = Settings.defaultSettings();
        MeasurementSettings measurementSettings = settings.getMeasurementSettings();
        boolean invertedInvertLut = !measurementSettings.isInvertLut();
        settings.abort();
        assertNotEquals(invertedInvertLut, measurementSettings.isInvertLut());
        measurementSettings.setInvertLut(invertedInvertLut);
        assertEquals(invertedInvertLut, measurementSettings.isInvertLut());
        assertEquals(settings.getMeasurementSettings().isInvertLut(),
                measurementSettings.isInvertLut());
        assertEquals(invertedInvertLut, settings.getMeasurementSettings().isInvertLut());
    }

    @Test
    public void testLoadEmptyFile() throws IOException {
        Path path = folder.newFile().toPath();
        assertThrows(JsonParseException.class, () -> Settings.load(path));
    }

    @Test
    public void testInvalidSettingsFile() throws IOException {
        Path path = Paths.get("src/test/resources/img/BS6 - 60sec - B - 1.tif");
        assertThrows(JsonParseException.class, () -> Settings.load(path));
    }

    @Test
    public void testIsDirty() {
        Settings settings = Settings.defaultSettings();

        assertFalse(settings.isDirty());
        boolean invertLut = settings.getMeasurementSettings().isInvertLut();
        settings.getMeasurementSettings().setInvertLut(!invertLut);
        assertTrue(settings.isDirty());
        settings.store();
        assertFalse(settings.isDirty());
    }

    @Test
    public void testIsSaved() throws IOException, JsonParseException {
        Path path = copyDefaultSettingsFile();
        Settings settings = Settings.load(path);

        assertTrue(settings.isSaved());
        boolean invertLut = settings.getMeasurementSettings().isInvertLut();
        settings.getMeasurementSettings().setInvertLut(!invertLut);
        assertFalse(settings.isSaved());
        settings.store();
        assertFalse(settings.isSaved());
        settings.save();
        assertTrue(settings.isSaved());
    }
}
