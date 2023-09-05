package com.scitequest.martin;

import java.awt.Frame;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.scitequest.martin.view.GuiUtils;

/** This class is used to handle a critical exception whereever it occurs. */
public final class GlobalExceptionHandler implements UncaughtExceptionHandler {

    private final Logger log;

    private GlobalExceptionHandler(Logger log) {
        this.log = log;
    }

    /**
     * Create a new global exception handler from the specified logger.
     *
     * @param log the logger to log the message to
     * @return the uncaught exception handler
     */
    public static GlobalExceptionHandler withLogger(Logger log) {
        return new GlobalExceptionHandler(log);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String msg = String.format("Critical error in thread '%s'", thread.getName());
        log.log(Level.SEVERE, msg, throwable);

        GuiUtils.showLogfileErrorDialog((Frame) null, "Unfortunately we encountered an critical error.\n\n"
                + " We would be really grateful if you could send us the 'martin.log' file"
                + " so that we can fix it.",
                "Critical Error");
        System.exit(1);
    }
}
