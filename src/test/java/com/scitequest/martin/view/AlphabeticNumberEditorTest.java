package com.scitequest.martin.view;

import org.junit.Assert;
import org.junit.Test;

public class AlphabeticNumberEditorTest {

    @Test
    public void testToAlphabeticNumber() {
        Assert.assertEquals("A", AlphabeticNumberEditor.toAlphabeticNumber(0));
        Assert.assertEquals("Z", AlphabeticNumberEditor.toAlphabeticNumber(25));
        Assert.assertEquals("AA", AlphabeticNumberEditor.toAlphabeticNumber(26));
        Assert.assertEquals("AZ", AlphabeticNumberEditor.toAlphabeticNumber(51));
        Assert.assertEquals("BA", AlphabeticNumberEditor.toAlphabeticNumber(52));
    }

    @Test
    public void testNumberFromAlphabetic() {
        Assert.assertEquals(0, AlphabeticNumberEditor.fromAlphabeticNumber("A"));
        Assert.assertEquals(25, AlphabeticNumberEditor.fromAlphabeticNumber("Z"));
        Assert.assertEquals(26, AlphabeticNumberEditor.fromAlphabeticNumber("AA"));
        Assert.assertEquals(51, AlphabeticNumberEditor.fromAlphabeticNumber("AZ"));
        Assert.assertEquals(52, AlphabeticNumberEditor.fromAlphabeticNumber("BA"));
    }
}
