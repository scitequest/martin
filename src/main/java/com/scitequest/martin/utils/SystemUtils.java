package com.scitequest.martin.utils;

import java.nio.file.Path;
import java.util.Locale;

public final class SystemUtils {

    /**
     * Get the system locale if set, otherwise use the default locale.
     *
     * @return the locale
     */
    public static Locale getLocale() {
        String language = System.getProperty("user.language");
        String country = System.getProperty("user.country");
        if (language != null && country != null) {
            return new Locale(language, country);
        }
        return Locale.getDefault();
    }

    /**
     * Get the application installation path.
     *
     * <p>
     * This gets the path to the application content. If NOT starting from an
     * install using jpackage, the Maven target directory is returend for usage with
     * Maven development builds.
     * </p>
     *
     * Based on <a>https://stackoverflow.com/a/69141553</a> and
     * <a>https://docs.oracle.com/en/java/javase/16/jpackage/packaging-overview.html#GUID-DAE6A497-6E6F-4895-90CA-3C71AF052271</a>.
     *
     * @return path to the folder where the application content is stored
     */
    public static Path getAppContentPath() {
        boolean isJPackageBuild = System.getProperty("jpackage.app-path") != null;
        if (isJPackageBuild) {
            Path runtimePath = Path.of(System.getProperty("java.home"));
            Path contentPath = runtimePath.getParent();
            return contentPath;
        }
        // Return current working directory
        return Path.of(System.getProperty("user.dir")).resolve("target");
    }
}
