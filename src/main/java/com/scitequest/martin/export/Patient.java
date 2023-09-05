package com.scitequest.martin.export;

import java.util.Collections;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.scitequest.martin.utils.StringUtils;

/**
 * Represents a Patient metadata within a measurement.
 */
public final class Patient implements JsonExportable {
    /** The identifier of the patient as may be given from a hospital. */
    private final String id;
    /** The full name of the patient. */
    private final String name;
    /**
     * Tags associated with the patient such as "pregnant" or "mother".
     *
     * The tags are wrapped in a unmodifiable set to prevent outside modification.
     */
    private final Set<String> tags;

    /**
     * Create new patient metadata.
     *
     * @param id   the ID
     * @param name the full name
     * @param tags the tags
     */
    private Patient(final String id, final String name, final Set<String> tags) {
        this.id = id;
        this.name = name;
        // Safety: We have to copy the tags or the outside can modify them.
        this.tags = Set.copyOf(tags);
    }

    /**
     * Creates a new patient from the specified parameters.
     *
     * @param id   the patient's ID, must not contain only whitespace or be empty
     * @param name the patient's full name, must not contain only whitespace or be
     *             empty
     * @param tags the tags associated with the patient. Each tag must not be empty,
     *             contain no whitespace and be lowercased.
     * @return The patient metadata.
     * @throws IllegalArgumentException If any of the above contracts are violated.
     */
    public static Patient of(String id, String name, Set<String> tags)
            throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Patient name cannot be empty");
        }
        if (tags == null) {
            throw new IllegalArgumentException("Tags cannot be null");
        }
        for (String tag : tags) {
            if (!Patient.isValidTag(tag)) {
                throw new IllegalArgumentException("Tag '"
                        + tag + "' is not valid. A tag must not be empty, "
                        + "contain no whitespace and be lowercase");
            }
        }

        return new Patient(id, name, tags);
    }

    /**
     * Check if the given String is a valid tag.
     *
     * <p>
     * Tags must not:
     * <ul>
     * <li>be empty
     * <li>contain whitespace
     * <li>use uppercase characters
     * </ul>
     *
     * @param tag the tag to validate
     * @return false if the tag is invalid, else true.
     */
    public static boolean isValidTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }
        if (!tag.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-' || c == '_')) {
            return false;
        }
        return true;
    }

    /**
     * Get the patient ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the full patient name.
     *
     * @return the patient full name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the tags of the patient as a cloned unmodifiable set.
     *
     * @return the patient
     */
    public Set<String> getTags() {
        // Safety: Returning an unmodifiable set is enough here because replacement of
        // the set is not possible.
        return Collections.unmodifiableSet(this.tags);
    }

    /**
     * Returns the ID in kebap-case-form.
     *
     * @return the ID in kebap-case.
     */
    public String getIdAsKebapCase() {
        return StringUtils.toKebapCase(id);
    }

    @Override
    public JsonObject asJson() {
        JsonArrayBuilder jsonTags = Json.createArrayBuilder();
        this.tags.stream().sorted().forEach(tag -> jsonTags.add(tag));

        JsonObject json = Json.createObjectBuilder()
                .add("id", this.id)
                .add("name", this.name)
                .add("tags", jsonTags)
                .build();
        return json;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
        Patient other = (Patient) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        return true;
    }
}
