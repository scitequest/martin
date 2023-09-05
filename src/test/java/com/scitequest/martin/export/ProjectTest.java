package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import javax.json.Json;

import org.junit.Test;

import com.scitequest.martin.settings.ImageDefaults;
import com.scitequest.martin.settings.ProjectExt;

public class ProjectTest {

    @Test
    public void testOf() {
        var proj = Project.of("name", "description");
        assertEquals("name", proj.getName());
        assertEquals("description", proj.getDescription());
    }

    @Test
    public void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> Project.of("  ", "description"));
        var proj = Project.of("name", "  ");
        assertEquals("name", proj.getName());
        assertEquals("", proj.getDescription());
    }

    @Test
    public void testNoneProjectNameNotAllowed() {
        assertThrows(IllegalArgumentException.class, () -> Project.of(" none ", "description"));
        assertThrows(IllegalArgumentException.class, () -> Project.of(" None ", "description"));
        assertThrows(IllegalArgumentException.class, () -> Project.of(" NoNe ", "description"));
        assertThrows(IllegalArgumentException.class, () -> Project.of(" NONE ", "description"));
        assertEquals("nOnE project", Project.of("nOnE project", "").getName());
    }

    @Test
    public void testNameAndDescriptionTrimmed() {
        var proj = Project.of("\t name  ", "  description\t");
        assertEquals("name", proj.getName());
        assertEquals("description", proj.getDescription());
    }

    @Test
    public void testFromProjectSettings() {
        var projSettings = ProjectExt.of("name", "description", Set.of(), List.of(),
                ImageDefaults.of("imager", 2, Duration.ofSeconds(0)));
        var proj = Project.fromProjectSettings(projSettings);
        assertEquals("name", proj.getName());
        assertEquals("description", proj.getDescription());
    }

    @Test
    public void testProjectNameAsKebapCase() {
        var s = "\t  #$some   ^!__project 3455 NAME";
        var proj = Project.of(s, "description");
        assertEquals("some-project-3455-name", proj.getNameAsKebapCase());
    }

    @Test
    public void testProjectNameAsKebapCase2() {
        var s = "\t  #$some   ^!__project 3455 NAME//\\\\";
        var proj = Project.of(s, "description");
        assertEquals("some-project-3455-name", proj.getNameAsKebapCase());
    }

    @Test
    public void testAsJson() {
        var proj = Project.of("some name", "Some description ...");
        var json = Json.createObjectBuilder()
                .add("name", "some name")
                .add("description", "Some description ...")
                .build();
        assertEquals(proj.asJson(), json);
    }
}
