package com.scitequest.martin.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;

import com.scitequest.martin.export.Incubation;
import com.scitequest.martin.export.JsonParseException;
import com.scitequest.martin.export.Quantity;

public class ProjectExtTest {

    @Test
    public void testChangeTags() {
        Set<String> tags = new HashSet<>(Arrays.asList("mother", "pregnant"));
        List<Incubation> incubations = List.of(Incubation.of("solution",
                Quantity.fromPercent(1.0), Quantity.fromPercent(1.0),
                Duration.ofSeconds(0)));
        ProjectExt proj = ProjectExt.of("test", "test", tags,
                incubations, ImageDefaults.of("imager", 1, Duration.ofSeconds(0)));

        tags.add("test");
        assertNotEquals(tags, proj.getTags());
    }

    @Test
    public void testAsJson() throws FileNotFoundException {
        var proj = ProjectExt.placeholder()
                .withName("Test Project")
                .withDescription("Some form of description")
                .withTags(Set.of("z", "a", "mother", "pregnant"));

        String path = "src/test/resources/settings/project.json";
        JsonObject expected = Json.createReader(new FileReader(path)).readObject();
        assertEquals(expected, proj.asJson());
    }

    @Test
    public void testFromJson() throws FileNotFoundException, JsonParseException {
        var expected = ProjectExt.placeholder()
                .withName("Test Project")
                .withDescription("Some form of description")
                .withTags(Set.of("z", "a", "mother", "pregnant"));

        String path = "src/test/resources/settings/project.json";
        JsonObject json = Json.createReader(new FileReader(path)).readObject();
        assertEquals(expected, ProjectExt.fromJson(json));
    }
}
