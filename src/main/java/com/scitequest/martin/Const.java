package com.scitequest.martin;

import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scitequest.martin.utils.SystemUtils;

import dev.dirs.ProjectDirectories;

/** Constants for the applications. */
public final class Const {
    /** The format used for each log entry that is written to the file. */
    public static final String LOG_FORMAT = "[%1$tF %1$tT] %4$s:%2$s: %5$s%6$s%n";

    /** Project directories specific to MARTin. */
    public static final ProjectDirectories PROJECT_DIRECTORIES = ProjectDirectories.from(
            "com", "Scite Quest", "MARTin");

    /** The resource bundle of all messages that uses the current system locale. */
    public static final ResourceBundle bundle = ResourceBundle.getBundle(
            "messages", SystemUtils.getLocale());

    /** Project version. */
    public static final Version VERSION = Version.fromProperties();

    /** Project Git information. */
    public static final GitInfo GIT_INFO = GitInfo.fromProperties();

    /** The epsilon tolerance allowed during an integrity check. */
    public static final double INTEGRITY_CHECK_EPSILON = 1e-6;

    public static final ObjectMapper mapper = new ObjectMapper();
}
