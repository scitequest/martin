package com.scitequest.martin.view;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

public class TagSelectionTest {

    @Test
    public void testCreateTagSelection() {
        TagSelection.empty();
    }

    @Test
    public void testSetTags() {
        var tagSelection = TagSelection.empty();
        var tags = Set.of("mother", "pregnant");
        tagSelection.setTags(tags);
    }

    @Test
    public void testGetSelectedTags() {
        var tagSelection = TagSelection.empty();
        tagSelection.setTags(Set.of("test", "something"));
        assertEquals(Set.of(), tagSelection.getSelectedTags());
        tagSelection.setTagSelected("something", true);
        assertEquals(Set.of("something"), tagSelection.getSelectedTags());
    }
}
