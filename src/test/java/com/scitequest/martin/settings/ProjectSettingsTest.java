package com.scitequest.martin.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.scitequest.martin.export.JsonParseException;

public class ProjectSettingsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetProjectNames() {
        var projSettings = ProjectSettings.defaultSettings();
        assertEquals(List.of("New project"), projSettings.getNameList());
        projSettings.setProjects(
                List.of(ProjectExt.placeholder(), ProjectExt.placeholder().withName("Test")));
        assertEquals(List.of("New project", "Test"), projSettings.getNameList());
    }

    @Test
    public void testSetAndGetProjects() {
        var proj1 = ProjectExt.placeholder();
        var proj2 = ProjectExt.placeholder().withName("Test");
        var projSettings = ProjectSettings.defaultSettings();

        assertEquals(List.of(proj1), projSettings.getProjects());
        projSettings.setProjects(List.of(proj2, proj1));
        assertEquals(List.of(proj2, proj1), projSettings.getProjects());
    }

    @Test
    public void testSetAndGetLastUsedProject() {
        var proj1 = ProjectExt.placeholder();
        var proj2 = ProjectExt.placeholder().withName("Test");
        var projSettings = ProjectSettings.of(List.of(proj1, proj2), 0);

        assertEquals(0, projSettings.getLastUsedProjectIndex());
        projSettings.setLastUsedProjectIndex(2);
        assertEquals(-1, projSettings.getLastUsedProjectIndex());
        projSettings.setLastUsedProjectIndex(-100);
        assertEquals(-1, projSettings.getLastUsedProjectIndex());
        projSettings.setLastUsedProjectIndex(1);
        assertEquals(1, projSettings.getLastUsedProjectIndex());
    }

    @Test
    public void testProjectImportExportRoundtrip() throws IOException, JsonParseException {
        Settings compare = Settings.defaultSettings();
        ProjectExt compProject = ProjectExt.placeholder().withName("noDuplicate");
        compare.getProjectSettings().addProject(compProject);

        Settings trip = Settings.defaultSettings();

        Path path = folder.getRoot().toPath().resolve("projects.json");

        ProjectSettings.exportProject(compProject, path);

        assertTrue(!Files.readString(path).isBlank());
        trip.getProjectSettings().importProject(path);

        assertEquals(compare, trip);
    }

    @Test
    public void testAsJson() throws IOException {
        var proj1 = ProjectExt.placeholder()
                .withName("Test Project")
                .withDescription("Some form of description")
                .withTags(Set.of("z", "a", "mother", "pregnant"));
        var proj2 = ProjectExt.placeholder();
        var projSettings = ProjectSettings.of(List.of(proj1, proj2), 0);

        String path = "src/test/resources/settings/project_settings.json";
        JsonObject expected = Json.createReader(new FileReader(path)).readObject();
        assertEquals(expected, projSettings.asJson());
    }

    @Test
    public void testFromJson() throws IOException, JsonParseException {
        var proj1 = ProjectExt.placeholder()
                .withName("Test Project")
                .withDescription("Some form of description")
                .withTags(Set.of("z", "a", "mother", "pregnant"));
        var proj2 = ProjectExt.placeholder();
        var expected = ProjectSettings.of(List.of(proj1, proj2), 0);

        Path path = Paths.get("src/test/resources/settings/project_settings.json");
        try (BufferedReader reader = Files.newBufferedReader(path);
                JsonReader jsonReader = Json.createReader(reader)) {
            assertEquals(expected, ProjectSettings.fromJson(jsonReader.readObject()));
        }
    }
}
