package com.scitequest.martin.settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import com.scitequest.martin.Version;
import com.scitequest.martin.export.JsonExportable;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.settings.exception.DuplicateElementException;
import com.scitequest.martin.settings.exception.OneElementRequiredException;

/**
 * Represents all project specific settings.
 */
public final class ProjectSettings implements JsonExportable {

    /** The current version of the file format specification. */
    public static final Version CURRENT_VERSION = Version.of(0, 1, 0);

    private final List<ProjectExt> projects;

    /** Set to keep track of duplicates in the actual project list. */
    private final Set<String> projectNames;

    private int lastUsedProjectIndex = -1;

    private ProjectSettings(List<ProjectExt> projects, Set<String> projectNames, int lastUsedProjectIndex) {
        this.projects = projects;
        this.projectNames = projectNames;
        this.lastUsedProjectIndex = lastUsedProjectIndex;
    }

    /**
     * Create project settings from preexisting projects.
     *
     * @param projects the projects
     * @return project settings
     */
    static ProjectSettings of(List<ProjectExt> projects, int lastUsedProjectIndex) {
        if (projects.isEmpty()) {
            throw new IllegalArgumentException("Projects cannot be empty");
        }
        if (lastUsedProjectIndex < -1 || lastUsedProjectIndex >= projects.size()) {
            throw new IllegalArgumentException("Last used mask index is out of bounds");
        }

        Set<String> distinctNameSet = projectsToDistinctNameSet(projects);
        if (projects.size() != distinctNameSet.size()) {
            throw new IllegalArgumentException("Project list contains duplicate project names");
        }

        return new ProjectSettings(new ArrayList<>(projects), distinctNameSet, lastUsedProjectIndex);
    }

    /**
     * Initialize project settings with a default project.
     *
     * @return default project settings
     */
    static ProjectSettings defaultSettings() {
        ProjectExt defaultProj = ProjectExt.placeholder();
        return ProjectSettings.of(List.of(defaultProj), 0);
    }

    /**
     * Helper method to create a set of all normalized names of the project names.
     *
     * @param projects the projects of which the normalized name set should be
     *                 created
     * @return the distinct name set
     */
    private static Set<String> projectsToDistinctNameSet(List<ProjectExt> projects) {
        Set<String> distinctNameSet = projects.stream()
                .map(m -> m.getNormalizedName())
                .distinct()
                .collect(Collectors.toSet());
        return distinctNameSet;
    }

    void copyInto(ProjectSettings other) {
        this.projects.clear();
        this.projects.addAll(other.projects);
        this.projectNames.clear();
        this.projectNames.addAll(projectsToDistinctNameSet(other.projects));
        this.lastUsedProjectIndex = other.lastUsedProjectIndex;
    }

    public static void exportProject(ProjectExt proj, Path path) throws IOException {
        JsonObjectBuilder obj = Json.createObjectBuilder()
                .add("version", CURRENT_VERSION.toString());
        proj.asJson().asJsonObject().forEach((k, v) -> obj.add(k, v));
        Files.writeString(path, obj.build().toString(), StandardCharsets.UTF_8);
    }

    public ProjectExt importProject(Path path) throws IOException, JsonParseException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject obj = jsonReader.readObject();
            if (obj.containsKey("version")) {
                Version version = Version.of(obj.getString("version"));
                if (!CURRENT_VERSION.isCompatible(version)) {
                    throw new JsonParseException("Provided project settings are incompatible");
                }
            }
            // Parse project from JSON
            ProjectExt proj = ProjectExt.fromJson(obj);
            // Add the project
            addProject(proj);
            return proj;
        } catch (JsonException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    /**
     * Get a copy of all projects.
     *
     * @return the stored projects
     */
    public List<ProjectExt> getProjects() {
        // Shallow copy is enough, as a individual project is immutable
        return new ArrayList<>(projects);
    }

    /**
     * Copies all provided projects into the project settings.
     *
     * @param projects the projects to replace the existing projects with
     */
    public void setProjects(List<ProjectExt> projects) {
        this.projects.clear();
        this.projects.addAll(projects);
    }

    public ProjectExt getProject(int projectIndex) {
        return projects.get(projectIndex);
    }

    public void setProject(int index, ProjectExt project) {
        String oldNormalizedName = getProject(index).getNormalizedName();
        String newNormalizedName = project.getNormalizedName();
        if (!oldNormalizedName.equals(newNormalizedName)
                && projectNames.contains(newNormalizedName)) {
            throw new DuplicateElementException();
        }
        projects.set(index, project);
        projectNames.remove(oldNormalizedName);
        projectNames.add(newNormalizedName);

        projects.set(index, project);
    }

    /**
     * Adds a new project to the list of projects if the name is not a duplicate.
     *
     * @param project the project to add
     * @throws DuplicateElementException if the mask name is already in masks
     */
    public void addProject(ProjectExt project) {
        String normalizedName = project.getNormalizedName();

        if (this.projectNames.contains(normalizedName)) {
            throw new DuplicateElementException();
        }

        projects.add(project);
        projectNames.add(normalizedName);
    }

    public void removeProject(int index) {
        if (projects.size() <= 1) {
            throw new OneElementRequiredException();
        }
        ProjectExt proj = projects.remove(index);
        projectNames.remove(proj.getNormalizedName());
        if (lastUsedProjectIndex == index) {
            lastUsedProjectIndex = -1;
        } else if (lastUsedProjectIndex > index) {
            // The last used mask index must be shifted down since we deleted an element
            // before
            lastUsedProjectIndex--;
        }

    }

    /**
     * Get all names of the stored projects.
     *
     * @return the project names
     */
    public List<String> getNameList() {
        return getNameList(projects);
    }

    public List<String> getNameList(List<ProjectExt> projects) {
        return projects.stream()
                .map(project -> project.getName())
                .collect(Collectors.toList());
    }

    /**
     * Returns the last used project index.
     *
     * If there is no last used project set, the index is set to -1.
     *
     * @return last used project index or -1 if no last used project is set.
     */
    public int getLastUsedProjectIndex() {
        return lastUsedProjectIndex;
    }

    /**
     * Set the last used project index directly.
     *
     * If the index is outside the range of the current projects it is set to -1 for
     * no last project.
     *
     * @param idx the index
     */
    public void setLastUsedProjectIndex(int idx) {
        if (idx < -1 || idx >= projects.size()) {
            this.lastUsedProjectIndex = -1;
            return;
        }
        this.lastUsedProjectIndex = idx;
    }

    @Override
    public JsonStructure asJson() {
        JsonArrayBuilder projectsJson = Json.createArrayBuilder();
        projects.stream().forEach(proj -> projectsJson.add(proj.asJson()));

        JsonObject json = Json.createObjectBuilder()
                .add("last_used_project_index", lastUsedProjectIndex)
                .add("projects", projectsJson)
                .build();
        return json;
    }

    public static ProjectSettings fromJson(JsonObject json) throws JsonParseException {
        try {
            int lastUsedProject = json.getInt("last_used_project_index");
            List<ProjectExt> projs = new ArrayList<>();
            for (JsonValue value : json.getJsonArray("projects")) {
                projs.add(ProjectExt.fromJson(value.asJsonObject()));
            }
            return ProjectSettings.of(projs, lastUsedProject);
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
        result = prime * result + ((projects == null) ? 0 : projects.hashCode());
        result = prime * result + lastUsedProjectIndex;
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
        ProjectSettings other = (ProjectSettings) obj;
        if (projects == null) {
            if (other.projects != null)
                return false;
        } else if (!projects.equals(other.projects))
            return false;
        if (lastUsedProjectIndex != other.lastUsedProjectIndex)
            return false;
        return true;
    }
}
