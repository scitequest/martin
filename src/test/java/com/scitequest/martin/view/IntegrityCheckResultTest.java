package com.scitequest.martin.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import com.scitequest.martin.view.IntegrityCheckResult.IntegrityCheckContext;

public class IntegrityCheckResultTest {

    private static final IntegrityCheckContext CTX = new IntegrityCheckContext(null, null);

    @Test
    public void testIsSuccess_AllTrue() {
        HashMap<Path, Boolean> results = new HashMap<>();
        results.put(Path.of("file1.txt"), true);
        results.put(Path.of("file2.txt"), true);
        results.put(Path.of("file3.txt"), true);
        IntegrityCheckResult result = IntegrityCheckResult.ofCompleted(results, CTX);

        assertTrue(result.isOk());
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
    }

    @Test
    public void testIsSuccess_OneFalse() {
        HashMap<Path, Boolean> results = new HashMap<>();
        results.put(Path.of("file1.txt"), true);
        results.put(Path.of("file2.txt"), false);
        results.put(Path.of("file3.txt"), true);
        IntegrityCheckResult result = IntegrityCheckResult.ofCompleted(results, CTX);

        assertTrue(result.isOk());
        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
    }

    @Test
    public void testGetValidFiles() {
        HashMap<Path, Boolean> results = new HashMap<>();
        results.put(Path.of("file1.txt"), true);
        results.put(Path.of("file2.txt"), false);
        results.put(Path.of("file3.txt"), true);
        IntegrityCheckResult result = IntegrityCheckResult.ofCompleted(results, CTX);

        Set<Path> validFiles = result.unwrapValidFiles();
        assertEquals(2, validFiles.size());
        assertTrue(validFiles.contains(Path.of("file1.txt")));
        assertTrue(validFiles.contains(Path.of("file3.txt")));
    }

    @Test
    public void testGetInvalidFiles() {
        HashMap<Path, Boolean> results = new HashMap<>();
        results.put(Path.of("file1.txt"), true);
        results.put(Path.of("file2.txt"), false);
        results.put(Path.of("file3.txt"), true);
        IntegrityCheckResult result = IntegrityCheckResult.ofCompleted(results, CTX);

        Set<Path> invalidFiles = result.unwrapInvalidFiles();
        assertEquals(1, invalidFiles.size());
        assertTrue(invalidFiles.contains(Path.of("file2.txt")));
    }
}
