package com.scitequest.martin;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class GitInfoTest {

    @Test
    public void gitCommitIdAbbrev() throws IOException {
        GitInfo git = GitInfo.fromProperties();
        assertEquals(7, git.getCommitIdAbbrev().length());
    }
}
