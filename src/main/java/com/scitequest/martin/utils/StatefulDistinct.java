package com.scitequest.martin.utils;

import java.util.Comparator;
import java.util.Optional;

/**
 * Can be used together with streams to remove duplicates from a <b>sorted</b>
 * list.
 *
 * NOTE: The list must be sorted or only subsequent duplicates will be removed.
 *
 * @param <T> the type of the elements that should be checked
 */
public final class StatefulDistinct<T> {

    private Optional<T> prev = Optional.empty();
    private final Comparator<T> comparator;

    private StatefulDistinct(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public static <T> StatefulDistinct<T> fromComparator(Comparator<T> comparator) {
        return new StatefulDistinct<>(comparator);
    }

    /**
     * Checks if the provided element is the same as the element supplied in the
     * previous call.
     *
     * @param current the current element
     * @return whether the current element is a duplicate of the previous element
     */
    public boolean isDuplicate(T current) {
        boolean isDuplicate = prev
                .map(prevv -> comparator.compare(prevv, current) == 0)
                .orElse(false);
        prev = Optional.of(current);
        return isDuplicate;
    }
}
