package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.scitequest.martin.Const;

public class DataStatisticsTest {

    @Test
    public void dataEquals() {
        Data s1 = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 1)));
        Data s2 = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 2, 4.5, 2.25, 6.3), 2.25, 1)));
        Data s3 = Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 2, 4.5, 2.2, 6.3), 2.0, 0.5)));

        DataStatistics d1 = DataStatistics.analyze(s1);
        DataStatistics d2 = DataStatistics.analyze(s2);
        DataStatistics d3 = DataStatistics.analyze(s3);
        DataStatistics d4 = DataStatistics.analyze(s1);

        assertTrue(d1.equals(d2));
        assertTrue(d1.equals(d4));
        assertFalse(d1.equals(d3));
        assertFalse(d2.equals(d3));
    }

    @Test
    public void analysisConsistency() {
        List<Datapoint> values = new ArrayList<>();
        // SpotField A
        Measurepoint m1 = Measurepoint.of(0, 0, 0, 1, 15, 10, 1);
        Datapoint d1 = Datapoint.of(m1, 10, 1.0);
        values.add(d1); // Spot 1

        Measurepoint m2 = Measurepoint.of(0, 0, 1, 2, 10, 5, 1);
        Datapoint d2 = Datapoint.of(m2, 5, 0.5);
        values.add(d2); // Spot 2

        // SpotField B
        Measurepoint m3 = Measurepoint.of(1, 0, 0, 2, 16, 12, 1);
        Datapoint d3 = Datapoint.of(m3, 10, 1.0);
        values.add(d3); // Spot 1

        Measurepoint m4 = Measurepoint.of(1, 0, 1, 2, 10, 8, 1);
        Datapoint d4 = Datapoint.of(m4, 6, 0.6);
        values.add(d4); // Spot 2

        Data dSet = Data.of(values);

        DataStatistics dStat = DataStatistics.analyze(dSet);
        DatapointStatistics sStat = dStat.getSpotStatistics().get(0);

        double allowedDeviation = 0.001;
        assertEquals(sStat.getRawAvg(), 10, allowedDeviation);
        assertEquals(sStat.getRawAvgNormalized(), 1, allowedDeviation);
        // No deviation since both spots have a meanMinusMin of 10
        assertEquals(sStat.getStdDevRawAvg(), 0, allowedDeviation);
        assertEquals(sStat.getRelStdDevRawAvg(), 0, allowedDeviation);
        assertEquals(sStat.getStdDevNormAvg(), 0, allowedDeviation);
        assertEquals(sStat.getRelStdDevNormAvg(), 0, allowedDeviation);

        sStat = dStat.getSpotStatistics().get(1);

        assertEquals(sStat.getRawAvg(), 5.5, allowedDeviation);
        assertEquals(sStat.getRawAvgNormalized(), 0.55, allowedDeviation);
        assertEquals(sStat.getStdDevRawAvg(), 0.5, allowedDeviation);
        assertEquals(sStat.getRelStdDevRawAvg(), 9.0909090909090, allowedDeviation);
        assertEquals(sStat.getStdDevNormAvg(), 0.05, allowedDeviation);
        assertEquals(sStat.getRelStdDevNormAvg(), 9.0909090909090, allowedDeviation);
    }

    @Test
    public void testEmptyDataset() {
        DataStatistics dataStatistics = DataStatistics.analyze(Data.of(List.of()));
        assertEquals(0, dataStatistics.getSpotStatistics().size());
    }

    @Test
    public void testAverageOf0NotNaN() {
        DataStatistics dataStatistics = DataStatistics.analyze(Data.of(List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 0, 0, 0, 0), 0, 0))));
        for (DatapointStatistics dps : dataStatistics.getSpotStatistics()) {
            assertFalse(Double.isNaN(dps.getRawAvg()));
            assertFalse(Double.isNaN(dps.getStdDevRawAvg()));
            assertFalse(Double.isNaN(dps.getRelStdDevRawAvg()));
            assertFalse(Double.isNaN(dps.getRawAvgNormalized()));
            assertFalse(Double.isNaN(dps.getStdDevNormAvg()));
            assertFalse(Double.isNaN(dps.getRelStdDevNormAvg()));
        }
    }

    @Test
    public void testTsvExport() throws IOException {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 0.0, 0.0, 0.0, 0.0), 10.0, 1.0),
                Datapoint.of(Measurepoint.of(0, 0, 1, 0.0, 0.0, 0.0, 0.0), 5.0, 0.5),
                Datapoint.of(Measurepoint.of(1, 0, 0, 0.0, 0.0, 0.0, 0.0), 10.0, 1.0),
                Datapoint.of(Measurepoint.of(1, 0, 1, 0.0, 0.0, 0.0, 0.0), 6.0, 0.6));
        Data data = Data.of(values);
        DataStatistics dataStatistics = DataStatistics.analyze(data);

        Path path = Path.of("src/test/resources/export/data_statistics.tsv");
        String expected = Files.readString(path).replace("\r\n", "\n");
        assertEquals(expected, dataStatistics.asTsv());
    }

    @Test
    public void testTsvImport() {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 0.0, 0.0, 0.0, 0.0), 10.0, 1.0),
                Datapoint.of(Measurepoint.of(0, 0, 1, 0.0, 0.0, 0.0, 0.0), 5.0, 0.5),
                Datapoint.of(Measurepoint.of(1, 0, 0, 0.0, 0.0, 0.0, 0.0), 10.0, 1.0),
                Datapoint.of(Measurepoint.of(1, 0, 1, 0.0, 0.0, 0.0, 0.0), 6.0, 0.6));
        Data data = Data.of(values);
        DataStatistics dataStatistics = DataStatistics.analyze(data);

        assertEquals(dataStatistics, DataStatistics.fromTsv(dataStatistics.asTsv()));
    }

    @Test
    public void testJsonExport() throws StreamReadException, DatabindException, IOException {
        List<Datapoint> values = List.of(
                Datapoint.of(Measurepoint.of(0, 0, 0, 0.0, 0.0, 0.0, 0.0), 10.0, 1.0),
                Datapoint.of(Measurepoint.of(0, 0, 1, 0.0, 0.0, 0.0, 0.0), 5.0, 0.5),
                Datapoint.of(Measurepoint.of(1, 0, 0, 0.0, 0.0, 0.0, 0.0), 10.0, 1.0),
                Datapoint.of(Measurepoint.of(1, 0, 1, 0.0, 0.0, 0.0, 0.0), 6.0, 0.6));
        Data data = Data.of(values);
        DataStatistics dataStatistics = DataStatistics.analyze(data);

        String path = "src/test/resources/export/data_statistics.json";
        DataStatistics expected = Const.mapper.readValue(
                new FileReader(path), DataStatistics.class);
        assertEquals(expected, dataStatistics);
    }
}
