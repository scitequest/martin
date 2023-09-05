package com.scitequest.martin.export;

import java.time.Duration;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Represents the metadata of the incubation procedure and holds information
 * about the process.
 */
public final class Incubation implements JsonExportable {

    /** The solution that was used for incubation. */
    private final String solution;
    /** The concentration percentage of the of the shelf product. */
    private final Quantity stockConcentration;
    /** The final concentration of the solution used. */
    private final Quantity finalConcentration;
    /** The time taken for incubation. */
    private final Duration incubationTime;

    private Incubation(final String solution,
            final Quantity stockConcentrationPercent,
            final Quantity finalConcentrationPercent,
            final Duration incubationTime) {
        this.solution = solution;
        this.stockConcentration = stockConcentrationPercent;
        this.finalConcentration = finalConcentrationPercent;
        this.incubationTime = incubationTime;
    }

    /**
     * Create a new instance of this class using the supplied parameters.
     *
     * @param solution           The solution used, must not be empty or
     *                           consist solely of whitespace
     * @param stockConcentration The stock concentration
     * @param finalConcentration The final concentration
     * @param incubationTime     The incubation time as {@code Duration},
     *                           cannot be negative
     * @return An instance of this class.
     * @throws IllegalArgumentException If any of the above contracts is violated.
     */
    public static Incubation of(
            final String solution,
            final Quantity stockConcentration, final Quantity finalConcentration,
            final Duration incubationTime)
            throws IllegalArgumentException {
        if (solution == null || solution.isBlank()) {
            throw new IllegalArgumentException("Solution cannot be empty");
        }
        if (incubationTime.isNegative()) {
            throw new IllegalArgumentException("Incubation time cannot be negative");
        }

        return new Incubation(solution, stockConcentration,
                finalConcentration, incubationTime);
    }

    /**
     * Get the solution.
     *
     * @return the solution.
     */
    public String getSolution() {
        return solution;
    }

    /**
     * Get the stock concentation.
     *
     * @return the stock concentration.
     */
    public Quantity getStockConcentration() {
        return stockConcentration;
    }

    /**
     * Get the final concentration.
     *
     * @return the final concentration.
     */
    public Quantity getFinalConcentration() {
        return finalConcentration;
    }

    /**
     * Get the incubation time.
     *
     * @return the incubation time.
     */
    public Duration getIncubationTime() {
        return incubationTime;
    }

    @Override
    public JsonObject asJson() {
        JsonObject json = Json.createObjectBuilder()
                .add("solution", this.solution)
                .add("stock_concentration", this.stockConcentration.asJson())
                .add("final_concentration", this.finalConcentration.asJson())
                .add("incubation_time", this.incubationTime.toString())
                .build();
        return json;
    }

    public static Incubation fromJson(JsonObject obj) {
        String solution = obj.getString("solution");
        Quantity stockConcentrationPercent = Quantity.fromJson(
                obj.getJsonObject("stock_concentration"));
        Quantity finalConcentrationPercent = Quantity.fromJson(
                obj.getJsonObject("final_concentration"));
        Duration incubationTime = Duration.parse(obj.getString("incubation_time"));
        return Incubation.of(solution, stockConcentrationPercent,
                finalConcentrationPercent, incubationTime);
    }

    public Incubation getCopy() {
        // Not sure if there's a better way to copy an instance of Duration than doing
        // this.
        Duration copyDuration = Duration.ofNanos(incubationTime.getNano());

        return of(solution, stockConcentration, finalConcentration, copyDuration);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((solution == null) ? 0 : solution.hashCode());
        result = prime * result + ((stockConcentration == null) ? 0 : stockConcentration.hashCode());
        result = prime * result + ((finalConcentration == null) ? 0 : finalConcentration.hashCode());
        result = prime * result + ((incubationTime == null) ? 0 : incubationTime.hashCode());
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
        Incubation other = (Incubation) obj;
        if (solution == null) {
            if (other.solution != null)
                return false;
        } else if (!solution.equals(other.solution))
            return false;
        if (stockConcentration == null) {
            if (other.stockConcentration != null)
                return false;
        } else if (!stockConcentration.equals(other.stockConcentration))
            return false;
        if (finalConcentration == null) {
            if (other.finalConcentration != null)
                return false;
        } else if (!finalConcentration.equals(other.finalConcentration))
            return false;
        if (incubationTime == null) {
            if (other.incubationTime != null)
                return false;
        } else if (!incubationTime.equals(other.incubationTime))
            return false;
        return true;
    }
}
