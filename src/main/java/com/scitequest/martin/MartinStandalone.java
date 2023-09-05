package com.scitequest.martin;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.imagej.ImageJ;

/** Class containing the entrypoint for the standalone application. */
public final class MartinStandalone {

    private static final Logger log = Logger.getLogger("com.scitequest.martin");

    /** Prevent instantiation. */
    private MartinStandalone() {
    }

    /**
     * Entrypoint for the standalone application.
     *
     * @param args they are unused
     */
    public static void main(String[] args) {
        // Create an ImageJ handle
        ImageJ ij = new ImageJ();

        // Initialize an handler that is called if an unexpected exception happens
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler.withLogger(log));

        // Launch the plugin
        SwingUtilities.invokeLater(() -> {
            Control.standalone(ij);
        });
    }
}
