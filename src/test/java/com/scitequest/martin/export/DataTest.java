package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.scitequest.martin.Const;

public class DataTest {

    @Test
    public void dataEquals() {
        Data s1 = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 1)));
        Data s2 = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 1)));
        Data s3 = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 2, 4.5, 2.2, 6.3), 2.0, 0.5)));

        assertTrue(s1.equals(s2));
        assertEquals(s1.hashCode(), s2.hashCode());

        assertFalse(s1.equals(s3));
        assertNotEquals(s1.hashCode(), s3.hashCode());

        assertFalse(s2.equals(s3));
        assertNotEquals(s2.hashCode(), s3.hashCode());
    }

    /**
     * Check if the calculation of meanMinusMin and normalizedMean are correct.
     * The implementation should match min-max feature scaling.
     */
    @Test
    public void testFromMeasurepoints() {
        double epsilon = 1.0e-6;
        var measurepoints = List.of(
                Measurepoint.of(0, 0, 0, 0, 10.0, 1.0, 0),
                Measurepoint.of(0, 0, 1, 0, 10.0, 2.0, 0),
                Measurepoint.of(0, 1, 0, 0, 10.0, 3.0, 0),
                Measurepoint.of(1, 0, 0, 0, 10.0, 2.0, 0),
                Measurepoint.of(1, 0, 1, 0, 10.0, 3.0, 0),
                Measurepoint.of(1, 1, 0, 0, 10.0, 4.0, 0));
        Data data = Data.fromMeasurepoints(measurepoints);

        // spotfield1MinMean = 1.0;
        // spotfield2MinMean = 2.0;
        assertEquals(0.0, data.getValues().get(0).getMeanMinusMin(), epsilon);
        assertEquals(1.0, data.getValues().get(1).getMeanMinusMin(), epsilon);
        assertEquals(2.0, data.getValues().get(2).getMeanMinusMin(), epsilon);
        assertEquals(0.0, data.getValues().get(3).getMeanMinusMin(), epsilon);
        assertEquals(1.0, data.getValues().get(4).getMeanMinusMin(), epsilon);
        assertEquals(2.0, data.getValues().get(5).getMeanMinusMin(), epsilon);

        assertEquals(0.0, data.getValues().get(0).getNormalizedMean(), epsilon);
        assertEquals(0.5, data.getValues().get(1).getNormalizedMean(), epsilon);
        assertEquals(1.0, data.getValues().get(2).getNormalizedMean(), epsilon);
        assertEquals(0.0, data.getValues().get(3).getNormalizedMean(), epsilon);
        assertEquals(0.5, data.getValues().get(4).getNormalizedMean(), epsilon);
        assertEquals(1.0, data.getValues().get(5).getNormalizedMean(), epsilon);
    }

    @Test
    public void testIsNan() {
        var measurepoints = List.of(
                Measurepoint.of(0, 0, 0, 0.0, 0.0, 0.0, 0));
        Data data = Data.fromMeasurepoints(measurepoints);
        for (Datapoint dp : data.getValues()) {
            assertFalse(Double.isNaN(dp.getMeanMinusMin()));
            assertFalse(Double.isNaN(dp.getNormalizedMean()));
        }
    }

    @Test
    public void testIsEmpty() {
        Data empty = Data.of(List.of());
        assertTrue(empty.isEmpty());

        Data notEmpty = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 0, 0, 0, 0), 0, 0)));
        assertFalse(notEmpty.isEmpty());
    }

    @Test
    public void testUnsortedDataset() {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 1, 1, 0, 10.0, 4.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(1, 0, 0, 0, 10.0, 5.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(0, 1, 0, 0, 10.0, 3.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(0, 0, 1, 0, 10.0, 2.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(1, 1, 0, 0, 10.0, 7.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(0, 0, 0, 0, 10.0, 1.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(1, 1, 1, 0, 10.0, 8.0, 0), 0, 0),
                Datapoint.of(Measurepoint.of(1, 0, 1, 0, 10.0, 6.0, 0), 0, 0));
        Data data = Data.of(values);

        List<Double> originalMeanOrder = data.getValues().stream()
                .map(Datapoint::getMeasurePoint)
                .map(Measurepoint::getMean)
                .collect(Collectors.toList());
        List<Double> sortedMeanOrder = originalMeanOrder.stream()
                .sorted()
                .collect(Collectors.toList());
        assertEquals(sortedMeanOrder, originalMeanOrder);

        Data dataFromMeasurepoint = Data.fromMeasurepoints(values.stream()
                .map(Datapoint::getMeasurePoint)
                .collect(Collectors.toList()));
        originalMeanOrder = dataFromMeasurepoint.getValues().stream()
                .map(Datapoint::getMeasurePoint)
                .map(Measurepoint::getMean)
                .collect(Collectors.toList());
        assertEquals(sortedMeanOrder, originalMeanOrder);
    }

    @Test
    public void testRejectsDuplicateIds() {
        // Spot/row/column must be distinct
        assertThrows(IllegalArgumentException.class, () -> {
            Data.of(List.of(
                    Datapoint.of(Measurepoint.of(1, 0, 1, 0, 10.0, 4.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(1, 0, 1, 0, 10.0, 5.0, 0), 0, 0)));
        });
    }

    @Test
    public void testUnbalancedSpotfields() {
        // Number of elements within spotfields are not equals
        // oo | oo | o
        assertThrows(IllegalArgumentException.class, () -> {
            Data.of(List.of(
                    Datapoint.of(Measurepoint.of(0, 0, 0, 0, 10.0, 1.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(0, 0, 1, 0, 10.0, 2.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(1, 0, 0, 0, 10.0, 5.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(1, 0, 1, 0, 10.0, 5.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(2, 0, 1, 0, 10.0, 5.0, 0), 0, 0)));
        });
    }

    @Test
    public void testSpotfieldsSameShape() {
        // Arrangement within spotfield is not the same for all spotfields
        // oo | oo
        // o- | -o
        assertThrows(IllegalArgumentException.class, () -> {
            Data.of(List.of(
                    Datapoint.of(Measurepoint.of(0, 0, 0, 0, 10.0, 1.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(0, 0, 1, 0, 10.0, 2.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(0, 1, 0, 0, 10.0, 2.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(1, 0, 0, 0, 10.0, 5.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(1, 0, 1, 0, 10.0, 5.0, 0), 0, 0),
                    Datapoint.of(Measurepoint.of(1, 1, 1, 0, 10.0, 5.0, 0), 0, 0)));
        });
    }

    @Test
    public void testDeserializeJsonData()
            throws StreamReadException, DatabindException, IOException {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3), 2.4, 0.8),
                Datapoint.of(Measurepoint.of(0, 0, 1, 1.2, 3.7, 2.5, 7.0), 2.5, 1.0),
                Datapoint.of(Measurepoint.of(1, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 0.7),
                Datapoint.of(Measurepoint.of(1, 0, 1, 2, 4.5, 2.25, 6.3), 2.25, 0.7));
        Data data = Data.of(values);

        String path = "src/test/resources/export/data.json";
        Data expected = Const.mapper.readValue(new FileReader(path), Data.class);
        assertEquals(expected, data);
    }

    @Test
    public void testExportTsvData() throws IOException {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3), 2.4, 0.8),
                Datapoint.of(Measurepoint.of(0, 0, 1, 1.2, 3.7, 2.5, 7.0), 2.5, 1),
                Datapoint.of(Measurepoint.of(1, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 0.7),
                Datapoint.of(Measurepoint.of(1, 0, 1, 2, 4.5, 2.25, 6.3), 2.25, 0.7));
        Data data = Data.of(values);

        Path path = Path.of("src/test/resources/export/data.tsv");
        String expected = Files.readString(path).replace("\r\n", "\n");
        assertEquals(expected, data.asTsv());
    }

    @Test
    public void testImportTsvData() {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 1.1, 3.5, 2.4, 6.3), 2.4, 0.8),
                Datapoint.of(Measurepoint.of(0, 0, 1, 1.2, 3.7, 2.5, 7.0), 2.5, 1),
                Datapoint.of(Measurepoint.of(1, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 0.7),
                Datapoint.of(Measurepoint.of(1, 0, 1, 2, 4.5, 2.25, 6.3), 2.25, 0.7));
        Data data = Data.of(values);

        assertEquals(data, Data.fromTsv(data.asTsv()));
    }

    @Test
    public void testImportInvalidTsvData() {
        assertThrows(IllegalArgumentException.class, () -> Data.fromTsv("#"));
        assertThrows(IllegalArgumentException.class, () -> Data.fromTsv(
                "spot	row	col	min	max	mean	std_deviation	mean_minus_min	normalized_mean\n0	0	0$	0.0	0.0	0.0	0.0	0.0	0.0"));
    }
}
