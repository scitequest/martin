package com.scitequest.martin;

import java.util.Optional;

import javax.swing.SwingUtilities;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import net.imagej.ImageJ;
import net.imagej.patcher.LegacyInjector;

/**
 * Command implementing the MARTin plugin call.
 *
 * Note: We don't catch all errors here, since they show in ImageJ itself and we
 * don't want to override ImageJ's default behavior.
 */
@Plugin(type = Command.class, menuPath = "Plugins>MARTin")
public final class MartinCommand implements Command {

    static {
        LegacyInjector.preinit();
    }

    /** The ImageJ handle. */
    @Parameter
    private ImageJ ij;

    /** The currently open image. */
    @Parameter(required = false)
    private ImagePlus imagePlus;

    @Override
    public void run() {
        Optional<ImagePlus> iPlus = Optional.ofNullable(imagePlus);
        SwingUtilities.invokeLater(() -> {
            Control.plugin(ij, iPlus);
        });
    }
}
