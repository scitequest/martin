package com.scitequest.martin.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scitequest.martin.utils.StatefulDistinct;

/**
 * Holds the datapoints of a measurement.
 *
 * Creating a dataset provides a couple assurances, namely:
 *
 * <ul>
 * <li>Datapoints are sorted by their spot, row, column</li>
 * <li>Duplicate datapoints (spot, row, column) are not permitted</li>
 * <li>Each spotfield must have the same "layout". This means each spotfield has
 * the same number of measurepoints and they are at the same position within the
 * spotfield.</li>
 * </ul>
 */
public final class Data {

    private static final String TSV_HEADER = "spot\trow\tcol\tmin\tmax\tmean\tstd_deviation"
            + "\tmean_minus_min\tnormalized_mean";

    public static final Comparator<Datapoint> DATAPOINT_COMPARATOR = (dp1, dp2) -> {
        return dp1.getMeasurePoint().compareTo(dp2.getMeasurePoint());
    };

    /** The measurement values. */
    @JsonProperty("values")
    private final List<Datapoint> values;
    /** The datapoints superimposed as a single spotfield. */
    @JsonIgnore
    private final Map<List<Integer>, List<Datapoint>> transposedValues;

    /**
     * Create a new measurement values holder.
     *
     * @param values           the measurement values
     * @param transposedValues the datapoints indexed by (row, col)
     */
    private Data(List<Datapoint> values,
            Map<List<Integer>, List<Datapoint>> transposedValues) {
        this.values = values;
        this.transposedValues = transposedValues;
    }

    /**
     * Return a new measurement values holder.
     *
     * @param values the measurement values
     * @return the created measurement values holder
     */
    @JsonCreator
    public static Data of(@JsonProperty("values") List<Datapoint> values) {
        var distinctor = StatefulDistinct.fromComparator(DATAPOINT_COMPARATOR);
        List<Datapoint> datapoints = values.stream()
                .sorted(DATAPOINT_COMPARATOR)
                .filter(dp -> !distinctor.isDuplicate(dp))
                .collect(Collectors.toList());

        if (datapoints.size() != values.size()) {
            throw new IllegalArgumentException("Provided measurepoints contained duplicates");
        }
        var transposed = calculateTransposedDatapoints(datapoints);
        ensureSpotfieldsHaveSameShape(transposed);

        return new Data(datapoints, transposed);
    }

    private static Map<List<Integer>, List<Datapoint>> calculateTransposedDatapoints(
            List<Datapoint> datapoints) {
        var transposedSpotfield = datapoints.stream()
                .collect(Collectors.groupingBy(dp -> {
                    Measurepoint mp = dp.getMeasurePoint();
                    return List.of(mp.getRow(), mp.getCol());
                }));
        return transposedSpotfield;
    }

    /**
     * Return a new measurement values holder calculating datapoint fields.
     *
     * @param values the measurement values
     * @return the created measurement values holder
     */
    public static Data fromMeasurepoints(List<Measurepoint> values) {
        Map<Integer, DoubleSummaryStatistics> spotfieldsStats = values.stream()
                .collect(Collectors.groupingBy(Measurepoint::getSpot,
                        Collectors.summarizingDouble(Measurepoint::getMean)));

        var distinctor = StatefulDistinct.fromComparator(DATAPOINT_COMPARATOR);
        var datapoints = values.stream()
                .sorted()
                .map(mp -> {
                    double spotfieldMinMean = spotfieldsStats.get(mp.getSpot()).getMin();
                    double spotfieldMaxMean = spotfieldsStats.get(mp.getSpot()).getMax();

                    double meanMinusMin = mp.getMean() - spotfieldMinMean;
                    // Normalize meanMinusMin by spotfieldMaxMean
                    double normalizedSpot = meanMinusMin / (spotfieldMaxMean - spotfieldMinMean);
                    if (Double.isNaN(normalizedSpot)) {
                        normalizedSpot = 0.0;
                    }
                    return Datapoint.of(mp, meanMinusMin, normalizedSpot);
                })
                .filter(dp -> !distinctor.isDuplicate(dp))
                .collect(Collectors.toList());

        if (datapoints.size() != values.size()) {
            throw new IllegalArgumentException("Provided measurepoints contained duplicates");
        }
        var transposed = calculateTransposedDatapoints(datapoints);
        ensureSpotfieldsHaveSameShape(transposed);

        return new Data(datapoints, transposed);
    }

    private static void ensureSpotfieldsHaveSameShape(
            Map<List<Integer>, List<Datapoint>> transposedDatapoints) {
        var counts = transposedDatapoints.values().stream()
                .map(list -> list.stream().count())
                .collect(Collectors.toList());
        var someElem = counts.stream().findAny();
        someElem.ifPresent(elem -> {
            if (!counts.stream().allMatch(elem::equals)) {
                throw new IllegalArgumentException("Spotfields have a differing shape");
            }
        });
    }

    /**
     * Check if the dataset contains any datapoints.
     *
     * @return true if the dataset is empty
     */
    @JsonIgnore
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Get the values as an unmodifiable list.
     *
     * @return the values
     */
    public List<Datapoint> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Get the transposed data representation.
     *
     * @return the transposed values
     */
    public Map<List<Integer>, List<Datapoint>> getTransposedValues() {
        return Collections.unmodifiableMap(transposedValues);
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
        for (Datapoint dp : values) {
            Measurepoint mp = dp.getMeasurePoint();
            sb.append(mp.getSpot()).append("\t");
            sb.append(mp.getRow()).append("\t");
            sb.append(mp.getCol()).append("\t");
            sb.append(mp.getMin()).append("\t");
            sb.append(mp.getMax()).append("\t");
            sb.append(mp.getMean()).append("\t");
            sb.append(mp.getStdDev()).append("\t");
            sb.append(dp.getMeanMinusMin()).append("\t");
            sb.append(dp.getNormalizedMean()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Parse data from a given TSV formatted string.
     *
     * @param s the string
     * @return the statistics
     * @throws IllegalArgumentException if the TSV is malformed
     */
    public static Data fromTsv(String s) throws IllegalArgumentException {
        // Create an iterator over each line
        var iter = s.lines().iterator();
        // Check for the presence of a correct TSV header
        if (!iter.hasNext() || !iter.next().equals(TSV_HEADER)) {
            throw new IllegalArgumentException("Missing TSV header");
        }

        // Parse each line into a datapoint
        List<Datapoint> values = new ArrayList<>();
        while (iter.hasNext()) {
            // Split line into each value as a string
            String[] splits = iter.next().split("\t");
            if (splits.length != 9) {
                throw new IllegalArgumentException("Improper TSV entry");
            }

            // Parse each value
            int spot = Integer.parseInt(splits[0]);
            int row = Integer.parseInt(splits[1]);
            int col = Integer.parseInt(splits[2]);
            double min = Double.parseDouble(splits[3]);
            double max = Double.parseDouble(splits[4]);
            double mean = Double.parseDouble(splits[5]);
            double stdDev = Double.parseDouble(splits[6]);
            double meanMinusMin = Double.parseDouble(splits[7]);
            double normalizedMean = Double.parseDouble(splits[8]);

            // Create datapoint
            Datapoint dp = Datapoint.of(
                    Measurepoint.of(spot, row, col, min, max, mean, stdDev),
                    meanMinusMin, normalizedMean);
            values.add(dp);
        }

        return Data.of(values);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((values == null) ? 0 : values.hashCode());
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
        Data other = (Data) obj;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
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
        Data other = (Data) obj;
        if (values == null || other.values == null)
            return false;
        if (values.size() != other.values.size())
            return false;
        for (int i = 0; i < values.size(); i++) {
            Datapoint dp1 = values.get(i);
            Datapoint dp2 = other.values.get(i);
            if (dp1 == null || dp2 == null)
                return false;
            if (!dp1.equalsEpsilon(dp2, eps))
                return false;
        }
        return true;
    }
}
