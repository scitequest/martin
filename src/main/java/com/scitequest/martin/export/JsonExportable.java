package com.scitequest.martin.export;

import javax.json.JsonStructure;

/**
 * This interface identifies a class which can be converted to a JSON
 * representation.
 *
 * For this the implementor must implement the {@code asJson()} method.
 */
public interface JsonExportable {
    /**
     * Convert this classes values into a JSON structure.
     *
     * @return the JSON structure representing the class.
     */
    JsonStructure asJson();
}
