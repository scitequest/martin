package com.scitequest.martin.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scitequest.martin.utils.DoubleStatistics;

public final class DataStatistics {

    private static final String TSV_HEADER = "row\tcol"
            + "\tadjusted_average\tadjusted_average_std_deviation"
            + "\trelative_adjusted_average_std_deviation"
            + "\tnormalized_average\tnormalized_average_std_deviation"
            + "\trelative_normalized_average_std_deviation";

    /** The maxium value of a number in percent. */
    private static final double MAX_PERCENT = 100.0;

    /** The statistics of each datapoint. */
    @JsonProperty("values")
    private final List<DatapointStatistics> spotStatistics;

    @JsonCreator
    private DataStatistics(@JsonProperty("values") List<DatapointStatistics> spotStatistics) {
        this.spotStatistics = spotStatistics;
    }

    /**
     * Does analysis on a given Dataset and returns a new instance of
     * DataStatistics.
     *
     * Analysis includes the following:
     * Averaging across all spotFields
     * Normalization of Average
     * Raw standard deviation between spotFields - absolute and relative
     * Normalized standard deviation between spotFields - absolute and relative
     *
     * @param data collection of Datapoints that shall be analyzed
     * @return the data statistics
     */
    public static DataStatistics analyze(Data data) {
        var transposed = data.getTransposedValues();
        List<DatapointStatistics> stats = transposed.entrySet().stream()
                // FIXME: The transposed Map should be a pre-sorted list so we spare the sorting
                .sorted((e1, e2) -> {
                    int row1 = e1.getKey().get(0);
                    int row2 = e2.getKey().get(0);
                    int ret = Integer.compare(row1, row2);
                    if (ret != 0) {
                        return ret;
                    }
                    int col1 = e1.getKey().get(1);
                    int col2 = e2.getKey().get(1);
                    return Integer.compare(col1, col2);
                })
                .map(entry -> {
                    int row = entry.getKey().get(0);
                    int col = entry.getKey().get(1);
                    List<Datapoint> values = entry.getValue();

                    DoubleStatistics adjusted = values.stream()
                            .map(Datapoint::getMeanMinusMin)
                            .collect(DoubleStatistics.collector());
                    double relStdDevRawAvg = adjusted.getStandardDeviation() * MAX_PERCENT
                            / adjusted.getAverage();
                    if (Double.isNaN(relStdDevRawAvg)) {
                        relStdDevRawAvg = 0.0;
                    }
                    DoubleStatistics normalized = values.stream()
                            .map(Datapoint::getNormalizedMean)
                            .collect(DoubleStatistics.collector());
                    double relStdDevNormAvg = normalized.getStandardDeviation() * MAX_PERCENT
                            / normalized.getAverage();
                    if (Double.isNaN(relStdDevNormAvg)) {
                        relStdDevNormAvg = 0.0;
                    }

                    DatapointStatistics datapointStatistics = DatapointStatistics.of(
                            row, col,
                            adjusted.getAverage(), adjusted.getStandardDeviation(),
                            relStdDevRawAvg,
                            normalized.getAverage(), normalized.getStandardDeviation(),
                            relStdDevNormAvg);
                    return datapointStatistics;
                })
                .collect(Collectors.toList());
        return new DataStatistics(stats);
    }

    /**
     * Get the statistics of all datapoints.
     *
     * @return an unmodifiable list of the statistics
     */
    public List<DatapointStatistics> getSpotStatistics() {
        return Collections.unmodifiableList(spotStatistics);
    }

    /**
     * Get the data formatted as Tab Separated Values (TSV).
     *
     * @return the data as TSV representation
     */
    public String asTsv() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append(TSV_HEADER).append("\n");

        // Values
        for (DatapointStatistics dpStats : spotStatistics) {
            sb.append(dpStats.getRow()).append("\t");
            sb.append(dpStats.getColumn()).append("\t");
            sb.append(dpStats.getRawAvg()).append("\t");
            sb.append(dpStats.getStdDevRawAvg()).append("\t");
            sb.append(dpStats.getRelStdDevRawAvg()).append("\t");
            sb.append(dpStats.getRawAvgNormalized()).append("\t");
            sb.append(dpStats.getStdDevNormAvg()).append("\t");
            sb.append(dpStats.getRelStdDevNormAvg()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Parse data statistics from a given TSV formatted string.
     *
     * @param s the string
     * @return the data statistics
     * @throws IllegalArgumentException if the TSV is malformed
     */
    public static DataStatistics fromTsv(String s) throws IllegalArgumentException {
        // Create an iterator over each line
        var iter = s.lines().iterator();
        // Check for the presence of a correct TSV header
        if (!iter.hasNext() || !iter.next().equals(TSV_HEADER)) {
            throw new IllegalArgumentException("Missing TSV header");
        }

        // Parse each line into a datapoint
        List<DatapointStatistics> values = new ArrayList<>();
        while (iter.hasNext()) {
            // Split line into each value as a string
            String[] splits = iter.next().split("\t");
            if (splits.length != 8) {
                throw new IllegalArgumentException("Improper TSV entry");
            }

            // Parse each value
            int row = Integer.parseInt(splits[0]);
            int col = Integer.parseInt(splits[1]);
            double rawAvg = Double.parseDouble(splits[2]);
            double stdDevRawAvg = Double.parseDouble(splits[3]);
            double relStdDevRawAvg = Double.parseDouble(splits[4]);
            double rawAvgNormalized = Double.parseDouble(splits[5]);
            double stdDevNormAvg = Double.parseDouble(splits[6]);
            double relStdDevNormAvg = Double.parseDouble(splits[7]);

            // Create datapoint
            DatapointStatistics ds = DatapointStatistics.of(row, col,
                    rawAvg, stdDevRawAvg, relStdDevRawAvg,
                    rawAvgNormalized, stdDevNormAvg, relStdDevNormAvg);
            values.add(ds);
        }

        return new DataStatistics(values);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((spotStatistics == null) ? 0 : spotStatistics.hashCode());
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
        DataStatistics other = (DataStatistics) obj;
        if (spotStatistics == null) {
            if (other.spotStatistics != null)
                return false;
        } else if (!spotStatistics.equals(other.spotStatistics))
            return false;
        return true;
    }

    @SuppressWarnings("checkstyle:NeedBraces")
    public boolean equalsEpsilon(Object obj, double eps) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataStatistics other = (DataStatistics) obj;
        if (spotStatistics == null || other.spotStatistics == null)
            return false;
        if (spotStatistics.size() != other.spotStatistics.size())
            return false;
        for (int i = 0; i < spotStatistics.size(); i++) {
            DatapointStatistics ds1 = spotStatistics.get(i);
            DatapointStatistics ds2 = other.spotStatistics.get(i);
            if (ds1 == null || ds2 == null)
                return false;
            if (!ds1.equalsEpsilon(ds2, eps))
                return false;
        }
        return true;
    }
}
