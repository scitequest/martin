package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;

public class MetadataTest {

    @Test
    public void testChangeIncubations() {
        List<Incubation> incubations = new ArrayList<>();

        ZonedDateTime datetime = ZonedDateTime.of(2022, 06, 14, 15, 4, 33, 0,
                ZoneId.of("Europe/Berlin"));
        var project = Optional.of(Project.of("Project Name", "Project Description"));
        Patient patient = Patient.of("AB123", "Benjamin", Set.of("mother", "pregnant"));
        var created = ZonedDateTime.of(2022, 06, 14, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"));
        Image image = Image.of(created, "Best imager ever", 2, Duration.ofSeconds(30));

        Metadata metadata = Metadata.of(datetime, project, patient, image, incubations);

        Incubation incubation = Incubation.of("Potassium",
                Quantity.fromMicroMolPerLitre(2.3),
                Quantity.fromPercent(5.8),
                Duration.ofSeconds(15));
        incubations.add(incubation);
        assertNotEquals(incubations, metadata.getIncubations());
    }

    @Test
    public void testEquals() {
        ZonedDateTime datetime = ZonedDateTime.of(2022, 06, 14, 15, 4, 33, 0,
                ZoneId.of("Europe/Berlin"));
        var project = Optional.of(Project.of("project name", "project description"));
        Patient patient = Patient.of("AB123", "Benjamin", Set.of("mother", "pregnant"));
        var created = ZonedDateTime.of(2022, 06, 14, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"));
        Image image = Image.of(created, "imager", 2, Duration.ofSeconds(30));
        var incubations = List.of(Incubation.of("Potassium",
                Quantity.fromMicroMolPerLitre(2.3),
                Quantity.fromPercent(5.8),
                Duration.ofSeconds(15)));
        Image image2 = Image.of(created, "imager", 2, Duration.ofSeconds(31));

        Metadata metadata1 = Metadata.of(datetime, project, patient, image, incubations);
        Metadata metadata2 = Metadata.of(datetime, project, patient, image, incubations);
        Metadata metadata3 = Metadata.of(datetime, project, patient, image2, incubations);

        assertTrue(metadata1.equals(metadata2));
        assertEquals(metadata1.hashCode(), metadata2.hashCode());

        assertFalse(metadata1.equals(metadata3));
        assertNotEquals(metadata1.hashCode(), metadata3.hashCode());
        assertFalse(metadata2.equals(metadata3));
        assertNotEquals(metadata2.hashCode(), metadata3.hashCode());
    }

    @Test
    public void testExportMetadata() throws FileNotFoundException {
        var datetime = ZonedDateTime.of(2022, 06, 14, 15, 4, 33, 0, ZoneId.of("Europe/Berlin"));
        var project = Optional.of(Project.of("Project Name", "Project Description"));
        Patient patient = Patient.of("AB123", "Benjamin", Set.of("mother", "pregnant"));
        var created = ZonedDateTime.of(2022, 06, 13, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"));
        Image image = Image.of(created, "Best imager ever", 3, Duration.ofSeconds(30));
        Incubation incubation = Incubation.of("Potassium",
                Quantity.fromMicroMolPerLitre(2.3),
                Quantity.fromPercent(5.8),
                Duration.ofSeconds(15));
        Metadata metadata = Metadata.of(datetime, project,
                patient, image, List.of(incubation));

        String path = "src/test/resources/export/metadata.json";
        JsonObject expected = Json.createReader(new FileReader(path)).readObject();
        assertEquals(expected, metadata.asJson());
    }

    @Test
    public void testSupportsEmptyProject() {
        var datetime = ZonedDateTime.of(2022, 06, 14, 15, 4, 33, 0, ZoneId.of("Europe/Berlin"));
        Optional<Project> project = Optional.empty();
        Patient patient = Patient.of("AB123", "Benjamin", Set.of("mother", "pregnant"));
        var created = ZonedDateTime.of(2022, 06, 13, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"));
        Image image = Image.of(created, "Best imager ever", 2, Duration.ofSeconds(30));
        Incubation incubation = Incubation.of("Potassium",
                Quantity.fromMicroMolPerLitre(2.3),
                Quantity.fromPercent(5.8),
                Duration.ofSeconds(15));
        Metadata metadata = Metadata.of(datetime, project,
                patient, image, List.of(incubation));

        assertFalse(metadata.asJson().toString().contains("\"project\":"));
    }
}
