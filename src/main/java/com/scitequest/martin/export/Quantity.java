package com.scitequest.martin.export;

import javax.json.Json;
import javax.json.JsonObject;

public final class Quantity implements JsonExportable {

    /** Maximum percentage value. */
    private static final double MAX_PERCENT = 100.0;

    public enum Unit {
        PERCENT,
        MOL_PER_LITRE,
        MICRO_MOL_PER_LITRE,
        NANO_MOL_PER_LITRE;

        public String toSnakeCase() {
            return this.name().toLowerCase();
        }

        @Override
        public String toString() {
            switch (this) {
                case PERCENT:
                    return "%";
                case MOL_PER_LITRE:
                    return "mol/l";
                case MICRO_MOL_PER_LITRE:
                    return "µmol/l";
                case NANO_MOL_PER_LITRE:
                    return "nmol/l";
                default:
                    break;
            }
            return "";
        }
    }

    private final double value;
    private final Unit unit;

    private Quantity(double value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public static Quantity of(double value, Unit unit) throws IllegalArgumentException {
        switch (unit) {
            case PERCENT:
                if (value < 0 || value > MAX_PERCENT) {
                    throw new IllegalArgumentException(
                            "Percent quantity must be within [0.0; 100.0]");
                }
                break;
            case MOL_PER_LITRE:
                if (value < 0) {
                    throw new IllegalArgumentException("Molarity cannot be negative");
                }
                break;
            case MICRO_MOL_PER_LITRE:
                if (value < 0) {
                    throw new IllegalArgumentException("Molarity cannot be negative");
                }
                break;
            case NANO_MOL_PER_LITRE:
                if (value < 0) {
                    throw new IllegalArgumentException("Molarity cannot be negative");
                }
                break;
            default:
                throw new IllegalStateException("Unit is invalid");
        }
        return new Quantity(value, unit);
    }

    /**
     * Create a new quantity from a percentage value in the interval [0.0;100.0].
     *
     * @param value the value
     * @return the quantity
     * @throws IllegalArgumentException if the value is not in the required interval
     */
    public static Quantity fromPercent(double value) throws IllegalArgumentException {
        return new Quantity(value, Unit.PERCENT);
    }

    /**
     * Create a new quantity from a molarity in µmol/l which must be positive.
     *
     * @param value the value
     * @return the quantity
     * @throws IllegalArgumentException if the value is not positive
     */
    public static Quantity fromMicroMolPerLitre(double value) throws IllegalArgumentException {
        return new Quantity(value, Unit.MICRO_MOL_PER_LITRE);
    }

    /**
     * Create a new quantity from a molarity in nmol/l which must be positive.
     *
     * @param value the value
     * @return the quantity
     * @throws IllegalArgumentException if the value is not positive
     */
    public static Quantity fromNanoMolPerLitre(double value) throws IllegalArgumentException {
        return new Quantity(value, Unit.NANO_MOL_PER_LITRE);
    }

    public double getValue() {
        return value;
    }

    public Unit getUnit() {
        return unit;
    }

    public static Quantity fromJson(JsonObject obj) {
        double value = obj.getJsonNumber("value").doubleValue();
        Unit unit = Unit.valueOf(obj.getString("unit").toUpperCase());
        return new Quantity(value, unit);
    }

    @Override
    public JsonObject asJson() {
        JsonObject obj = Json.createObjectBuilder()
                .add("value", value)
                .add("unit", unit.toSnakeCase())
                .build();
        return obj;
    }

    @Override
    public String toString() {
        return value + "[" + unit + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
        Quantity other = (Quantity) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        if (unit != other.unit)
            return false;
        return true;
    }
}
