package com.scitequest.martin;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Stores information about the state of the Git repository.
 *
 * The main use of this is, that we can display the Git commit ID to the user
 * and if they report a bug, we can identify the exact software version used to
 * better reproduce the problem.
 */
public final class GitInfo {

    /** The abbreviated 7 char SHA1 commit ID. */
    private final String commitIdAbbrev;

    /**
     * Create new git information.
     *
     * @param commitIdAbbrev the abbreviated commit ID
     */
    private GitInfo(String commitIdAbbrev) {
        this.commitIdAbbrev = commitIdAbbrev;
    }

    /**
     * Load the git information from the project's properties file.
     *
     * * If the properties file could not be loaded this throws an
     * {@code UncheckedIOException}.
     *
     * @return the git information
     * @see UncheckedIOException
     * @see Properties#load(java.io.InputStream)
     */
    public static GitInfo fromProperties() {
        Properties properties = new Properties();
        try {
            InputStream stream = Version.class.getClassLoader()
                    .getResourceAsStream("project.properties");
            properties.load(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new GitInfo(properties.getProperty("git.commit.id.abbrev"));
    }

    /**
     * Get the abbreviated commit ID.
     *
     * @return the commit ID
     */
    public String getCommitIdAbbrev() {
        return commitIdAbbrev;
    }

    @Override
    public String toString() {
        return commitIdAbbrev;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commitIdAbbrev == null) ? 0 : commitIdAbbrev.hashCode());
        return result;
    }

    @SuppressWarnings("checkstyle:NeedBraces")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GitInfo other = (GitInfo) obj;
        if (commitIdAbbrev == null) {
            if (other.commitIdAbbrev != null)
                return false;
        } else if (!commitIdAbbrev.equals(other.commitIdAbbrev))
            return false;
        return true;
    }
}
