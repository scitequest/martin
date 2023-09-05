package com.scitequest.martin;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.scijava.log.LogLevel;
import org.scijava.log.LogService;

public final class IjLogHandler extends Handler {

    /** The reference to the ImageJ logging service. */
    private final LogService ijLogService;

    private IjLogHandler(LogService ijLogService) {
        this.ijLogService = ijLogService;
    }

    /**
     * Create a new adapter to also log via the ImageJ logging system.
     *
     * @param ijLogService the ImageJ log service
     * @return a new logging handler that logs to the ImageJ log service
     * @throws NullPointerException if the provided log service is null
     */
    public static IjLogHandler of(LogService ijLogService) throws NullPointerException {
        if (ijLogService == null) {
            throw new NullPointerException("Provided log service is null");
        }
        return new IjLogHandler(ijLogService);
    }

    @Override
    public void publish(LogRecord record) {
        if (ijLogService == null) {
            this.reportError("ImageJ log service no longer available",
                    null, ErrorManager.WRITE_FAILURE);
            return;
        }

        Level javaLevel = record.getLevel();
        int level = LogLevel.NONE;

        if (javaLevel.equals(Level.SEVERE)) {
            level = LogLevel.ERROR;
        } else if (javaLevel.equals(Level.WARNING)) {
            level = LogLevel.WARN;
        } else if (javaLevel.equals(Level.INFO)) {
            level = LogLevel.INFO;
        } else if (javaLevel.equals(Level.CONFIG)) {
            level = LogLevel.DEBUG;
        } else if (javaLevel.equals(Level.FINE)
                || javaLevel.equals(Level.FINER)
                || javaLevel.equals(Level.FINEST)) {
            level = LogLevel.TRACE;
        }

        ijLogService.log(level, record.getMessage(), record.getThrown());
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }
}
