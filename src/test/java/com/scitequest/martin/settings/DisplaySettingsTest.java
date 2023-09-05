package com.scitequest.martin.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.awt.Color;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;

import com.scitequest.martin.export.JsonParseException;

public class DisplaySettingsTest {

    @Test
    public void testColor2hex() {
        assertEquals("#FF0000", DisplaySettings.color2hex(Color.RED));
        assertEquals("#FF00FF", DisplaySettings.color2hex(Color.MAGENTA));
    }

    @Test
    public void testIncompleteJson() {
        JsonObject obj = Json.createObjectBuilder()
                .add("colors", "empty")
                .build();
        assertThrows(JsonParseException.class, () -> DisplaySettings.fromJson(obj));
    }

    @Test
    public void testInvalidColor() {
        JsonObject obj = Json.createObjectBuilder()
                .add("colors", Json.createObjectBuilder()
                        .add("deletion_rectangle", "ff0000")
                        .add("halfline", "#0000ff")
                        .add("measure_circle", "#ff00ff")
                        .add("slide", "#0000ff")
                        .add("spotfield", "#00ff00"))
                .add("enable_button_help", true)
                .add("show_measure_circles", true)
                .add("show_spotfield_grid", true)
                .build();
        assertThrows(JsonParseException.class, () -> DisplaySettings.fromJson(obj));
    }
}
