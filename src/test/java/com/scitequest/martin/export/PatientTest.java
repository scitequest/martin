package com.scitequest.martin.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class PatientTest {

    @Test
    public void testChangeTags() {
        Set<String> patientTags = new HashSet<>(Arrays.asList("mother", "pregnant"));
        Patient patient = Patient.of("AB123", "Benjamin", patientTags);
        patientTags.add("test");
        assertNotEquals(patientTags, patient.getTags());
    }

    @Test
    public void testIsValidTag() {
        // Empty
        assertTrue(!Patient.isValidTag(""));
        assertTrue(!Patient.isValidTag(" \t"));
        // Spaces
        assertTrue(Patient.isValidTag("tag"));
        assertTrue(!Patient.isValidTag("ta g"));
        assertTrue(!Patient.isValidTag("tag "));
        assertTrue(!Patient.isValidTag(" tag"));
        // Uppercase
        assertTrue(Patient.isValidTag("TaG"));
        // Hyphen and underscore
        assertTrue(Patient.isValidTag("Tag_tag"));
        assertTrue(Patient.isValidTag("Tag-tag"));
        assertTrue(Patient.isValidTag("-t--ag"));
        assertTrue(Patient.isValidTag("_ta__g"));
        // Digits
        assertTrue(Patient.isValidTag("0192Tag"));
        // Specials
        assertTrue(!Patient.isValidTag("asd#%$f"));
        assertTrue(Patient.isValidTag("asdöäüßf"));
    }

    @Test
    public void patientEquals() {
        Patient patientA = Patient.of("AB123", "Benjamin", Set.of("mother", "pregnant"));
        Patient patientB = Patient.of("AB123", "Benjamin", Set.of("mother", "pregnant"));
        Patient patientC = Patient.of("AB123", "Benjamin", Set.of("mother"));

        assertTrue(patientA.equals(patientB));
        assertEquals(patientA.hashCode(), patientB.hashCode());

        assertFalse(patientA.equals(patientC));
        assertNotEquals(patientA.hashCode(), patientC.hashCode());
        assertFalse(patientB.equals(patientC));
        assertNotEquals(patientB.hashCode(), patientC.hashCode());
    }
}
