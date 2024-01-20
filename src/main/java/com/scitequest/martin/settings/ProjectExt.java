package com.scitequest.martin.settings;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;

import com.scitequest.martin.export.Incubation;
import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.export.Project;
import com.scitequest.martin.export.Quantity;

/**
 * This class stores all relevant information about project settings.
 *
 * The name and description are encapsulated in the exported project
 * representation.
 *
 * This class is an immutable value class and values can't be changed after they
 * have been set.
 *
 * @see com.scitequest.martin.export.Project
 */
public final class ProjectExt implements JsonExportable {
    public static final String DEFAULT_NAME = "New project";
    /**
     * The actual project.
     */
    private final Project project;
    /**
     * List of tags useable in the Project.
     */
    private final Set<String> tags;
    /**
     * List of incubations typically used for an assay of the set Project.
     */
    private final List<Incubation> incubations;
    /**
     * Imager typically used for this Project as well as exposure time.
     */
    private final ImageDefaults imageDefaults;

    /**
     * Constructor, instead of changing values projects are set to be final.
     * Changing values will be treated as if a new project was made.
     */
    private ProjectExt(Project project,
            Set<String> tags, List<Incubation> incubations, ImageDefaults imageDefaults) {
        this.project = project;
        // Safety: We have to copy the tags or the outside can modify them.
        this.tags = Set.copyOf(tags);
        this.incubations = incubations;
        this.imageDefaults = imageDefaults;
    }

    /**
     * Create a new settings project.
     *
     * @param name          project name
     * @param description   project description
     * @param tags          available sample tags
     * @param incubations   default incubations
     * @param imageMetadata default image metadata
     * @return a new settings project
     * @throws IllegalArgumentException if the project name or description is
     *                                  invalid
     */
    public static ProjectExt of(String name, String description,
            Set<String> tags, List<Incubation> incubations, ImageDefaults imageMetadata)
            throws IllegalArgumentException {
        Project proj = Project.of(name, description);
        return new ProjectExt(proj, tags, incubations, imageMetadata);
    }

    public static ProjectExt placeholder() {
        List<Incubation> incubations = Arrays.asList(
                Incubation.of("Solution 1",
                        Quantity.fromPercent(10.0), Quantity.fromPercent(5.0),
                        Duration.ofSeconds(1000)),
                Incubation.of("Solution 2",
                        Quantity.fromMicroMolPerLitre(200.0),
                        Quantity.fromMicroMolPerLitre(100.0),
                        Duration.ofSeconds(1000)));
        ImageDefaults imageMetadata = ImageDefaults.of("Imager name", 1, Duration.ofSeconds(0));
        return ProjectExt.of(DEFAULT_NAME, "", new HashSet<>(), incubations, imageMetadata);
    }

    /**
     * Return a copied instance with the new name set.
     *
     * @param name the new project name
     * @return copied instance
     * @throws IllegalArgumentException if an invalid name is passed
     */
    public ProjectExt withName(String name) throws IllegalArgumentException {
        Project proj = Project.of(name, project.getDescription());
        return new ProjectExt(proj, tags, incubations, imageDefaults);
    }

    /**
     * Return a copied instance with the new description set.
     *
     * @param description the new project description
     * @return copied instance
     * @throws IllegalArgumentException if an invalid description is passed
     */
    public ProjectExt withDescription(String description) throws IllegalArgumentException {
        Project proj = Project.of(project.getName(), description);
        return new ProjectExt(proj, tags, incubations, imageDefaults);
    }

    /**
     * Return a copied instance with the tags changed.
     *
     * @param newTags the new tags
     * @return copied instance
     */
    public ProjectExt withTags(Set<String> newTags) {
        return new ProjectExt(project, Set.copyOf(newTags), incubations, imageDefaults);
    }

    /**
     * Get the project name.
     *
     * @return the project name
     */
    public String getName() {
        return project.getName();
    }

    /**
     * Get the project description.
     *
     * @return the project description
     */
    public String getDescription() {
        return project.getDescription();
    }

    /**
     * Get the tags available for this project.
     *
     * @return the project tags
     */
    public Set<String> getTags() {
        // Safety: Returning an unmodifiable set is enough here because replacement of
        // the set is not possible.
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Get the default incubation settings for this project.
     *
     * @return default incubation settings
     */
    public List<Incubation> getIncubations() {
        return Collections.unmodifiableList(incubations);
    }

    /**
     * Get the default image metadata.
     *
     * @return default image metadata
     */
    public ImageDefaults getImageDefaults() {
        return imageDefaults;
    }

    @Override
    public JsonStructure asJson() {
        JsonArrayBuilder jsonTags = Json.createArrayBuilder();
        this.tags.stream().sorted().forEach(tag -> jsonTags.add(tag));
        JsonArrayBuilder incubationsJson = Json.createArrayBuilder();
        incubations.stream().forEach(incub -> incubationsJson.add(incub.asJson()));

        JsonObject json = Json.createObjectBuilder()
                .add("name", project.getName())
                .add("description", project.getDescription())
                .add("tags", jsonTags)
                .add("default_incubations", incubationsJson)
                .add("default_image", imageDefaults.asJson())
                .build();
        return json;
    }

    public static ProjectExt fromJson(JsonObject json) throws JsonParseException {
        try {
            String name = json.getString("name");
            String description = json.getString("description");
            Set<String> tags = json.getJsonArray("tags").stream()
                    .map(JsonString.class::cast)
                    .map(JsonString::getString)
                    .collect(Collectors.toSet());
            List<Incubation> incubations = json.getJsonArray("default_incubations").stream()
                    .map(incub -> Incubation.fromJson(incub.asJsonObject()))
                    .collect(Collectors.toList());
            ImageDefaults image = ImageDefaults.fromJson(json.getJsonObject("default_image"));
            return ProjectExt.of(name, description, tags, incubations, image);
        } catch (ClassCastException e) {
            throw new JsonParseException("Encountered an unexpected JSON type", e);
        } catch (NullPointerException e) {
            throw new JsonParseException("Expected JSON key is missing", e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + ((incubations == null) ? 0 : incubations.hashCode());
        result = prime * result + ((imageDefaults == null) ? 0 : imageDefaults.hashCode());
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
        ProjectExt other = (ProjectExt) obj;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        if (incubations == null) {
            if (other.incubations != null)
                return false;
        } else if (!incubations.equals(other.incubations))
            return false;
        if (imageDefaults == null) {
            if (other.imageDefaults != null)
                return false;
        } else if (!imageDefaults.equals(other.imageDefaults))
            return false;
        return true;
    }

    public String getNormalizedName() {
        return project.getName().trim().toLowerCase(Locale.ENGLISH);
    }
}
