package com.scitequest.martin.export;

import javax.json.Json;
import javax.json.JsonStructure;

import com.scitequest.martin.settings.ProjectExt;
import com.scitequest.martin.utils.StringUtils;

/** Represents the project metadata information exported with a measurement. */
public final class Project implements JsonExportable {

    /** The project name. */
    private final String name;
    /** The project description. */
    private final String description;

    private Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Create a new project from the parameters.
     *
     * @param name        the project name, cannot be empty
     * @param description the project description, can be empty
     * @return the project metadata
     * @throws IllegalArgumentException if the name is blank
     */
    public static Project of(String name, String description) throws IllegalArgumentException {
        // Trim whitespace at front and end
        name = name.trim();
        description = description.trim();
        // Guard against empty name
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        // Guard against none project name
        if (name.equalsIgnoreCase("none")) {
            throw new IllegalArgumentException("The project name cannot be '" + name + "'");
        }
        return new Project(name, description);
    }

    /**
     * Create a new project based on project settings.
     *
     * @param proj the settings of a project
     * @return the project metadata
     */
    public static Project fromProjectSettings(ProjectExt proj) throws IllegalArgumentException {
        return Project.of(proj.getName(), proj.getDescription());
    }

    /**
     * Get the project name.
     *
     * @return the project name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the project name in kebap-case-form.
     *
     * @return the project name in kebap-case.
     */
    public String getNameAsKebapCase() {
        return StringUtils.toKebapCase(name);
    }

    /**
     * Get the project description.
     *
     * @return the project description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public JsonStructure asJson() {
        return Json.createObjectBuilder()
                .add("name", this.name)
                .add("description", this.description)
                .build();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
        Project other = (Project) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }
}
