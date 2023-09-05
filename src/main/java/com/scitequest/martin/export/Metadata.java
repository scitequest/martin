package com.scitequest.martin.export;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 * This class holds all information about the metadata of a slide measurement.
 */
public final class Metadata implements JsonExportable {

    /** The current version of the file format specification. */
    public static final String CURRENT_VERSION = "0.7.0";

    /**
     * The time when this measurement took place and the corresponding timezone.
     */
    private final ZonedDateTime datetime;
    /** Information about the project associated with the measurement. */
    private final Optional<Project> project;
    /** Information about the patient associated with the measurement. */
    private final Patient patient;
    /** Information about the image. */
    private final Image image;
    /** Metadata about the incubation process. */
    private final List<Incubation> incubations;

    /**
     * Create a new measurement metadata class.
     *
     * @param datetime    the point in time of the measurement
     * @param project     the associated project
     * @param patient     the associated patient
     * @param image       the image metadata
     * @param incubations the incubation metatdata
     */
    private Metadata(ZonedDateTime datetime, Optional<Project> project,
            Patient patient, Image image, List<Incubation> incubations) {
        this.datetime = datetime;
        this.project = project;
        this.patient = patient;
        this.image = image;
        this.incubations = List.copyOf(incubations);
    }

    /**
     * Create a new measurement metadata.
     *
     * @param datetime    the point in time of the measurement
     * @param project     the associated project
     * @param patient     the associated patient
     * @param image       the image metadata
     * @param incubations the incubation metadata
     * @return a new measurement metadata instance
     * @throws IllegalArgumentException if any of the above contracts is violated
     * @see Incubation
     */
    public static Metadata of(ZonedDateTime datetime, Optional<Project> project,
            Patient patient, Image image, List<Incubation> incubations)
            throws IllegalArgumentException {
        return new Metadata(datetime, project, patient, image, incubations);
    }

    /**
     * Get the datetime.
     *
     * @return the datetime
     */
    public ZonedDateTime getDatetime() {
        return datetime;
    }

    /**
     * Get the project.
     *
     * @return the project
     */
    public Optional<Project> getProject() {
        return project;
    }

    /**
     * Get the patient.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Get the image metadata.
     *
     * @return the image metadata
     */
    public Image getImage() {
        return image;
    }

    /**
     * Get the incubation metadata.
     *
     * @return the incubation metadata
     */
    public List<Incubation> getIncubations() {
        return Collections.unmodifiableList(incubations);
    }

    @Override
    public JsonObject asJson() {
        JsonArrayBuilder incubationsJson = Json.createArrayBuilder();
        incubations.stream().forEach(incub -> incubationsJson.add(incub.asJson()));

        var json = Json.createObjectBuilder()
                .add("version", Metadata.CURRENT_VERSION)
                .add("datetime", this.datetime.toOffsetDateTime().toString())
                .add("patient", this.patient.asJson())
                .add("image", this.image.asJson())
                .add("incubations", incubationsJson);
        this.project.ifPresent(proj -> json.add("project", proj.asJson()));

        return json.build();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datetime == null) ? 0 : datetime.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((patient == null) ? 0 : patient.hashCode());
        result = prime * result + ((image == null) ? 0 : image.hashCode());
        result = prime * result + ((incubations == null) ? 0 : incubations.hashCode());
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
        Metadata other = (Metadata) obj;
        if (datetime == null) {
            if (other.datetime != null)
                return false;
        } else if (!datetime.equals(other.datetime))
            return false;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        if (patient == null) {
            if (other.patient != null)
                return false;
        } else if (!patient.equals(other.patient))
            return false;
        if (image == null) {
            if (other.image != null)
                return false;
        } else if (!image.equals(other.image))
            return false;
        if (incubations == null) {
            if (other.incubations != null)
                return false;
        } else if (!incubations.equals(other.incubations))
            return false;
        return true;
    }
}
