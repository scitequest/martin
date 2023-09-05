package com.scitequest.martin.settings;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.scitequest.martin.export.JsonParseException;

public class MaskSettingsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testImportParametersVersion() throws IOException, JsonParseException {
        Path path = folder.newFile().toPath();
        JsonParseException ex;
        Settings settings = Settings.defaultSettings();
        MaskSettings maskSettings = settings.getMaskSettings();

        MaskSettings.exportMask(maskSettings.getMask(0), path);
        Files.writeString(path, Files.readString(path).replace("0.2.0", "0.3.0"));
        ex = assertThrows(JsonParseException.class,
                () -> maskSettings.importMask(path));
        assertTrue(ex.getMessage().contains("incompatible"));

        MaskSettings.exportMask(maskSettings.getMask(0), path);
        Files.writeString(path, Files.readString(path).replace("0.2.0", "0.1.9"));
        ex = assertThrows(JsonParseException.class,
                () -> maskSettings.importMask(path));
        assertTrue(ex.getMessage().contains("incompatible"));

        MaskExt exportMask = MaskExt.defaultSettings().withName("nonDuplicate");
        MaskSettings.exportMask(exportMask, path);
        Files.writeString(path, Files.readString(path).replace("0.2.0", "0.2.3"));
        maskSettings.importMask(path);
    }
}
