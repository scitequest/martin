package com.scitequest.martin;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Value class representing Semantic Versioning Versioning 2.0.0.
 *
 * See: https://semver.org/ for more information.
 *
 * Note: this only supports version core at this moment.
 */
public final class Version implements Comparable<Version> {

    /** Major version number. */
    private final int major;
    /** Minor version number. */
    private final int minor;
    /** Patch version number. */
    private final int patch;

    private Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Create a new semver.
     *
     * Note: Throws an {@code IllegalArgumentException} if the version numbers are
     * not non-negative.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @return the semver
     */
    public static Version of(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers must non-negative");
        }
        return new Version(major, minor, patch);
    }

    /**
     * Create a new semver.
     *
     * Note: Throws an {@code IllegalArgumentException} if the version numbers are
     * not non-negative.
     *
     * @param version the version string
     * @return the semver
     */
    public static Version of(String version) {
        String[] split = version.trim().split("\\.");
        int major = Integer.parseInt(split[0]);
        int minor = Integer.parseInt(split[1]);
        int patch = Integer.parseInt(split[2]);

        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers must non-negative");
        }

        return new Version(major, minor, patch);
    }

    /**
     * Get the semver from the properties file created during build time.
     *
     * If the properties file could not be loaded this throws an
     * {@code UncheckedIOException}.
     *
     * @return the semver
     * @see UncheckedIOException
     * @see Properties#load(java.io.InputStream)
     */
    public static Version fromProperties() {
        Properties properties = new Properties();
        try {
            InputStream stream = Version.class.getClassLoader()
                    .getResourceAsStream("project.properties");
            properties.load(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Version.of(properties.getProperty("version"));
    }

    @Override
    public int compareTo(Version other) {
        int ret = Integer.compare(this.major, other.major);
        if (ret != 0) {
            return ret;
        }
        ret = Integer.compare(this.minor, other.minor);
        if (ret != 0) {
            return ret;
        }
        return Integer.compare(this.patch, other.patch);
    }

    /**
     * Test if the next version has no breaking changes.
     *
     * @param next the next version
     * @return true if the next version is compatible
     */
    public boolean isCompatible(Version next) {
        if (this.compareTo(next) > 0) {
            return false;
        }
        // Development case
        if (this.major == 0) {
            return this.minor == next.minor;
        }
        return this.major == next.major;
    }

    /**
     * Get the major version.
     *
     * @return the major version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Get the minor version.
     *
     * @return the minor version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Get the patch version.
     *
     * @return the patch version
     */
    public int getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + patch;
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
        Version other = (Version) obj;
        if (major != other.major)
            return false;
        if (minor != other.minor)
            return false;
        if (patch != other.patch)
            return false;
        return true;
    }
}
