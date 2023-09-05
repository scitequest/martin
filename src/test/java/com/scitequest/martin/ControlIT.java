package com.scitequest.martin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.settings.Settings;

import ij.IJ;
import net.imagej.ImageJ;
import net.imagej.patcher.LegacyInjector;

public class ControlIT {

    static {
        LegacyInjector.preinit();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ImageJ ij;
    private Path settingsPath;

    @Before
    public void setUp() throws IOException {
        ij = new ImageJ();
        settingsPath = folder.newFile().toPath();
        Settings.defaultSettings().save(settingsPath);
    }

    @After
    public void tearDown() {
        if (ij != null) {
            ij.dispose();
        }
        settingsPath = null;
    }

    @Test
    public void testImageClosed() throws SecurityException, IOException, JsonParseException {
        String imagePath = "src/test/resources/img/BS6 - 60sec - B - 1.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);
        assertTrue(control.isImageLoaded());
        control.imageClosed();
        assertTrue(!control.isImageLoaded());
    }

    @Test
    public void testGetDateTimeOriginalFromFileValidFile() {
        File file = new File("src/test/resources/img/22-06-02 - 60sec - N.tif");
        Optional<LocalDateTime> datetime = Control.getDateTimeOriginalFromFile(file);
        assertTrue(datetime.isPresent());
        assertEquals(LocalDateTime.of(2022, 6, 4, 17, 23), datetime.get());
    }

    @Test
    public void testGetDateTimeOriginalFromFileGarbageFile() {
        File file = new File("pom.xml");
        Optional<LocalDateTime> datetime = Control.getDateTimeOriginalFromFile(file);
        assertTrue(datetime.isEmpty());
    }
}
