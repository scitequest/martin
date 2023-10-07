package com.scitequest.martin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.scitequest.martin.export.Data;
import com.scitequest.martin.export.Image;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.export.Measurepoint;
import com.scitequest.martin.export.Metadata;
import com.scitequest.martin.export.Patient;
import com.scitequest.martin.settings.Settings;

import ij.IJ;
import net.imagej.ImageJ;
import net.imagej.patcher.LegacyInjector;

public class MeasurementConsistencyIT {

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
    public void test() throws IOException, SecurityException, JsonParseException {
        String imagePath = "src/test/resources/img/22-06-02 - 60sec - P.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);

        Data data1 = control.doMeasure();
        Data data2 = control.doMeasure();

        assertEquals(data1.getValues().size(), data2.getValues().size());

        for (int i = 0; i < data1.getValues().size(); i++) {
            Measurepoint datapoint1 = data1.getValues().get(i).getMeasurePoint();
            Measurepoint datapoint2 = data2.getValues().get(i).getMeasurePoint();
            assertEquals(datapoint1, datapoint2);
        }

        assertEquals(data1, data2);
    }

    /**
     * Tests, whether using adaptive filtering changes measurement output.
     *
     * Note: This is only partly testable since the call to the GUI is never made
     * due to running in headless.
     */
    @Test
    public void testFilteringDoesNotAffectMeasuring() throws SecurityException, IOException, JsonParseException {
        String imagePath = "src/test/resources/img/22-06-02 - 60sec - P.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);

        control.setFilterEnabled(false);
        Data first = control.doMeasure();
        control.setFilterEnabled(true);
        Data second = control.doMeasure();
        control.setFilterEnabled(false);
        Data third = control.doMeasure();

        assertEquals(first, second);
        assertEquals(first, third);
        assertEquals(second, third);
    }

    @Test
    public void testMeasurementWithSpotfieldOutsideOfImage() throws SecurityException, IOException, JsonParseException {
        Settings settings = Settings.load(settingsPath);
        // Prevents spamming of the console as only 4 measurepoints exist.
        settings.getMaskSettings().setLastMeasurePointIndex(1);
        // Disable background subtraction
        settings.getMeasurementSettings().setSubtractBackground(false);
        settings.store();
        settings.save();

        String imagePath = "src/test/resources/img/BS6 - 60sec - B - 1.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);
        control.moveSlide(-10000, -10000);
        Data data = control.doMeasure();
        assertTrue(data.getValues().stream().allMatch(dp -> {
            Measurepoint mp = dp.getMeasurePoint();
            return mp.getMin() == 0.0
                    && mp.getMean() == 0.0
                    && mp.getMax() == 0.0
                    && dp.getMeanMinusMin() == 0.0
                    && dp.getNormalizedMean() == 0.0;
        }));
    }

    @Test
    public void testMeasureFollowedByValidation()
            throws SecurityException, IOException, JsonParseException {
        String imagePath = "src/test/resources/img/22-06-02 - 60sec - N.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);
        control.setActiveMask(Paths.get("src/test/resources/20220602_N_mask.json"));
        control.repositionSlide();
        control.moveSlide(504, 973);
        control.rotateSlide(358.2);

        File measurementFolder = folder.newFolder();

        Metadata dummyMetadata = Metadata.of(
                ZonedDateTime.now(), Optional.empty(),
                Patient.of("id", "name", Set.of()),
                Image.of(ZonedDateTime.now(), "imager",
                        1, Duration.ofSeconds(60)),
                List.of());
        control.doMeasureAndExport(measurementFolder.toPath(), dummyMetadata);
        assertTrue(control.checkIntegrity(measurementFolder.toPath()).isSuccess());
    }

    @Test
    public void testMeasurementAreSequentialSpots()
            throws SecurityException, IOException, JsonParseException {
        Settings settings = Settings.load(settingsPath);
        // Disable invert LUT for the checkerboard.
        settings.getMeasurementSettings().setInvertLut(false);
        // Load and activate the checker mask.
        settings.getMaskSettings().importMask(Paths.get("src/test/resources/checker.json"));
        int numMasks = settings.getMaskSettings().getNumberOfMasks();
        settings.getMaskSettings().setLastUsedMaskIndex(numMasks - 1);
        // Set the last measurepoint index to the maximum.
        settings.getMaskSettings().setLastUsedMaskLastMeasurePointIndexToMax();
        settings.store();
        settings.save();

        String imagePath = "src/test/resources/img/checker.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);

        Data data = control.doMeasure();
        assertCheckerImgDataSymmetric(data);
    }

    @Test
    public void testLastSpotfieldIndexNotAffectingMeasurementPositions()
            throws SecurityException, IOException, JsonParseException {
        Settings settings = Settings.load(settingsPath);
        // Disable invert LUT for the checkerboard.
        settings.getMeasurementSettings().setInvertLut(false);
        // Load and activate the checker mask.
        settings.getMaskSettings().importMask(Paths.get("src/test/resources/checker.json"));
        int numMasks = settings.getMaskSettings().getNumberOfMasks();
        settings.getMaskSettings().setLastUsedMaskIndex(numMasks - 1);
        // Set the last measurepoint index to less than the maximum.
        settings.getMaskSettings().setLastMeasurePointIndex(7);
        settings.store();
        settings.save();

        String imagePath = "src/test/resources/img/checker.tif";
        Control control = Control.headless(ij, IJ.openImage(imagePath), settingsPath);

        Data data = control.doMeasure();
        assertCheckerImgDataSymmetric(data);
    }

    /**
     * The checker test image contains 24 gray patches with values increasing each
     * by 10. Thus, for a mask with 12 measurements per spotfield the values of the
     * right spotfield must be the value for the respective measurepoint of the left
     * spotfield + 120. This way we are able to test that measurepoints are measured
     * sequentially. More importantly, it is possible to test that if the last
     * measurepoint index is less than the maximum the measurements are not shifted
     * but properly skipped.
     *
     * @param data the data to assert for symmetry
     */
    private static void assertCheckerImgDataSymmetric(Data data) {
        int numSpotsPerSpotfield = data.getValues().size() / 2;
        for (int i = 0; i < numSpotsPerSpotfield; i++) {
            double leftMeasurepointMean = data.getValues()
                    .get(i)
                    .getMeasurePoint()
                    .getMean();
            double rightMeasurepointMean = data.getValues()
                    .get(i + numSpotsPerSpotfield)
                    .getMeasurePoint()
                    .getMean();
            assertEquals(leftMeasurepointMean, rightMeasurepointMean - 120, 1e-4);
        }
    }
}
